package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.ApprovalDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.RejectionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import javax.validation.Valid;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.JobAdvertisementAction.APPROVE;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.JobAdvertisementAction.CANCEL;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.JobAdvertisementAction.CREATE_FROM_AVAM;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.JobAdvertisementAction.REJECT;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.*;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageSystem.AVAM;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageSystem.JOB_AD_SERVICE;

@EnableBinding(Source.class)
public class AvamSource {

    private static final Logger LOG = LoggerFactory.getLogger(AvamSource.class);

    private MessageChannel output;

    public AvamSource(MessageChannel output) {
        this.output = output;
    }

    public void approve(ApprovalDto approvalDto) {
        LOG.debug("Approve JobAdvertisement stellennummerAvam={}, stellennummerEgov={}", approvalDto.getStellennummerAvam(), approvalDto.getStellennummerEgov());
        output.send(MessageBuilder
                .withPayload(approvalDto)
                .setHeader(PARTITION_KEY, approvalDto.getStellennummerAvam())
                .setHeader(RELEVANT_ID, approvalDto.getStellennummerAvam())
                .setHeader(ACTION, APPROVE.name())
                .setHeader(SOURCE_SYSTEM, AVAM.name())
                .setHeader(TARGET_SYSTEM, JOB_AD_SERVICE.name())
                .setHeader(PAYLOAD_TYPE, approvalDto.getClass().getSimpleName())
                .build());
    }

    public void reject(RejectionDto rejectionDto) {
        LOG.debug("Reject JobAdvertisement stellennummerAvam={}, stellennummerEgov={}", rejectionDto.getStellennummerAvam(), rejectionDto.getStellennummerEgov());
        output.send(MessageBuilder
                .withPayload(rejectionDto)
                .setHeader(PARTITION_KEY, rejectionDto.getStellennummerEgov())
                .setHeader(RELEVANT_ID, rejectionDto.getStellennummerEgov())
                .setHeader(ACTION, REJECT.name())
                .setHeader(SOURCE_SYSTEM, AVAM.name())
                .setHeader(TARGET_SYSTEM, JOB_AD_SERVICE.name())
                .setHeader(PAYLOAD_TYPE, rejectionDto.getClass().getSimpleName())
                .build());
    }

    public void create(AvamCreateJobAdvertisementDto createJobAdvertisementFromAvamDto) {
        LOG.debug("Create JobAdvertisement stellennummerAvam={}", createJobAdvertisementFromAvamDto.getStellennummerAvam());
        output.send(MessageBuilder
                .withPayload(createJobAdvertisementFromAvamDto)
                .setHeader(PARTITION_KEY, createJobAdvertisementFromAvamDto.getStellennummerAvam())
                .setHeader(RELEVANT_ID, createJobAdvertisementFromAvamDto.getStellennummerAvam())
                .setHeader(ACTION, CREATE_FROM_AVAM.name())
                .setHeader(SOURCE_SYSTEM, AVAM.name())
                .setHeader(TARGET_SYSTEM, JOB_AD_SERVICE.name())
                .setHeader(PAYLOAD_TYPE, createJobAdvertisementFromAvamDto.getClass().getSimpleName())
                .build());
    }

    public void cancel(@Valid AvamCancellationDto cancellationDto) {
        LOG.debug("Cancel JobAdvertisement stellennummerAvam={}, stellennummerEgov={}", cancellationDto.getStellennummerAvam(), cancellationDto.getStellennummerEgov());
        output.send(MessageBuilder
                .withPayload(cancellationDto)
                .setHeader(PARTITION_KEY, cancellationDto.getStellennummerAvam())
                .setHeader(RELEVANT_ID, cancellationDto.getStellennummerAvam())
                .setHeader(ACTION, CANCEL.name())
                .setHeader(SOURCE_SYSTEM, AVAM.name())
                .setHeader(TARGET_SYSTEM, JOB_AD_SERVICE.name())
                .setHeader(PAYLOAD_TYPE, cancellationDto.getClass().getSimpleName())
                .build());
    }
}
