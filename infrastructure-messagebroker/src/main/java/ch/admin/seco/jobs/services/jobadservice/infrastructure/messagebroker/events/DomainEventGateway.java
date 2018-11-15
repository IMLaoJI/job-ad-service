package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.events;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventType;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashSet;
import java.util.Set;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.*;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageSystem.JOB_AD_SERVICE;

class DomainEventGateway {

    private static Logger LOG = LoggerFactory.getLogger(DomainEventGateway.class);

    private final MessageChannel messageChannel;

    private final Set<DomainEventType> relevantEvents = new HashSet<>();

    DomainEventGateway(MessageChannel messageChannel, Set<DomainEventType> relevantEvents) {
        this.messageChannel = messageChannel;
        if (relevantEvents != null) {
            this.relevantEvents.addAll(relevantEvents);
        }
    }

    @TransactionalEventListener
    public void handleJobAdvertisementEvent(JobAdvertisementEvent event) {
        if (!relevantEvents.contains(event.getDomainEventType())) {
            return;
        }
        LOG.debug("EVENT caught for event type: '{}' for aggregate id: '{}'", event.getDomainEventType().getValue(), event.getAggregateId().getValue());
        JobAdvertisementEventDto jobAdvertisementEventDto = toJobAdvertisementEventDto(event);
        this.messageChannel.send(
                MessageBuilder
                        .withPayload(jobAdvertisementEventDto)
                        .setHeader(PARTITION_KEY, jobAdvertisementEventDto.getId())
                        .setHeader(RELEVANT_ID, jobAdvertisementEventDto.getId())
                        .setHeader(EVENT, jobAdvertisementEventDto.getEventType())
                        .setHeader(SOURCE_SYSTEM, JOB_AD_SERVICE.name())
                        .setHeader(PAYLOAD_TYPE, JobAdvertisementEventDto.class.getSimpleName())
                        .build()
        );
    }

    private JobAdvertisementEventDto toJobAdvertisementEventDto(JobAdvertisementEvent event) {
        return JobAdvertisementEventDto.from(
                event.getAggregateId().getValue(),
                event.getDomainEventType().getValue(),
                event.getJobAdvertisementStatus()
        );
    }
}
