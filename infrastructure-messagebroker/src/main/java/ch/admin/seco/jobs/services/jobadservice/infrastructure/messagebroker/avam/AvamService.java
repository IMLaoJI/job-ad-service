package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementAlreadyExistsException;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.AvamCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.ApprovalDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.CancellationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.RejectionDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import java.util.Map;

import static ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition.notNull;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents.JOB_ADVERTISEMENT_CANCELLED;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents.JOB_ADVERTISEMENT_INSPECTING;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.APPROVE_CONDITION;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.CANCEL_CONDITION;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.CREATE_FROM_AVAM_CONDITION;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.JOB_AD_INT_ACTION_CHANNEL;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.REJECT_CONDITION;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.EVENT;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.PARTITION_KEY;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.PAYLOAD_TYPE;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.RELEVANT_ID;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.SOURCE_SYSTEM;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.TARGET_SYSTEM;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageSystem.AVAM;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageSystem.JOB_AD_SERVICE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class AvamService {

    private final Logger LOG = LoggerFactory.getLogger(AvamService.class);

    private final MessageChannel jobAdEventChannel;

    private final JobCenterService jobCenterService;

    private final JobAdvertisementApplicationService jobAdvertisementApplicationService;

    private final AvamMailSenderService avamMailSenderService;

    public AvamService(JobAdvertisementApplicationService jobAdvertisementApplicationService,
                       MessageChannel jobAdEventChannel,
                       JobCenterService jobCenterService,
                       AvamMailSenderService avamMailSenderService) {
        this.jobAdvertisementApplicationService = jobAdvertisementApplicationService;
        this.jobAdEventChannel = jobAdEventChannel;
        this.jobCenterService = jobCenterService;
        this.avamMailSenderService = avamMailSenderService;
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
    public void handleCancelAction(AvamCancellationDto avamCancellationDto) {
        JobAdvertisementDto jobAdvertisementDto;
        if (isNotBlank(avamCancellationDto.getStellennummerEgov())) {
            jobAdvertisementDto = jobAdvertisementApplicationService.findByStellennummerEgov(avamCancellationDto.getStellennummerEgov());
        } else {
            jobAdvertisementDto = jobAdvertisementApplicationService.findByStellennummerAvam(avamCancellationDto.getStellennummerAvam());
        }
        if (jobAdvertisementDto == null) {
            LOG.info("Couldn't find the jobAdvertisement for AvamCancellationDto with stellennummerAvam {} ", avamCancellationDto.getStellennummerAvam());
            if (avamCancellationDto.getContactEmail() == null) {
                return;
            }
            final JobCenter jobCenter = jobCenterService.findJobCenterByCode(avamCancellationDto.getJobCenterCode());
            Map<String, Object> variables = avamMailSenderService.prepareTemplateVariables(avamCancellationDto, jobCenter);
            MailSenderData mailSenderData = avamMailSenderService.prepareMailSenderData(avamCancellationDto, variables);
            avamMailSenderService.send(mailSenderData);
        } else {
            CancellationDto cancellationDto = AvamCancellationDto.toDto(avamCancellationDto);
            jobAdvertisementApplicationService.cancel(
                    new JobAdvertisementId(jobAdvertisementDto.getId()),
                    cancellationDto,
                    null
            );
        }
    }

}
