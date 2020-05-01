package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.SearchProfileApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.ResolvedSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventType;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.JobAlertSubscribedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.JobAlertUnsubscribedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.JobAlert;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read.JobAdvertisementSearchRequestAssembler;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocumentIndexedEvent;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.percolator.PercolateQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

@Component("job-alert-percolator-event-listener")
public class JobAlertPercolatorEventListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobAlertPercolatorEventListener.class);

	private static final String DOC = "doc";

	private static final String PERCOLATE_QUERY_INDEX_NAME = "job-alert-percolator";

	private static final String PERCOLATE_QUERY_FIELD_NAME = "query";

	private static final String DELETE_JOBADVERTISEMENT_ID_FROM_MATCHED_IDS = "DELETE FROM search_profile_matched_id WHERE job_advertisement_id = ?";

	private final ClassPathResource mappingFile = new ClassPathResource("config/elasticsearch/mappings/job-alert-percolator.json");

	private final ClassPathResource settingsFile = new ClassPathResource("config/elasticsearch/settings/folding-analyzer.json");

	private static Set<DomainEventType> RELEVANT_EVENTS = new HashSet<>(
			Arrays.asList(
					JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_PUBLIC.getDomainEventType(),
					JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_RESTRICTED.getDomainEventType(),
					JobAdvertisementEvents.JOB_ADVERTISEMENT_CANCELLED.getDomainEventType(),
					JobAdvertisementEvents.JOB_ADVERTISEMENT_ARCHIVED.getDomainEventType(),
					JobAdvertisementEvents.JOB_ADVERTISEMENT_ADJOURNED_PUBLICATION.getDomainEventType()
			)
	);

	private final ObjectMapper objectMapper;

	private final ElasticsearchTemplate elasticsearchTemplate;

	private final SearchProfileRepository searchProfileRepository;

	private final SearchProfileApplicationService searchProfileApplicationService;

	private final JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

	private final JobAdvertisementSearchRequestAssembler jobAdvertisementSearchRequestAssembler;

	private final JdbcTemplate jdbcTemplate;

	public JobAlertPercolatorEventListener(ObjectMapper objectMapper,
	                                       ElasticsearchTemplate elasticsearchTemplate,
	                                       SearchProfileRepository searchProfileRepository,
	                                       SearchProfileApplicationService searchProfileApplicationService,
	                                       JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository,
	                                       JobAdvertisementSearchRequestAssembler jobAdvertisementSearchRequestAssembler, JdbcTemplate jdbcTemplate) {
		this.objectMapper = objectMapper;
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.searchProfileRepository = searchProfileRepository;
		this.searchProfileApplicationService = searchProfileApplicationService;
		this.jobAdvertisementElasticsearchRepository = jobAdvertisementElasticsearchRepository;
		this.jobAdvertisementSearchRequestAssembler = jobAdvertisementSearchRequestAssembler;
		this.jdbcTemplate = jdbcTemplate;
	}

	@PostConstruct
	public void createIndexIfAbsent() throws IOException {
		boolean exists = elasticsearchTemplate.getClient().admin()
				.indices()
				.prepareExists(PERCOLATE_QUERY_INDEX_NAME)
				.get()
				.isExists();
		if (exists) {
			return;
		}
		createIndex(getMapping(), buildSettings());
	}

	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener
	public void handle(JobAlertSubscribedEvent jobAlertSubscribedEvent) throws IOException {
		final SearchProfileId searchProfileId = jobAlertSubscribedEvent.getSearchProfile().getId();
		final ResolvedSearchProfileDto resolvedSearchProfileDto = searchProfileApplicationService.toResolvedSearchProfileDto(jobAlertSubscribedEvent.getSearchProfile());
		final NativeSearchQuery jobAdvertisementSearchQuery = jobAdvertisementSearchRequestAssembler.toJobAdvertisementSearchRequest(resolvedSearchProfileDto);


		final BoolQueryBuilder boolQueryBuilder = boolQuery()
				.must(jobAdvertisementSearchQuery.getQuery())
				.filter(jobAdvertisementSearchQuery.getFilter());

		addQueryToEntity(searchProfileId, boolQueryBuilder);
		addQueryToPercolatorIndex(searchProfileId, boolQueryBuilder);
	}

	@TransactionalEventListener
	public void handle(JobAlertUnsubscribedEvent jobAlertUnsubscribedEvent) {
		final SearchProfileId searchProfileId = jobAlertUnsubscribedEvent.getSearchProfile().getId();
		removeQueryFromPercolatorIndex(searchProfileId);
	}

	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener
	public void handle(JobAdvertisementDocumentIndexedEvent jobAdvertisementDocumentIndexedEvent) throws JsonProcessingException {
		final JobAdvertisementEvent originalEvent = jobAdvertisementDocumentIndexedEvent.getOriginalEvent();
		//TODO: fago check perfomance on this method.
		if (!RELEVANT_EVENTS.contains(originalEvent.getDomainEventType())) {
			return;
		}

		switch (originalEvent.getJobAdvertisementStatus()) {
			case PUBLISHED_PUBLIC:
			case PUBLISHED_RESTRICTED:
				process(originalEvent);
				break;

			case ARCHIVED:
			case CANCELLED:
			case REJECTED:
				remove(originalEvent);
				break;
		}
	}

	private void remove(JobAdvertisementEvent jobAdvertisementEvent) {
		LOGGER.debug("Removing JobAdvertisement with id: '{}' from matched Ids", jobAdvertisementEvent.getAggregateId().getValue());
		jdbcTemplate.update(DELETE_JOBADVERTISEMENT_ID_FROM_MATCHED_IDS, jobAdvertisementEvent.getAggregateId().getValue());
	}

	public void process(JobAdvertisementEvent jobAdvertisementEvent) throws JsonProcessingException {
		final JobAdvertisementId aggregateId = jobAdvertisementEvent.getAggregateId();

		Optional<JobAdvertisementDocument> jobAdvertisementDocument = jobAdvertisementElasticsearchRepository.findById(aggregateId.getValue());

		if(!jobAdvertisementDocument.isPresent()){
			LOGGER.debug("jobAdvertisementDocument with id: '{}' not found, not checking against percolator index", aggregateId.getValue());
			return;
		}

		byte[] jobAdBytes = objectMapper.writeValueAsBytes(jobAdvertisementDocument.get());

		QueryBuilder percolateQueryBuilder = new PercolateQueryBuilder(PERCOLATE_QUERY_FIELD_NAME, new BytesArray(jobAdBytes), XContentType.JSON);
		NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(percolateQueryBuilder);
		nativeSearchQuery.addIndices(PERCOLATE_QUERY_INDEX_NAME);

		List<SearchProfileId> matchedSearchProfileIds = elasticsearchTemplate.queryForIds(nativeSearchQuery)
				.stream()
				.map(SearchProfileId::new)
				.collect(Collectors.toList());

		LOGGER.debug("The JobAdvertisement with ID: '{}' matched a JobAlert and is added to the matched Ids", aggregateId);

		matchedSearchProfileIds.stream()
				.map(searchProfileApplicationService::getById)
				.forEach(searchProfile -> {
					LOGGER.debug("{}", searchProfile);
					final JobAlert jobAlert = searchProfile.getJobAlert();
					jobAlert.add(aggregateId);
					searchProfileRepository.save(searchProfile);;
				});
	}

	private void addQueryToEntity(SearchProfileId searchProfileId, BoolQueryBuilder boolQueryBuilder) {
		final Optional<SearchProfile> searchProfile = this.searchProfileRepository.findById(searchProfileId);
		if(searchProfile.isPresent()){
			final JobAlert jobAlert = searchProfile.get().getJobAlert();
			jobAlert.addQuery(boolQueryBuilder.toString());
		}
	}

	private void addQueryToPercolatorIndex(SearchProfileId searchProfileId, BoolQueryBuilder boolQueryBuilder) throws IOException {
		elasticsearchTemplate.getClient().prepareIndex(PERCOLATE_QUERY_INDEX_NAME, DOC, searchProfileId.getValue())
				.setSource(jsonBuilder()
						.startObject()
						.field(PERCOLATE_QUERY_FIELD_NAME, boolQueryBuilder)
						.endObject())
				.execute().actionGet();
	}

	private void removeQueryFromPercolatorIndex(SearchProfileId searchProfileId) {
		elasticsearchTemplate.getClient().prepareDelete(PERCOLATE_QUERY_INDEX_NAME, DOC, searchProfileId.getValue())
				.execute()
				.actionGet();
	}

	public Settings buildSettings() throws IOException {
		String settingsFileContent = IOUtils.toString(settingsFile.getInputStream());
		return Settings.builder()
				.loadFromSource(settingsFileContent, XContentType.JSON)
				.build();
	}

	public String getMapping() throws IOException {
		return IOUtils.toString(mappingFile.getInputStream());
	}

	public void createIndex(String mappingFileContent, Settings settings) {
		elasticsearchTemplate.getClient().admin()
				.indices()
				.prepareCreate(PERCOLATE_QUERY_INDEX_NAME)
				.setSettings(settings)
				.addMapping(DOC, mappingFileContent, XContentType.JSON)
				.get();
	}
}
