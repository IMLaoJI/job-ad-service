package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.eures;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EuresJobAdvertisementEventListener {

    private static Logger LOG = LoggerFactory.getLogger(EuresJobAdvertisementEventListener.class);

    private final EuresMessagingService euresMessagingService;

    public EuresJobAdvertisementEventListener(EuresMessagingService euresMessagingService) {
        this.euresMessagingService = euresMessagingService;
    }

    @EventListener
    protected void handleJobAdvertisementEvent(JobAdvertisementEvent event) {
        LOG.debug("EVENT caught for EURES: '{}' for JobAdvertisementId: '{}'", event.getDomainEventType().getValue(), event.getAggregateId().getValue());
        euresMessagingService.sendEuresEventToMessageBroker(event.getAggregateId().getValue());
    }
}
