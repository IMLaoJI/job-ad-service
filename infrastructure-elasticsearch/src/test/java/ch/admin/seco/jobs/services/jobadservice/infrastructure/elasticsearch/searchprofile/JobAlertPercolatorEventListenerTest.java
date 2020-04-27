package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.SearchProfileApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.ResolvedSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.ResolvedSearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.JobAlertSubscribedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.JobAlertUnsubscribedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileIdFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.JobAlert;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read.JobAdvertisementSearchQueryBuilder;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read.JobAdvertisementSearchRequestAssembler;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocumentIndexedEvent;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
import org.awaitility.Duration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Optional;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.EmploymentFixture.testEmployment;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobContentFixture.testJobContent;
import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileFixture.prepareSearchFilter;
import static org.awaitility.Awaitility.await;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JobAlertPercolatorEventListenerTest {

	@Autowired
	private JobAlertPercolatorEventListener jobAlertPercolatorEventListener;

	@MockBean
	private SearchProfileRepository searchProfileRepository;

	@MockBean
	private JdbcTemplate jdbcTemplate;

	@MockBean
	private SearchProfileApplicationService searchProfileApplicationService;

	@Autowired
	JobAdvertisementRepository jobAdvertisementRepository;

	@MockBean
	JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Mock
	JobAlert jobAlert;

	@MockBean
	JobAdvertisementSearchRequestAssembler jobAdvertisementSearchRequestAssembler;

	@Autowired
	JobAdvertisementSearchQueryBuilder jobAdvertisementSearchQueryBuilder;

	private static final String PERCOLATOR_INDEX_NAME = "job-alert-percolator";

	@Before
	public void setUp() throws IOException {
		elasticsearchTemplate.deleteIndex(PERCOLATOR_INDEX_NAME);
		jobAlertPercolatorEventListener.createIndexIfAbsent();
	}

	@Test
	public void createIndexIfAbsent() throws IOException {
		//given

		//when
		jobAlertPercolatorEventListener.createIndexIfAbsent();

		//then
		assertTrue(elasticsearchTemplate.indexExists(PERCOLATOR_INDEX_NAME));
	}

	@Test
	public void handleJobAlertEnabledEvent() throws IOException {
		//given
		JobAlertSubscribedEvent jobAlertSubscribedEvent = new JobAlertSubscribedEvent(SearchProfileFixture.testSearchProfileWithJobAlert(SearchProfileIdFixture.search_profile_01.id()));
		JobAlertSubscribedEvent jobAlertSubscribedEvent2 = new JobAlertSubscribedEvent(SearchProfileFixture.testSearchProfileWithJobAlert(SearchProfileIdFixture.search_profile_02.id()));
		JobAlertSubscribedEvent jobAlertSubscribedEvent3 = new JobAlertSubscribedEvent(SearchProfileFixture.testSearchProfileWithJobAlert(SearchProfileIdFixture.search_profile_03.id()));
		Mockito.when(searchProfileApplicationService.toResolvedSearchProfileDto(any())).thenReturn(new ResolvedSearchProfileDto().setSearchFilter(new ResolvedSearchFilterDto()));

		Mockito.when(jobAdvertisementSearchRequestAssembler.toJobAdvertisementSearchRequest(any()))
				.thenReturn(new NativeSearchQueryBuilder()
						.withQuery(matchAllQuery())
						.withFilter(boolQuery().must(termsQuery("jobAdvertisement.jobContent.employment.permanent", true))).build());

		//when
		jobAlertPercolatorEventListener.handle(jobAlertSubscribedEvent);
		jobAlertPercolatorEventListener.handle(jobAlertSubscribedEvent2);
		jobAlertPercolatorEventListener.handle(jobAlertSubscribedEvent3);

		//then

		NativeSearchQuery build = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices(PERCOLATOR_INDEX_NAME).build();

		assertTrue(elasticsearchTemplate.indexExists(PERCOLATOR_INDEX_NAME));
		await().atMost(Duration.FIVE_SECONDS).untilAsserted(() -> assertEquals(3, this.elasticsearchTemplate.count(build)));
	}

	@Test
	public void handleJobAlertDisabledEvent() throws IOException {
		//given
		JobAlertSubscribedEvent jobAlertSubscribedEvent = new JobAlertSubscribedEvent(SearchProfileFixture.testSearchProfileWithJobAlert(SearchProfileIdFixture.search_profile_01.id()));
		JobAlertSubscribedEvent jobAlertSubscribedEvent2 = new JobAlertSubscribedEvent(SearchProfileFixture.testSearchProfileWithJobAlert(SearchProfileIdFixture.search_profile_02.id()));
		JobAlertSubscribedEvent jobAlertSubscribedEvent3 = new JobAlertSubscribedEvent(SearchProfileFixture.testSearchProfileWithJobAlert(SearchProfileIdFixture.search_profile_03.id()));
		JobAlertUnsubscribedEvent jobAlertUnsubscribedEvent = new JobAlertUnsubscribedEvent(SearchProfileFixture.testSearchProfileWithJobAlert(SearchProfileIdFixture.search_profile_01.id()));
		JobAlertUnsubscribedEvent jobAlertUnsubscribedEvent2 = new JobAlertUnsubscribedEvent(SearchProfileFixture.testSearchProfileWithJobAlert(SearchProfileIdFixture.search_profile_02.id()));
		JobAlertUnsubscribedEvent jobAlertUnsubscribedEvent3 = new JobAlertUnsubscribedEvent(SearchProfileFixture.testSearchProfileWithJobAlert(SearchProfileIdFixture.search_profile_03.id()));
		Mockito.when(searchProfileApplicationService.toResolvedSearchProfileDto(any())).thenReturn(new ResolvedSearchProfileDto().setSearchFilter(new ResolvedSearchFilterDto()));

		Mockito.when(jobAdvertisementSearchRequestAssembler.toJobAdvertisementSearchRequest(any()))
				.thenReturn(new NativeSearchQueryBuilder()
						.withQuery(matchAllQuery())
						.withFilter(boolQuery().must(termsQuery("jobAdvertisement.jobContent.employment.permanent", true))).build());
		jobAlertPercolatorEventListener.handle(jobAlertSubscribedEvent);
		jobAlertPercolatorEventListener.handle(jobAlertSubscribedEvent2);
		jobAlertPercolatorEventListener.handle(jobAlertSubscribedEvent3);


		NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices(PERCOLATOR_INDEX_NAME).build();

		assertTrue(elasticsearchTemplate.indexExists(PERCOLATOR_INDEX_NAME));
		await().atMost(Duration.FIVE_SECONDS).untilAsserted(() -> assertEquals(3, this.elasticsearchTemplate.count(searchQuery)));

		//when
		jobAlertPercolatorEventListener.handle(jobAlertUnsubscribedEvent);
		jobAlertPercolatorEventListener.handle(jobAlertUnsubscribedEvent2);
		jobAlertPercolatorEventListener.handle(jobAlertUnsubscribedEvent3);


		//then
		await().atMost(Duration.FIVE_SECONDS).untilAsserted(() -> assertEquals(0, this.elasticsearchTemplate.count(searchQuery)));
	}


	@Test
	public void handleMatchingJobAdCreatedEvent() throws IOException {
		//given
		SearchProfile searchProfileWithMockedJobAlert = getSearchProfileWithMockedJobAlert();

		JobAlertSubscribedEvent jobAlertSubscribedEvent = new JobAlertSubscribedEvent(searchProfileWithMockedJobAlert);
		Mockito.when(searchProfileApplicationService.toResolvedSearchProfileDto(any())).thenReturn(new ResolvedSearchProfileDto().setSearchFilter(new ResolvedSearchFilterDto()));

		Mockito.when(jobAdvertisementSearchRequestAssembler.toJobAdvertisementSearchRequest(any()))
				.thenReturn(new NativeSearchQueryBuilder()
						.withQuery(matchAllQuery())
						.withFilter(boolQuery().must(termsQuery("jobAdvertisement.jobContent.employment.permanent", true))).build());
		jobAlertPercolatorEventListener.handle(jobAlertSubscribedEvent);
		NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices(PERCOLATOR_INDEX_NAME).build();
		assertTrue(elasticsearchTemplate.indexExists(PERCOLATOR_INDEX_NAME));
		await().atMost(Duration.ONE_SECOND).untilAsserted(() -> assertEquals(1, this.elasticsearchTemplate.count(searchQuery)));


		JobAdvertisement jobAd = JobAdvertisementFixture.testJobAdvertisement().setStatus(JobAdvertisementStatus.PUBLISHED_PUBLIC)
				.setJobContent(
						testJobContent()
								.setEmployment(testEmployment()
										.setPermanent(true)
										.build())
								.build())
				.build();
		JobAdvertisementDocument jobAdvertisementDocument = new JobAdvertisementDocument(jobAd);

		JobAdvertisementEvent jobAdvertisementEvent = new JobAdvertisementEvent(JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_PUBLIC, jobAd);
		JobAdvertisementDocumentIndexedEvent jobAdvertisementDocumentIndexedEvent = new JobAdvertisementDocumentIndexedEvent(jobAdvertisementEvent);
		Mockito.when(jobAdvertisementElasticsearchRepository.findById(jobAdvertisementEvent.getAggregateId().getValue())).thenReturn(Optional.of(jobAdvertisementDocument));
		Mockito.when(searchProfileApplicationService.getById(any())).thenReturn(searchProfileWithMockedJobAlert);

		//when
		jobAlertPercolatorEventListener.handle(jobAdvertisementDocumentIndexedEvent);

		//then
		verify(jobAlert, times(1)).add(any());
	}

	private SearchProfile getSearchProfileWithMockedJobAlert() {
		return SearchProfile.builder()
				.setId(SearchProfileIdFixture.search_profile_01.id())
				.setName("name-" + SearchProfileIdFixture.search_profile_01.id())
				.setOwnerUserId("job-seeker-1")
				.setSearchFilter(prepareSearchFilter())
				.setJobAlert(jobAlert)
				.build();
	}

	@Test
	public void handleNotMatchingJobAdCreatedEvent() throws IOException {
		//given
		SearchProfile searchProfileWithMockedJobAlert = getSearchProfileWithMockedJobAlert();

		JobAlertSubscribedEvent jobAlertSubscribedEvent = new JobAlertSubscribedEvent(searchProfileWithMockedJobAlert);
		Mockito.when(searchProfileApplicationService.toResolvedSearchProfileDto(any())).thenReturn(new ResolvedSearchProfileDto().setSearchFilter(new ResolvedSearchFilterDto()));
		Mockito.when(jobAdvertisementSearchRequestAssembler.toJobAdvertisementSearchRequest(any()))
				.thenReturn(new NativeSearchQueryBuilder()
						.withQuery(matchAllQuery())
						.withFilter(boolQuery()
								.must(termsQuery("jobAdvertisement.jobContent.employment.permanent", false)))
						.build());

		jobAlertPercolatorEventListener.handle(jobAlertSubscribedEvent);
		NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices(PERCOLATOR_INDEX_NAME).build();
		assertTrue(elasticsearchTemplate.indexExists(PERCOLATOR_INDEX_NAME));
		await().atMost(Duration.ONE_SECOND).untilAsserted(() -> assertEquals(1, this.elasticsearchTemplate.count(searchQuery)));

		JobAdvertisement jobAd = JobAdvertisementFixture.testJobAdvertisement().setStatus(JobAdvertisementStatus.PUBLISHED_PUBLIC)
				.setJobContent(testJobContent().setEmployment(testEmployment().setPermanent(true).build()).build()).build();
		JobAdvertisementDocument jobAdvertisementDocument = new JobAdvertisementDocument(jobAd);

		JobAdvertisementEvent jobAdvertisementEvent = new JobAdvertisementEvent(JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_PUBLIC, jobAd);
		JobAdvertisementDocumentIndexedEvent jobAdvertisementDocumentIndexedEvent = new JobAdvertisementDocumentIndexedEvent(jobAdvertisementEvent);
		Mockito.when(jobAdvertisementElasticsearchRepository.findById(jobAdvertisementEvent.getAggregateId().getValue())).thenReturn(Optional.of(jobAdvertisementDocument));
		Mockito.when(searchProfileApplicationService.getById(any())).thenReturn(searchProfileWithMockedJobAlert);

		//when
		jobAlertPercolatorEventListener.handle(jobAdvertisementDocumentIndexedEvent);

		//then
		verify(jobAlert, times(0)).add(any());
	}

	@Test
	public void handleJobAdCancelledEvent() throws IOException {
		//given
		SearchProfile searchProfileWithMockedJobAlert = getSearchProfileWithMockedJobAlert();
		JobAlertSubscribedEvent jobAlertSubscribedEvent = new JobAlertSubscribedEvent(searchProfileWithMockedJobAlert);
		Mockito.when(searchProfileApplicationService.toResolvedSearchProfileDto(any())).thenReturn(new ResolvedSearchProfileDto().setSearchFilter(new ResolvedSearchFilterDto()));
		Mockito.when(jobAdvertisementSearchRequestAssembler.toJobAdvertisementSearchRequest(any()))
				.thenReturn(new NativeSearchQueryBuilder()
						.withQuery(matchAllQuery())
						.withFilter(boolQuery().must(termsQuery("jobAdvertisement.jobContent.employment.permanent", true))).build());
		jobAlertPercolatorEventListener.handle(jobAlertSubscribedEvent);
		NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices(PERCOLATOR_INDEX_NAME).build();
		assertTrue(elasticsearchTemplate.indexExists(PERCOLATOR_INDEX_NAME));
		await().atMost(Duration.ONE_SECOND).untilAsserted(() -> assertEquals(1, this.elasticsearchTemplate.count(searchQuery)));
		JobAdvertisement jobAd = JobAdvertisementFixture.testJobAdvertisement().setStatus(JobAdvertisementStatus.PUBLISHED_PUBLIC)
				.setJobContent(
						testJobContent()
								.setEmployment(testEmployment()
										.setPermanent(true)
										.build())
								.build())
				.build();
		JobAdvertisementDocument jobAdvertisementDocument = new JobAdvertisementDocument(jobAd);
		JobAdvertisementEvent jobAdvertisementEvent = new JobAdvertisementEvent(JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_PUBLIC, jobAd);
		JobAdvertisementDocumentIndexedEvent jobAdvertisementDocumentIndexedEvent = new JobAdvertisementDocumentIndexedEvent(jobAdvertisementEvent);
		Mockito.when(jobAdvertisementElasticsearchRepository.findById(jobAdvertisementEvent.getAggregateId().getValue())).thenReturn(Optional.of(jobAdvertisementDocument));
		Mockito.when(searchProfileApplicationService.getById(any())).thenReturn(searchProfileWithMockedJobAlert);
		jobAlertPercolatorEventListener.handle(jobAdvertisementDocumentIndexedEvent);
		verify(jobAlert, times(1)).add(any());

		//when
		jobAd = JobAdvertisementFixture.testJobAdvertisement().setStatus(JobAdvertisementStatus.CANCELLED).build();
		JobAdvertisementEvent jobAdvertisementCancelledEvent = new JobAdvertisementEvent(JobAdvertisementEvents.JOB_ADVERTISEMENT_CANCELLED, jobAd);
		JobAdvertisementDocumentIndexedEvent jobAdvertisementDocumentCancelledEvent = new JobAdvertisementDocumentIndexedEvent(jobAdvertisementCancelledEvent);
		jobAlertPercolatorEventListener.handle(jobAdvertisementDocumentCancelledEvent);

		//then
		verify(jdbcTemplate, times(1)).update(anyString(), anyString());

	}

}


