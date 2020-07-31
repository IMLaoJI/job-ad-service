package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventPublisher;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvent;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Component("job-advertisement-event-listener")
public class IndexerEventListener {

    private static Logger LOG = LoggerFactory.getLogger(IndexerEventListener.class);

    private ElasticsearchTemplate elasticsearchTemplate;

    private JobAdvertisementRepository jobAdvertisementJpaRepository;

    public IndexerEventListener(
            ElasticsearchTemplate elasticsearchTemplate,
            JobAdvertisementRepository jobAdvertisementJpaRepository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.jobAdvertisementJpaRepository = jobAdvertisementJpaRepository;
    }

    @Timed
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void indexJobAdvertisement(JobAdvertisementEvent event) {
        Optional<JobAdvertisement> jobAdvertisementOptional = this.jobAdvertisementJpaRepository.findById(event.getAggregateId());
        if (jobAdvertisementOptional.isPresent()) {
            this.elasticsearchTemplate.index(createIndexQuery(new JobAdvertisementDocument(jobAdvertisementOptional.get())));
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
