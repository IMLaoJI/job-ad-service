package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.ACTION;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.PARTITION_KEY;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.PAYLOAD_TYPE;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.RELEVANT_ID;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.SOURCE_SYSTEM;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.TARGET_SYSTEM;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageSystem.JOB_AD_SERVICE;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageSystem.EXTERNAL;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemWriter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external.ExternalCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.JobAdvertisementAction;

public class ExternalJobAdvertisementWriter implements ItemWriter<ExternalCreateJobAdvertisementDto> {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalJobAdvertisementWriter.class);

    private final MessageChannel output;

    public ExternalJobAdvertisementWriter(MessageChannel output) {
        this.output = output;
    }

    @Override
    public void write(List<? extends ExternalCreateJobAdvertisementDto> items) {
        LOG.debug("Send external JobAdvertisements ({}) to JobAd service", items.size());
        items.forEach(item -> send(item, item.getFingerprint()));
    }

    private void send(ExternalCreateJobAdvertisementDto createFromExternal, String key) {
        output.send(MessageBuilder
                .withPayload(createFromExternal)
                .setHeader(PARTITION_KEY, key)
                .setHeader(RELEVANT_ID, key)
                .setHeader(ACTION, JobAdvertisementAction.CREATE_FROM_EXTERNAL.name())
                .setHeader(SOURCE_SYSTEM, EXTERNAL.name())
                .setHeader(TARGET_SYSTEM, JOB_AD_SERVICE.name())
                .setHeader(PAYLOAD_TYPE, createFromExternal.getClass().getSimpleName())
                .build());
    }
}
