package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.JobAdvertisementAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import java.util.List;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.*;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageSystem.EXTERNAL;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageSystem.JOB_AD_SERVICE;

public class ExternalJobAdvertisementWriter implements ItemWriter<CreateJobAdvertisementDto> {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalJobAdvertisementWriter.class);

    private final MessageChannel output;

    public ExternalJobAdvertisementWriter(MessageChannel output) {
        this.output = output;
    }

    @Override
    public void write(List<? extends CreateJobAdvertisementDto> items) {
        LOG.debug("Send external JobAdvertisements ({}) to JobAd service", items.size());
        items.forEach(item -> send(item, item.getFingerprint()));
    }

    private void send(CreateJobAdvertisementDto createFromExternal, String key) {
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
