package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementAlreadyExistsException;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.AvamCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.ApprovalDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.RejectionDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.MessageSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition.notNull;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents.JOB_ADVERTISEMENT_CANCELLED;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents.JOB_ADVERTISEMENT_INSPECTING;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.*;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.*;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageSystem.AVAM;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageSystem.JOB_AD_SERVICE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class AvamService {

    private final Logger LOG = LoggerFactory.getLogger(AvamService.class);

    private final MessageChannel jobAdEventChannel;

    private final JobCenterService jobCenterService;

    private final JobAdvertisementApplicationService jobAdvertisementApplicationService;

    private final MailSenderService mailSenderService;

    private final MessageSource messageSource;


    private static final String JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_SUBJECT = "mail.jobAd.cancelled.subject_multilingual";
    private static final String JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_TEMPLATE = "JobAdCancelledMail_multilingual.html";
    private static final String DEFAULT_LANGUAGE = "";
    private static final String EMAIL_DELIMITER = "\\s*;\\s*";


    public AvamService(JobAdvertisementApplicationService jobAdvertisementApplicationService,
                       MessageChannel jobAdEventChannel,
                       JobCenterService jobCenterService,
                       MailSenderService mailSenderService,
                       MessageSource messageSource) {
        this.jobAdvertisementApplicationService = jobAdvertisementApplicationService;
        this.jobAdEventChannel = jobAdEventChannel;
        this.jobCenterService = jobCenterService;
        this.mailSenderService = mailSenderService;
        this.messageSource = messageSource;
    }

    void register(JobAdvertisement jobAdvertisement) {
        LOG.debug("Send through the message broker for action: REGISTER, JobAdvertisementId: '{}'", jobAdvertisement.getId().getValue());
        jobAdEventChannel.send(MessageBuilder
                .withPayload(jobAdvertisement)
                .setHeader(PARTITION_KEY, jobAdvertisement.getId().getValue())
                .setHeader(RELEVANT_ID, jobAdvertisement.getId().getValue())
                .setHeader(EVENT, JOB_ADVERTISEMENT_INSPECTING.getDomainEventType().getValue())
                .setHeader(SOURCE_SYSTEM, JOB_AD_SERVICE.name())
                .setHeader(TARGET_SYSTEM, AVAM.name())
                .setHeader(PAYLOAD_TYPE, jobAdvertisement.getClass().getSimpleName())
                .build());
    }

    void deregister(JobAdvertisement jobAdvertisement) {
        LOG.debug("Send through the message broker for action: DEREGISTER, JobAdvertisementId: '{}'", jobAdvertisement.getId().getValue());
        jobAdEventChannel.send(MessageBuilder
                .withPayload(jobAdvertisement)
                .setHeader(PARTITION_KEY, jobAdvertisement.getId().getValue())
                .setHeader(RELEVANT_ID, jobAdvertisement.getId().getValue())
                .setHeader(EVENT, JOB_ADVERTISEMENT_CANCELLED.getDomainEventType().getValue())
                .setHeader(SOURCE_SYSTEM, JOB_AD_SERVICE.name())
                .setHeader(TARGET_SYSTEM, AVAM.name())
                .setHeader(PAYLOAD_TYPE, jobAdvertisement.getClass().getSimpleName())
                .build());
    }

    @StreamListener(target = JOB_AD_INT_ACTION_CHANNEL, condition = APPROVE_CONDITION)
    public void handleApprovedAction(ApprovalDto approvalDto) {
        JobAdvertisementDto jobAdvertisementDto = jobAdvertisementApplicationService.getByStellennummerEgovOrAvam(approvalDto.getStellennummerEgov(), approvalDto.getStellennummerAvam());
        notNull(jobAdvertisementDto, "Couldn't find the jobAdvertisement for stellennummerEgov %s nor stellennummerAvam %s", approvalDto.getStellennummerEgov(), approvalDto.getStellennummerAvam());
        if (jobAdvertisementDto.getStatus() == JobAdvertisementStatus.INSPECTING) {
            jobAdvertisementApplicationService.approve(approvalDto);
        } else {
            jobAdvertisementApplicationService.adjourn(approvalDto);
        }
    }

    @StreamListener(target = JOB_AD_INT_ACTION_CHANNEL, condition = REJECT_CONDITION)
    public void handleRejectAction(RejectionDto rejectionDto) {
        jobAdvertisementApplicationService.reject(rejectionDto);
    }

    @StreamListener(target = JOB_AD_INT_ACTION_CHANNEL, condition = CREATE_FROM_AVAM_CONDITION)
    public void handleCreateAction(AvamCreateJobAdvertisementDto createJobAdvertisementFromAvamDto) {
        try {
            jobAdvertisementApplicationService.createFromAvam(createJobAdvertisementFromAvamDto);
        } catch (JobAdvertisementAlreadyExistsException e) {
            LOG.debug(e.getMessage());
        }
    }

    @StreamListener(target = JOB_AD_INT_ACTION_CHANNEL, condition = CANCEL_CONDITION)
    public void handleCancelAction(AvamCancellationDto cancellationDto) {
        JobAdvertisementDto jobAdvertisementDto;
        if (isNotBlank(cancellationDto.getStellennummerEgov())) {
            jobAdvertisementDto = jobAdvertisementApplicationService.findByStellennummerEgov(cancellationDto.getStellennummerEgov());
        } else {
            jobAdvertisementDto = jobAdvertisementApplicationService.findByStellennummerAvam(cancellationDto.getStellennummerAvam());
        }
        if (jobAdvertisementDto == null) {
            LOG.info("Couldn't find the jobAdvertisement for AvamCancellationDto with stellennummerAvam {} ", cancellationDto.getStellennummerAvam());
            if (cancellationDto.getContactEmail() == null) {
                return;
            }
            final JobCenter jobCenter = jobCenterService.findJobCenterByCode(cancellationDto.getJobCenterCode());
            Map<String, Object> variables = prepareTemplateVariables(cancellationDto, jobCenter);
            mailSenderService.send(prepareMailSenderData(cancellationDto, variables));
        } else {
            jobAdvertisementApplicationService.cancel(
                    new JobAdvertisementId(jobAdvertisementDto.getId()),
                    cancellationDto.getCancellationDate(),
                    cancellationDto.getCancellationCode(),
                    SourceSystem.RAV,
                    null
            );
        }
    }

    private Map<String, Object> prepareTemplateVariables(AvamCancellationDto cancellationDto, JobCenter jobCenter) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("stellennummer", cancellationDto.getStellennummerAvam());
        variables.put("jobCenter", jobCenter);
        return variables;
    }

    private MailSenderData prepareMailSenderData(AvamCancellationDto cancellationDto, Map<String, Object> variables) {
        return new MailSenderData.Builder()
                .setTo(parseMultipleAddresses(cancellationDto.getContactEmail()))
                .setSubject(messageSource.getMessage(JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_SUBJECT,
                        new Object[]{cancellationDto.getJobDescriptionTitle(), cancellationDto.getStellennummerAvam()}, new Locale(DEFAULT_LANGUAGE)))
                .setTemplateName(JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_TEMPLATE)
                .setTemplateVariables(variables)
                .setLocale(new Locale(DEFAULT_LANGUAGE))
                .build();
    }

    private static String[] parseMultipleAddresses(String emailAddress) {
        return (emailAddress == null) ? null : emailAddress.split(EMAIL_DELIMITER);
    }
}
