package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.event.EventListener;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementCancelledEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementInspectingEvent;

public class AvamDomainEventSenderGateway {

	private static Logger LOG = LoggerFactory.getLogger(AvamDomainEventSenderGateway.class);

	private final QueueChannel inputChannel;

	public AvamDomainEventSenderGateway(AvamIntegrationChannels avamIntegrationChannels) {
		this.inputChannel = avamIntegrationChannels.avamInputChannel();
	}

	@EventListener
	public void handle(JobAdvertisementInspectingEvent event) {
		LOG.debug("EVENT caught for AVAM: JOB_ADVERTISEMENT_INSPECTING for JobAdvertisementId: '{}'", event.getAggregateId().getValue());
		this.inputChannel.send(MessageBuilder
				.withPayload(new AvamTaskData(event.getAggregateId(), AvamTaskType.REGISTER))
				.build()
		);
	}

	@EventListener
	public void handle(JobAdvertisementCancelledEvent event) {
		if (shouldNotBeSend(event)) {
			return;
		}
		LOG.debug("EVENT caught for AVAM: JOB_ADVERTISEMENT_CANCELLED for JobAdvertisementId: '{}'", event.getAggregateId().getValue());
		this.inputChannel.send(
				MessageBuilder.withPayload(new AvamTaskData(event.getAggregateId(), AvamTaskType.DEREGISTER))
						.build()
		);
	}

	private boolean shouldNotBeSend(JobAdvertisementCancelledEvent event) {
		Object cancelledBy = event.getAttributesMap().get("cancelledBy");
		if (cancelledBy == null) {
			return false;
		}
		return cancelledBy.equals(SourceSystem.RAV);
	}

}
