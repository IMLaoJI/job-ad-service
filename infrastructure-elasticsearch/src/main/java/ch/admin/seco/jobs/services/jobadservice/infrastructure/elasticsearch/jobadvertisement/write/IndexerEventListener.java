package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Component("job-advertisement-event-listener")
public class IndexerEventListener {
    private static Logger LOG = LoggerFactory.getLogger(IndexerEventListener.class);

    private JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

    private JobAdvertisementRepository jobAdvertisementJpaRepository;

    public IndexerEventListener(
            JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository,
            JobAdvertisementRepository jobAdvertisementJpaRepository) {
        this.jobAdvertisementElasticsearchRepository = jobAdvertisementElasticsearchRepository;
        this.jobAdvertisementJpaRepository = jobAdvertisementJpaRepository;
    }

    @TransactionalEventListener
    public void handle(JobAdvertisementEvent event) {
        indexJobAdvertisement(event);
    }

    private void indexJobAdvertisement(JobAdvertisementEvent event) {
        Optional<JobAdvertisement> jobAdvertisementOptional = this.jobAdvertisementJpaRepository.findById(event.getAggregateId());
        if (jobAdvertisementOptional.isPresent()) {
            this.jobAdvertisementElasticsearchRepository.save(new JobAdvertisementDocument(jobAdvertisementOptional.get()));
        } else {
            LOG.warn("JobAdvertisement not found for the given id: {}", event.getAggregateId());
        }
    }
}
