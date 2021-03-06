package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.events;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventType;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import java.util.HashSet;
import java.util.Set;

class DomainEventSenderGateway {

	private static Logger LOG = LoggerFactory.getLogger(DomainEventSenderGateway.class);

	private final MessageChannel messageChannel;

	private final Set<DomainEventType> relevantEvents = new HashSet<>();

	DomainEventSenderGateway(DomainEventIntegrationChannels domainEventIntegrationChannels, Set<DomainEventType> relevantEvents) {
		this.messageChannel = domainEventIntegrationChannels.eventGatewayInputChannel();
		if (relevantEvents != null) {
			this.relevantEvents.addAll(relevantEvents);
		}
	}

	@EventListener
	public void handleJobAdvertisementEvent(JobAdvertisementEvent event) {
		if (!relevantEvents.contains(event.getDomainEventType())) {
			return;
		}
		if (isRestrictedJobAd(event)){
			return;
		}
		LOG.debug("EVENT caught for event type: '{}' for aggregate id: '{}'", event.getDomainEventType().getValue(), event.getAggregateId().getValue());
		JobAdvertisementEventDto jobAdvertisementEventDto = toJobAdvertisementEventDto(event);
		this.messageChannel.send(MessageBuilder.withPayload(jobAdvertisementEventDto).build());
	}

	private boolean isRestrictedJobAd(JobAdvertisementEvent event) {
		return event.getJobAdvertisementStatus().equals(JobAdvertisementStatus.PUBLISHED_RESTRICTED);
	}

	private JobAdvertisementEventDto toJobAdvertisementEventDto(JobAdvertisementEvent event) {
		return JobAdvertisementEventDto.from(
				event.getAggregateId().getValue(),
				event.getDomainEventType().getValue(),
				event.getJobAdvertisementStatus()
		);
	}

}
