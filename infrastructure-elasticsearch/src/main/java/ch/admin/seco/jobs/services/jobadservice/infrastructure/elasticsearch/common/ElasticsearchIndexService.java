package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common;

import ch.admin.seco.jobs.services.jobadservice.domain.apiuser.ApiUserRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.apiuser.write.ApiUserDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.apiuser.write.ApiUserElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
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
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
public class ElasticsearchIndexService {

    private static final int BUFFER_SIZE = 100;
    public static final String INDEX_NAME_JOB_ADVERTISEMENT = "job-advertisements";
    public static final String INDEX_NAME_API_USER = "api-users";
    public static final String TYPE_DOC = "doc";

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexService.class);

    private final EntityManager entityManager;

    private JobAdvertisementRepository jobAdvertisementRepository;

    private JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

    private ApiUserRepository apiUserRepository;

    private ApiUserElasticsearchRepository apiUserElasticsearchRepository;

    private FavouriteItemRepository favouriteItemRepository;

    private FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository;

    private final ElasticsearchTemplate elasticsearchTemplate;

    public ElasticsearchIndexService(
            EntityManager entityManager,
            JobAdvertisementRepository jobAdvertisementRepository,
            JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository,
            ApiUserRepository apiUserRepository,
            ApiUserElasticsearchRepository apiUserElasticsearchRepository,
            FavouriteItemRepository favouriteItemRepository,
            FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository,
            ElasticsearchTemplate elasticsearchTemplate) {
        this.entityManager = entityManager;
        this.jobAdvertisementRepository = jobAdvertisementRepository;
        this.jobAdvertisementElasticsearchRepository = jobAdvertisementElasticsearchRepository;
        this.apiUserRepository = apiUserRepository;
        this.apiUserElasticsearchRepository = apiUserElasticsearchRepository;
        this.favouriteItemRepository = favouriteItemRepository;
        this.favouriteItemElasticsearchRepository = favouriteItemElasticsearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Async
    @Transactional(readOnly = true)
    public void reindexAll() {

        reindexForClass(
                JobAdvertisementDocument.class,
                jobAdvertisementRepository,
                new ElassticRepositoryAdapter<JobAdvertisementDocument>() {
                    @Override
                    public long count() {
                        return jobAdvertisementElasticsearchRepository.count();
                    }

                    @Override
                    public Iterable<JobAdvertisementDocument> saveAll(Iterable<JobAdvertisementDocument> entities) {
                        return jobAdvertisementElasticsearchRepository.saveAll(entities);
                    }
                },
                JobAdvertisementDocument::new,
                "streamAll"
        );

        reindexForClass(
                ApiUserDocument.class,
                apiUserRepository,
                new ElassticRepositoryAdapter<ApiUserDocument>() {
                    @Override
                    public long count() {
                        return apiUserElasticsearchRepository.count();
                    }

                    @Override
                    public Iterable<ApiUserDocument> saveAll(Iterable<ApiUserDocument> entities) {
                        return apiUserElasticsearchRepository.saveAll(entities);
                    }
                },
                ApiUserDocument::new,
                "streamAll"
        );

        // TODO REINDEX FavouriteItemDocument
        reindexForClass(
                FavouriteItemDocument.class,
                favouriteItemRepository,
                new ElassticRepositoryAdapter<FavouriteItemDocument>() {
                    @Override
                    public long count() {
                        return favouriteItemElasticsearchRepository.count();
                    }

                    @Override
                    public Iterable<FavouriteItemDocument> saveAll(Iterable<FavouriteItemDocument> entities) {
                        return favouriteItemElasticsearchRepository.saveAll(entities);
                    }
                },
                FavouriteItemDocument::new,
                "streamAll"
        );

        log.info("Elasticsearch: Successfully performed reindexing");
    }

    @SuppressWarnings("unchecked")
    <JPA, ELASTIC, ID extends Serializable> void reindexForClass(
            Class<ELASTIC> documentClass,
            JpaRepository<JPA, ID> jpaRepository,
            ElassticRepositoryAdapter<ELASTIC> elasticsearchRepository,
            Function<JPA, ELASTIC> entityToDocumentMapper,
            String methodName) {
        elasticsearchTemplate.deleteIndex(documentClass);
        elasticsearchTemplate.createIndex(documentClass);
        elasticsearchTemplate.putMapping(documentClass);

        if (jpaRepository.count() > 0) {
            reindexWithStream(jpaRepository, elasticsearchRepository,
                    entityToDocumentMapper, documentClass, methodName);
        }
        log.info("Elasticsearch: Indexed all rows for " + documentClass.getSimpleName());
    }

    private <JPA, ELASTIC, ID extends Serializable> void reindexWithStream(
            JpaRepository<JPA, ID> jpaRepository,
            ElassticRepositoryAdapter<ELASTIC> elasticsearchRepository,
            Function<JPA, ELASTIC> entityToDocumentMapper, Class entityClass, String methodName) {

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


    interface ElassticRepositoryAdapter<S> {

        long count();

        Iterable<S> saveAll(Iterable<S> entities);

    }
}
