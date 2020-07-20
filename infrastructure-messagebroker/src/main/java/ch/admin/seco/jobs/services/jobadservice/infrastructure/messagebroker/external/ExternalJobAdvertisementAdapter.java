package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.external;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external.ExternalCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StopWatch;

import javax.annotation.Nullable;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.CREATE_FROM_EXTERNAL_CONDITION;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.JOB_AD_INT_ACTION_CHANNEL;

@Service
public class ExternalJobAdvertisementAdapter {

    private final Logger LOG = LoggerFactory.getLogger(ExternalJobAdvertisementAdapter.class);

    private final JobAdvertisementApplicationService jobAdvertisementApplicationService;

    private final JobAdvertisementRepository jobAdvertisementRepository;

    private final TransactionTemplate transactionTemplate;

    private final ExternalMessageLogRepository externalMessageLogRepository;

    public ExternalJobAdvertisementAdapter(JobAdvertisementApplicationService jobAdvertisementApplicationService,
                                           JobAdvertisementRepository jobAdvertisementRepository,
                                           TransactionTemplate transactionTemplate,
                                           ExternalMessageLogRepository externalMessageLogRepository) {
        this.jobAdvertisementApplicationService = jobAdvertisementApplicationService;
        this.jobAdvertisementRepository = jobAdvertisementRepository;
        this.transactionTemplate = transactionTemplate;
        this.externalMessageLogRepository = externalMessageLogRepository;
    }

    @StreamListener(target = JOB_AD_INT_ACTION_CHANNEL, condition = CREATE_FROM_EXTERNAL_CONDITION)
    public void handleCreateFromExternalAction(ExternalCreateJobAdvertisementDto createFromExternal) {
        StopWatch stopWatch = new StopWatch();
        LOG.trace(".start handleCreateFromExternalAction [fingerprint = {}]", createFromExternal.getFingerprint());

        startTask("..", "logLastExternalMessageDate", stopWatch);
        logLastExternalMessageDate(createFromExternal.getFingerprint());
        stopTask(stopWatch);

        startTask("...", "determineJobAdvertisementId", stopWatch);
        final JobAdvertisementId jobAdvertisementId = determineJobAdvertisementId(createFromExternal.getStellennummerEgov(), createFromExternal.getStellennummerAvam());
        stopTask(stopWatch);

        if (jobAdvertisementId != null) {
            startTask("....", "jobAdvertisementApplicationService.enrichFromExtern", stopWatch);
            jobAdvertisementApplicationService.enrichFromExtern(jobAdvertisementId, createFromExternal.getFingerprint(), createFromExternal.getProfessionCodes());
            stopTask(stopWatch);
        } else {
            startTask(".....", "findJobAdvertisementIdByFingerprint", stopWatch);
            final JobAdvertisementId externalJobAdvertisementId = findJobAdvertisementIdByFingerprint(createFromExternal.getFingerprint());
            stopTask(stopWatch);

            if (externalJobAdvertisementId != null) {
                startTask("......", "jobAdvertisementApplicationService.updateFromExtern", stopWatch);
                jobAdvertisementApplicationService.updateFromExtern(externalJobAdvertisementId, createFromExternal);
                stopTask(stopWatch);
            } else {
                startTask(".......", "jobAdvertisementApplicationService.createFromExtern", stopWatch);
                jobAdvertisementApplicationService.createFromExtern(createFromExternal);
                stopTask(stopWatch);
            }
        }
        LOG.trace("........finished handleCreateFromExternalAction [fingerprint = {}] in {}", createFromExternal.getFingerprint(), stopWatch.getTotalTimeMillis());
    }

    @Nullable
    private JobAdvertisementId determineJobAdvertisementId(String stellennummerEgov, String stellennummerAvam) {

        if (stellennummerEgov != null) {
            JobAdvertisementId jobAdvertisementId = transactionTemplate.execute(status ->
                    jobAdvertisementRepository.findByStellennummerEgov(stellennummerEgov)
                            .map(JobAdvertisement::getId)
                            .orElse(null)
            );

            if (jobAdvertisementId != null) {
                return jobAdvertisementId;
            }
        }

        if (stellennummerAvam != null) {

            return transactionTemplate.execute(status ->
                    jobAdvertisementRepository.findByStellennummerAvam(stellennummerAvam)
                            .map(JobAdvertisement::getId)
                            .orElse(null)
            );
        }

        return null;
    }

    @Nullable
    private JobAdvertisementId findJobAdvertisementIdByFingerprint(String fingerprint) {
        return transactionTemplate.execute(status -> jobAdvertisementRepository.findByFingerprint(fingerprint)
                .map(JobAdvertisement::getId)
                .orElse(null)
        );
    }

    private void logLastExternalMessageDate(String fingerprint) {
        ExternalJobAdvertisementMessageLog externalJobAdvertisementMessageLog = new ExternalJobAdvertisementMessageLog(fingerprint, TimeMachine.now().toLocalDate());
        externalMessageLogRepository.save(externalJobAdvertisementMessageLog);
    }

    private void startTask(String prefix, String task, StopWatch stopWatch) {
        LOG.trace(prefix + " start: {}", task);
        stopWatch.start(task);
    }

    private void stopTask(StopWatch stopWatch) {
        stopWatch.stop();
        LOG.trace("finished: {} in {}", stopWatch.getLastTaskName(), stopWatch.getLastTaskTimeMillis());
    }
}
