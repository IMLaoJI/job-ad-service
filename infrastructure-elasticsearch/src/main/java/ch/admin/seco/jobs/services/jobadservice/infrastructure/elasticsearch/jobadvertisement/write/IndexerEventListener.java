package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write;

import ch.admin.seco.jobs.services.jobadservice.application.TraceHelper;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventPublisher;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.StopWatch;

import java.util.Optional;

@Component("job-advertisement-event-listener")
public class IndexerEventListener {
    private static Logger LOG = LoggerFactory.getLogger(IndexerEventListener.class);

    private final ElasticsearchTemplate elasticsearchTemplate;

    private final JobAdvertisementRepository jobAdvertisementJpaRepository;

    public IndexerEventListener(
            ElasticsearchTemplate elasticsearchTemplate,
            JobAdvertisementRepository jobAdvertisementJpaRepository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.jobAdvertisementJpaRepository = jobAdvertisementJpaRepository;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(JobAdvertisementEvent event) {
        indexJobAdvertisement(event);
    }

    private void indexJobAdvertisement(JobAdvertisementEvent event) {
        StopWatch stopWatch = TraceHelper.stopWatch();
        Optional<JobAdvertisement> jobAdvertisementOptional = this.jobAdvertisementJpaRepository.findById(event.getAggregateId());
        if (jobAdvertisementOptional.isPresent()) {

            TraceHelper.startTask(".", "index JobAdvertisement", stopWatch);
            this.elasticsearchTemplate.index(createIndexQuery(new JobAdvertisementDocument(jobAdvertisementOptional.get())));
            TraceHelper.stopTask(stopWatch);

            DomainEventPublisher.publish(new JobAdvertisementDocumentIndexedEvent(event));
        } else {
            LOG.warn("JobAdvertisement not found for the given id: {}", event.getAggregateId());
        }
    }

    private IndexQuery createIndexQuery(JobAdvertisementDocument jobAdvertisementDocument) {
        IndexQuery query = new IndexQuery();
        query.setObject(jobAdvertisementDocument);
        query.setId(jobAdvertisementDocument.getId());
        return query;
    }
}
