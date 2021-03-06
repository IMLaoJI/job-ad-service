package ch.admin.seco.jobs.services.jobadservice.application.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.CreateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.JobAlertDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.JobAlertMaxAmountReachedException;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.ResolvedSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileResultDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.UpdateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.CantonFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.SearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventMockUtils;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileEvents;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.Interval;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.CantonFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileIdFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class SearchProfileApplicationServiceTest {

	private DomainEventMockUtils domainEventMockUtils;

	@Autowired
	private SearchProfileRepository searchProfileRepository;

	@Autowired
	private SearchProfileApplicationService searchProfileApplicationService;

	@Before
	public void setUp() {
		this.domainEventMockUtils = new DomainEventMockUtils();
	}

	@After
	public void tearDown() {
		this.searchProfileRepository.deleteAll();
		this.domainEventMockUtils.clearEvents();
	}

	@Test
	public void testGetSearchProfile() {
		// given
		ResolvedSearchProfileDto createdResolvedSearchProfileDto = searchProfileApplicationService.createSearchProfile(getCreateSearchProfileDto(search_profile_01.id()));

		// when
		SearchProfileId searchProfileId = new SearchProfileId(createdResolvedSearchProfileDto.getId());

		// then
		assertThat(this.searchProfileApplicationService.getSearchProfile(searchProfileId).getName()).isEqualTo(createdResolvedSearchProfileDto.getName());
		domainEventMockUtils.assertSingleDomainEventPublished(SearchProfileEvents.SEARCH_PROFILE_CREATED.getDomainEventType());
	}

	@Test
	public void testGetSearchProfiles() {
		// given
		CreateSearchProfileDto createSearchProfileDto1 = getCreateSearchProfileDto(search_profile_01.id());
		CreateSearchProfileDto createSearchProfileDto2 = getCreateSearchProfileDto(search_profile_02.id());
		CreateSearchProfileDto createSearchProfileDto3 = getCreateSearchProfileDto(search_profile_03.id());
		CreateSearchProfileDto createSearchProfileDto4 = getCreateSearchProfileDto(search_profile_04.id());
		CreateSearchProfileDto createSearchProfileDto5 = getCreateSearchProfileDto(search_profile_05.id());
		CreateSearchProfileDto createSearchProfileDto6 = getCreateSearchProfileDto(search_profile_06.id());
		CreateSearchProfileDto createSearchProfileDto7 = getCreateSearchProfileDto(search_profile_07.id());
		CreateSearchProfileDto createSearchProfileDto8 = getCreateSearchProfileDto(search_profile_08.id());
		CreateSearchProfileDto createSearchProfileDto9 = getCreateSearchProfileDto(search_profile_09.id());
		CreateSearchProfileDto createSearchProfileDto10 = getCreateSearchProfileDto(search_profile_10.id());

		ResolvedSearchProfileDto createdResolvedSearchProfileDto1 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto1);
		ResolvedSearchProfileDto createdResolvedSearchProfileDto2 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto2);
		ResolvedSearchProfileDto createdResolvedSearchProfileDto3 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto3);
		ResolvedSearchProfileDto createdResolvedSearchProfileDto4 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto4);
		ResolvedSearchProfileDto createdResolvedSearchProfileDto5 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto5);
		ResolvedSearchProfileDto createdResolvedSearchProfileDto6 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto6);
		ResolvedSearchProfileDto createdResolvedSearchProfileDto7 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto7);
		ResolvedSearchProfileDto createdResolvedSearchProfileDto8 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto8);
		ResolvedSearchProfileDto createdResolvedSearchProfileDto9 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto9);
		ResolvedSearchProfileDto createdResolvedSearchProfileDto10 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto10);

		// when
		Page<SearchProfileResultDto> searchProfileResultPageOne = searchProfileApplicationService.getSearchProfiles(createSearchProfileDto1.getOwnerUserId(), 0, 5);
		Page<SearchProfileResultDto> searchProfileResultPageTwo = searchProfileApplicationService.getSearchProfiles(createSearchProfileDto1.getOwnerUserId(), 1, 5);

		// then
		domainEventMockUtils.assertMultipleDomainEventPublished(10, SearchProfileEvents.SEARCH_PROFILE_CREATED.getDomainEventType());

		assertThat(searchProfileResultPageOne).hasSize(5);
		assertThat(searchProfileResultPageOne.getContent().get(0).getId()).isEqualTo(createdResolvedSearchProfileDto10.getId());
		assertThat(searchProfileResultPageOne.getContent().get(1).getId()).isEqualTo(createdResolvedSearchProfileDto9.getId());
		assertThat(searchProfileResultPageOne.getContent().get(2).getId()).isEqualTo(createdResolvedSearchProfileDto8.getId());
		assertThat(searchProfileResultPageOne.getContent().get(3).getId()).isEqualTo(createdResolvedSearchProfileDto7.getId());
		assertThat(searchProfileResultPageOne.getContent().get(4).getId()).isEqualTo(createdResolvedSearchProfileDto6.getId());

		assertThat(searchProfileResultPageTwo).hasSize(5);
		assertThat(searchProfileResultPageTwo.getContent().get(0).getId()).isEqualTo(createdResolvedSearchProfileDto5.getId());
		assertThat(searchProfileResultPageTwo.getContent().get(1).getId()).isEqualTo(createdResolvedSearchProfileDto4.getId());
		assertThat(searchProfileResultPageTwo.getContent().get(2).getId()).isEqualTo(createdResolvedSearchProfileDto3.getId());
		assertThat(searchProfileResultPageTwo.getContent().get(3).getId()).isEqualTo(createdResolvedSearchProfileDto2.getId());
		assertThat(searchProfileResultPageTwo.getContent().get(4).getId()).isEqualTo(createdResolvedSearchProfileDto1.getId());
	}

	@Test
	public void testCreate() {
		// given
		CreateSearchProfileDto createSearchProfileDto = getCreateSearchProfileDto(search_profile_01.id());

		// when
		ResolvedSearchProfileDto createdResolvedSearchProfileDto = searchProfileApplicationService.createSearchProfile(createSearchProfileDto);

		// then
		Optional<SearchProfile> createdSearchProfile = searchProfileRepository.findById(new SearchProfileId(createdResolvedSearchProfileDto.getId()));
		assertThat(createdSearchProfile).isPresent();
		assertThat(createdSearchProfile.get().getName()).isEqualTo(createSearchProfileDto.getName());
		domainEventMockUtils.assertSingleDomainEventPublished(SearchProfileEvents.SEARCH_PROFILE_CREATED.getDomainEventType());
	}

	@Test
	public void testCreateWithExistingName() {
		// given
		CreateSearchProfileDto createSearchProfileDto1 = getCreateSearchProfileDto(search_profile_01.id());
		CreateSearchProfileDto createSearchProfileDto2 = getCreateSearchProfileDto(search_profile_01.id());

		// when
		searchProfileApplicationService.createSearchProfile(createSearchProfileDto1);

		// then
		assertThatThrownBy(() -> this.searchProfileApplicationService.createSearchProfile(createSearchProfileDto2))
				.isInstanceOf(SearchProfileNameAlreadyExistsException.class)
				.hasMessageContaining("SearchProfile with name " + createSearchProfileDto2.getName() + " already exists. " +
						"Please rename your new SearchProfile for ownerUserId=" + createSearchProfileDto2.getOwnerUserId() + ".");
	}

	@Test
	public void testUpdateSearchProfile() {
		// given
		CreateSearchProfileDto createSearchProfileDto = getCreateSearchProfileDto(search_profile_01.id());
		ResolvedSearchProfileDto createdResolvedSearchProfileDto = searchProfileApplicationService.createSearchProfile(createSearchProfileDto);
		domainEventMockUtils.assertSingleDomainEventPublished(SearchProfileEvents.SEARCH_PROFILE_CREATED.getDomainEventType());

		// when
		UpdateSearchProfileDto updateSearchProfileDto = new UpdateSearchProfileDto();
		updateSearchProfileDto.setName("Another Name");
		updateSearchProfileDto.setSearchFilter(createSearchProfileDto.getSearchFilter());
		List<CantonFilterDto> cantonFilterList = Arrays.asList(
				new CantonFilterDto().setCode("ZH").setName("Zürich"));
		updateSearchProfileDto.getSearchFilter().setCantonFilters(cantonFilterList);

		searchProfileApplicationService.updateSearchProfile(new SearchProfileId(createdResolvedSearchProfileDto.getId()), updateSearchProfileDto);

		// then
		Optional<SearchProfile> updatedSearchProfile = searchProfileRepository.findById(new SearchProfileId(createdResolvedSearchProfileDto.getId()));
		assertThat(updatedSearchProfile).isPresent();
		assertThat(updatedSearchProfile.get().getName()).isEqualTo(updateSearchProfileDto.getName());
		assertThat(updatedSearchProfile.get().getSearchFilter().getCantonFilters()).contains(new CantonFilter("Zürich", "ZH"));
	}

	@Test
	public void testDeleteSearchProfile() {
		// given
		ResolvedSearchProfileDto createdResolvedSearchProfileDto = searchProfileApplicationService.createSearchProfile(getCreateSearchProfileDto(search_profile_01.id()));
		SearchProfileId searchProfileId = new SearchProfileId(createdResolvedSearchProfileDto.getId());

		// when
		assertThat(this.searchProfileApplicationService.getSearchProfile(searchProfileId).getName()).isEqualTo(createdResolvedSearchProfileDto.getName());
		searchProfileApplicationService.deleteSearchProfile(searchProfileId);

		// then
		assertThatThrownBy(() -> this.searchProfileApplicationService.getSearchProfile(searchProfileId))
				.isInstanceOf(SearchProfileNotExitsException.class)
				.hasMessageContaining("Aggregate with ID " + searchProfileId.getValue() + " not found");
	}

	@Test
	public void testSubscribeToJobAlert() {
		//given
		searchProfileRepository.save(SearchProfileFixture.testSearchProfile(search_profile_01.id()));
		// when
		searchProfileApplicationService.subscribeToJobAlert(search_profile_01.id(),
				new JobAlertDto()
				.setInterval(Interval.INT_1DAY)
				.setEmail("test@example.org"));

		// then
		SearchProfile searchProfile = this.searchProfileRepository.findById(search_profile_01.id()).get();
		assertThat(searchProfile.getJobAlert()).isNotNull();
		assertThat(searchProfile.getJobAlert().getCreatedAt()).isNotNull();
		assertThat(searchProfile.getJobAlert().getNextReleaseAt()).isNotNull();
		assertThat(searchProfile.getJobAlert().getUpdatedAt()).isNotNull();
		assertThat(searchProfile.getJobAlert().getInterval()).isEqualTo(Interval.INT_1DAY);
		assertThat(searchProfile.getJobAlert().getMatchedJobAdvertisementIds()).isEqualTo(Collections.emptySet());
		assertThat(searchProfile.getJobAlert().getEmail()).isEqualTo("test@example.org");
		assertThat(searchProfile.getJobAlert().getLanguage()).isEqualTo("en");
		domainEventMockUtils.assertSingleDomainEventPublished(SearchProfileEvents.JOBALERT_SUBSCRIBED_EVENT.getDomainEventType());
	}


	@Test
	public void testJobAlertsLimitedTo5() {

		//given
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJunitOwner(search_profile_01.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJunitOwner(search_profile_02.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJunitOwner(search_profile_03.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJunitOwner(search_profile_04.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJunitOwner(search_profile_05.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJunitOwner(search_profile_06.id()));

		// when
		final JobAlertDto jobAlertDto = new JobAlertDto()
				.setInterval(Interval.INT_1DAY)
				.setEmail("test@example.org");
		searchProfileApplicationService.subscribeToJobAlert(search_profile_01.id(), jobAlertDto);
		searchProfileApplicationService.subscribeToJobAlert(search_profile_02.id(), jobAlertDto);
		searchProfileApplicationService.subscribeToJobAlert(search_profile_03.id(), jobAlertDto);
		searchProfileApplicationService.subscribeToJobAlert(search_profile_04.id(), jobAlertDto);
		searchProfileApplicationService.subscribeToJobAlert(search_profile_05.id(), jobAlertDto);

		// then
		assertThatThrownBy(() -> searchProfileApplicationService.subscribeToJobAlert(search_profile_06.id(), jobAlertDto))
				.isInstanceOf(JobAlertMaxAmountReachedException.class)
				.hasMessageContaining("Maximum Amount of JobAlerts reached!");
	}

	@Test
	public void testReleaseJobAlerts() {
		//given
		// nextReleaseDate in the Past - Should be released
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJobAlertToBeReleased(search_profile_01.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJobAlertToBeReleased(search_profile_02.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJobAlertToBeReleased(search_profile_03.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJobAlertToBeReleased(search_profile_04.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJobAlertToBeReleased(search_profile_05.id()));

		// nextReleaseDate in the Future - Should not be released
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJobAlert(search_profile_06.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJobAlert(search_profile_07.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJobAlert(search_profile_08.id()));


		// when
		searchProfileApplicationService.releaseJobAlerts();

		// then
		domainEventMockUtils.assertMultipleDomainEventPublished(5, SearchProfileEvents.JOBALERT_RELEASED_EVENT.getDomainEventType());
	}

	@Test
	public void testUnsubscribeJobAlert() {
		// given
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJobAlert(search_profile_01.id()));

		// when
		searchProfileApplicationService.unsubscribeFromJobAlert(search_profile_01.id());

		// then
		SearchProfile searchProfile = this.searchProfileRepository.findById(search_profile_01.id()).get();
		assertThat(searchProfile.getJobAlert().getMatchedJobAdvertisementIds()).isEmpty();
		assertThat(searchProfile.getJobAlert().getInterval()).isNull();
		assertThat(searchProfile.getJobAlert().getUpdatedAt()).isNull();
		assertThat(searchProfile.getJobAlert().getNextReleaseAt()).isNull();
		assertThat(searchProfile.getJobAlert().getCreatedAt()).isNull();
		assertThat(searchProfile.getJobAlert().getQuery()).isNull();
		domainEventMockUtils.assertSingleDomainEventPublished(SearchProfileEvents.JOBALERT_UNSUBSCRIBED_EVENT.getDomainEventType());
	}

	private CreateSearchProfileDto getCreateSearchProfileDto(SearchProfileId searchProfileId) {
		SearchProfile searchProfile = SearchProfileFixture.testSearchProfile(searchProfileId);
		return new CreateSearchProfileDto(
				searchProfile.getName()
				, searchProfile.getOwnerUserId()
				, SearchFilterDto.toDto(searchProfile.getSearchFilter()));
	}

	@Test
	public void testJobAlertHousekeeping() {

		//given
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJobAlert(search_profile_01.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJobAlert(search_profile_02.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJobAlert(search_profile_03.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJobAlert(search_profile_04.id()));
		searchProfileRepository.save(SearchProfileFixture.testSearchProfileWithJobAlert(search_profile_05.id()));

		// when
		final JobAlertDto jobAlertDto = new JobAlertDto()
				.setInterval(Interval.INT_1DAY)
				.setEmail("test@example.org");
		searchProfileApplicationService.subscribeToJobAlert(search_profile_01.id(), jobAlertDto);
		searchProfileApplicationService.subscribeToJobAlert(search_profile_02.id(), jobAlertDto);
		searchProfileApplicationService.subscribeToJobAlert(search_profile_03.id(), jobAlertDto);
		searchProfileApplicationService.subscribeToJobAlert(search_profile_04.id(), jobAlertDto);
		searchProfileApplicationService.subscribeToJobAlert(search_profile_05.id(), jobAlertDto);

		// then

		assertEquals(this.searchProfileRepository.countAllWithJobAlerts(), Integer.valueOf(5));

		searchProfileApplicationService.jobAlertHousekeeping(TimeMachine.now().minusDays(1L));
		assertEquals(this.searchProfileRepository.countAllWithJobAlerts(), Integer.valueOf(5));

		searchProfileApplicationService.jobAlertHousekeeping(TimeMachine.now());
		assertEquals(this.searchProfileRepository.countAllWithJobAlerts(), Integer.valueOf(0));
	}
}
