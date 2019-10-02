package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.x28;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.ExternalJobAdvertisementArchiverService;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

//Todo: we need to review the architecture. This class it not related to the message broker. I think we should create an x28 adapter module.
@Service
public class X28JobAdvertisementArchiverService implements ExternalJobAdvertisementArchiverService {

    private static final Logger LOG = LoggerFactory.getLogger(X28JobAdvertisementArchiverService.class);

    private final JobAdvertisementRepository jobAdvertisementRepository;

    private final TransactionTemplate transactionTemplate;

    private final X28MessageLogRepository x28MessageLogRepository;

    public X28JobAdvertisementArchiverService(JobAdvertisementRepository jobAdvertisementRepository,
                                              TransactionTemplate transactionTemplate,
                                              X28MessageLogRepository x28MessageLogRepository) {
        this.jobAdvertisementRepository = jobAdvertisementRepository;
        this.transactionTemplate = transactionTemplate;
        this.x28MessageLogRepository = x28MessageLogRepository;
    }

    @Transactional(readOnly = true)
    public void archiveExternalJobAdvertisements() {
        final LocalDate today = TimeMachine.now().toLocalDate();

        if (isX28MessageReceived(today)) {
            archiveExternalJobAdsUpdatedBefore(today);
        } else {
            LOG.info("Archiving skipped because no x28 message was received. Please check the x28 import job!");
        }
    }

    private void archiveExternalJobAdsUpdatedBefore(final LocalDate executionDate) {
        final AtomicInteger totalCounter = new AtomicInteger(0);
        final AtomicInteger chunkCounter = new AtomicInteger(0);

        Predicate<JobAdvertisement> shouldArchive = jobAdvertisement ->
                this.x28MessageLogRepository.findById(jobAdvertisement.getFingerprint())
                        .map(X28MessageLog::getLastMessageDate)
                        .map(lastMessageDate -> lastMessageDate.isBefore(executionDate))
                        .orElse(Boolean.TRUE);


        Flux.fromStream(jobAdvertisementRepository.findAllPublishedExtern())
                .filter(shouldArchive)
                .buffer(JobAdvertisementRepository.HINT_FETCH_SIZE_VALUE)
                .doOnNext(this::archiveChunk)
                .doOnNext(jobs -> LOG.info("{} external jobads archived in chunk #{}", totalCounter.addAndGet(jobs.size()), chunkCounter.incrementAndGet()))
                .doOnSubscribe(subscription -> LOG.info("Start to archive external JobAdvertisements"))
                .doOnError(subscription -> LOG.error("Failed to archive external JobAdvertisements"))
                .doOnComplete(() -> LOG.info("Finished to archive external JobAdvertisements: {}", totalCounter.get()))
                .subscribe();
    }

    private void archiveChunk(List<JobAdvertisement> jobAdvertisements) {
        this.transactionTemplate.setName("archiving-tx");
        this.transactionTemplate.setReadOnly(false);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus s) {
                jobAdvertisements.forEach(JobAdvertisement::expirePublication);
            }
        });
    }

    private boolean isX28MessageReceived(LocalDate date) {
        return x28MessageLogRepository.countByLastMessageDateEquals(date) > 0;
    }
}
