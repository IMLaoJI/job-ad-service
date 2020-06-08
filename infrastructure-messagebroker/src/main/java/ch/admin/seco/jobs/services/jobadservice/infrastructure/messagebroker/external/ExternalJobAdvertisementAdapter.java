package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.external;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external.ExternalCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nullable;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.CREATE_FROM_EXTERNAL_CONDITION;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.JOB_AD_INT_ACTION_CHANNEL;

@Service
public class ExternalJobAdvertisementAdapter {

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
        logLastExternalMessageDate(createFromExternal.getFingerprint());

        final JobAdvertisementId jobAdvertisementId = determineJobAdvertisementId(createFromExternal.getStellennummerEgov(), createFromExternal.getStellennummerAvam());

        if (jobAdvertisementId != null) {
            jobAdvertisementApplicationService.enrichFromExtern(jobAdvertisementId, createFromExternal.getFingerprint(), createFromExternal.getProfessionCodes());
        } else {
            final JobAdvertisementId externalJobAdvertisementId = findJobAdvertisementIdByFingerprint(createFromExternal.getFingerprint());

            if (externalJobAdvertisementId != null) {
                jobAdvertisementApplicationService.updateFromExtern(externalJobAdvertisementId, createFromExternal);
            } else {
                jobAdvertisementApplicationService.createFromExtern(createFromExternal);
            }
        }
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
}
