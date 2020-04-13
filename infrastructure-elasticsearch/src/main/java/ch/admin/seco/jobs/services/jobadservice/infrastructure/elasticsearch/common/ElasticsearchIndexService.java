package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common;

import ch.admin.seco.jobs.services.jobadservice.domain.apiuser.ApiUserRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.apiuser.write.ApiUserDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.apiuser.write.ApiUserElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.searchprofile.JobAlertPercolatorEventListener;
import org.elasticsearch.common.io.stream.InputStreamStreamInput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Flux;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Service
public class ElasticsearchIndexService {

	private static final int BUFFER_SIZE = 10000;
	public static final String INDEX_NAME_JOB_ADVERTISEMENT = "job-advertisements";
	public static final String INDEX_NAME_API_USER = "api-users";
	public static final String TYPE_DOC = "doc";
	private static final String JOB_ALERT_PERCOLATOR = "job-alert-percolator";
	private static final String PERCOLATE_QUERY_FIELD_NAME = "query";

	private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexService.class);

	private final EntityManager entityManager;

	private JobAdvertisementRepository jobAdvertisementRepository;

	private JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

	private ApiUserRepository apiUserRepository;

	private ApiUserElasticsearchRepository apiUserElasticsearchRepository;

	private FavouriteItemRepository favouriteItemRepository;

	private FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository;

	private final ElasticsearchTemplate elasticsearchTemplate;

	private final JobAlertPercolatorEventListener jobAlertPercolatorEventListener;

	private final SearchProfileRepository searchProfileRepository;


	public ElasticsearchIndexService(
			EntityManager entityManager,
			JobAdvertisementRepository jobAdvertisementRepository,
			JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository,
			ApiUserRepository apiUserRepository,
			ApiUserElasticsearchRepository apiUserElasticsearchRepository,
			FavouriteItemRepository favouriteItemRepository,
			FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository,
			ElasticsearchTemplate elasticsearchTemplate,
			JobAlertPercolatorEventListener jobAlertPercolatorEventListener,
			SearchProfileRepository searchProfileRepository) {
		this.entityManager = entityManager;
		this.jobAdvertisementRepository = jobAdvertisementRepository;
		this.jobAdvertisementElasticsearchRepository = jobAdvertisementElasticsearchRepository;
		this.apiUserRepository = apiUserRepository;
		this.apiUserElasticsearchRepository = apiUserElasticsearchRepository;
		this.favouriteItemRepository = favouriteItemRepository;
		this.favouriteItemElasticsearchRepository = favouriteItemElasticsearchRepository;
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.jobAlertPercolatorEventListener = jobAlertPercolatorEventListener;
		this.searchProfileRepository = searchProfileRepository;
	}

	@Async
	@Transactional(readOnly = true)
	public void reindexAll() {
		reindexForClass(
				JobAdvertisementDocument.class,
				jobAdvertisementRepository,
				new ElasticRepositoryAdapter<JobAdvertisementDocument>() {
					@Override
					public long count() {
						return jobAdvertisementElasticsearchRepository.count();
					}

					@Override
					public void saveAll(Iterable<JobAdvertisementDocument> entities) {
						jobAdvertisementElasticsearchRepository.saveAll(entities);
					}
				},
				JobAdvertisementDocument::new,
				"streamAll",
				true
		);

		reindexForClass(
				ApiUserDocument.class,
				apiUserRepository,
				new ElasticRepositoryAdapter<ApiUserDocument>() {
					@Override
					public long count() {
						return apiUserElasticsearchRepository.count();
					}

					@Override
					public void saveAll(Iterable<ApiUserDocument> entities) {
						apiUserElasticsearchRepository.saveAll(entities);
					}
				},
				ApiUserDocument::new,
				"streamAll",
				true
		);

		reindexForClass(
				FavouriteItemDocument.class,
				favouriteItemRepository,
				new ElasticRepositoryAdapter<FavouriteItemDocument>() {
					@Override
					public long count() {
						return favouriteItemElasticsearchRepository.count();
					}

					@Override
					public void saveAll(Iterable<FavouriteItemDocument> entities) {
						favouriteItemElasticsearchRepository.saveAll(entities);
					}
				},
				FavouriteItemDocument::new,
				"streamAll",
				false
		);
	}

	@Async
	@Transactional(readOnly = true)
	public void reindexPercolatorIndex() throws IOException {
		elasticsearchTemplate.deleteIndex(JOB_ALERT_PERCOLATOR);
		jobAlertPercolatorEventListener.createIndex(jobAlertPercolatorEventListener.getMapping(), jobAlertPercolatorEventListener.buildSettings());

		try {
			disableHibernateSecondaryCache();
			Integer total = searchProfileRepository.countAllWithJobAlerts();
			AtomicInteger index = new AtomicInteger(0);
			AtomicInteger counter = new AtomicInteger(0);
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();

			Stream<SearchProfile> searchProfileStream = searchProfileRepository.streamAllSearchProfilesWithJobAlerts();
			Flux.fromStream(searchProfileStream)
					.buffer(BUFFER_SIZE)
					.doOnNext(this::saveQueryForJobAlert)
					.doOnNext(searchProfiles ->
							log.info("Index JobAlertPercolator chunk #{}, {} / {}", index.incrementAndGet(), counter.addAndGet(searchProfiles.size()), total))
					.doOnComplete(stopWatch::stop)
					.subscribe(searchProfiles -> removeAllElementFromHibernatePrimaryCache());
		} catch (Exception e) {
			log.error("Failed to reindex JobAlertPercolator", e);
		}
		log.info("Elasticsearch: Indexed all rows for JobAlertPercolator");
	}


	@SuppressWarnings("unchecked")
	<JPA, ELASTIC, ID extends Serializable> void reindexForClass(
			Class<ELASTIC> documentClass,
			JpaRepository<JPA, ID> jpaRepository,
			ElasticRepositoryAdapter<ELASTIC> elasticsearchRepository,
			Function<JPA, ELASTIC> entityToDocumentMapper,
			String methodName, boolean resetIndex) {

		if (resetIndex) {
			elasticsearchTemplate.deleteIndex(documentClass);
			elasticsearchTemplate.createIndex(documentClass);
			elasticsearchTemplate.putMapping(documentClass);
		}

		if (jpaRepository.count() > 0) {
			reindexWithStream(
					jpaRepository,
					elasticsearchRepository,
					entityToDocumentMapper,
					documentClass,
					methodName
			);
		}
		log.info("Elasticsearch: Indexed all rows for " + documentClass.getSimpleName());
	}

	private <JPA, ELASTIC, ID extends Serializable> void reindexWithStream(
			JpaRepository<JPA, ID> jpaRepository,
			ElasticRepositoryAdapter<ELASTIC> elasticsearchRepository,
			Function<JPA, ELASTIC> entityToDocumentMapper,
			Class entityClass,
			String methodName) {
		try {
			disableHibernateSecondaryCache();
			Method m = jpaRepository.getClass().getMethod(methodName);
			long total = jpaRepository.count();
			AtomicInteger index = new AtomicInteger(0);
			AtomicInteger counter = new AtomicInteger(0);
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			Stream<JPA> stream = Stream.class.cast(m.invoke(jpaRepository));
			Flux.fromStream(stream)
					.map(entityToDocumentMapper)
					.buffer(BUFFER_SIZE)
					.doOnNext(elasticsearchRepository::saveAll)
					.doOnNext(jobs ->
							log.info("Index {} chunk #{}, {} / {}", entityClass.getSimpleName(), index.incrementAndGet(), counter.addAndGet(jobs.size()), total))
					.doOnComplete(stopWatch::stop)
					.doOnComplete(() -> log.info("Indexed {} of {} entities from {} in {} s", elasticsearchRepository.count(), jpaRepository.count(), entityClass.getSimpleName(), stopWatch.getTotalTimeSeconds()))
					.subscribe(jobs -> removeAllElementFromHibernatePrimaryCache());
		} catch (Exception e) {
			log.error("ReindexWithStream failed", e);
		}
	}

	private void disableHibernateSecondaryCache() {
		((Session) entityManager.getDelegate()).setCacheMode(CacheMode.IGNORE);
	}

	private void removeAllElementFromHibernatePrimaryCache() {
		entityManager.clear();
	}

	interface ElasticRepositoryAdapter<S> {

		long count();

		void saveAll(Iterable<S> entities);

	}

	private void saveQueryForJobAlert(List<SearchProfile> searchProfiles) {
		searchProfiles.forEach(searchProfile ->
		{
			try {
				final XContentBuilder wrappedSearchQuery = wrapQueryWithRootQueryField(searchProfile.getJobAlert().getQuery());
				elasticsearchTemplate.getClient().prepareIndex(JOB_ALERT_PERCOLATOR, TYPE_DOC, searchProfile.getId().getValue())
						.setSource(wrappedSearchQuery)
						.execute().actionGet();
			} catch (IOException e) {
				log.debug(e.getMessage());
			}
		});
	}

	private XContentBuilder wrapQueryWithRootQueryField(String savedSearchQuery) throws IOException {
		final StreamInput searchQuery = InputStreamStreamInput.wrap(savedSearchQuery.getBytes());
		return jsonBuilder()
				.startObject()
				.rawField(PERCOLATE_QUERY_FIELD_NAME, searchQuery, XContentType.JSON)
				.endObject();
	}

}
