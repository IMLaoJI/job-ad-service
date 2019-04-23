package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.events;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture.testJobAdvertisement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents;

public class DomainEventSenderGatewayTest {

	private QueueChannel queueChannel;

	private DomainEventIntegrationChannels domainEventIntegrationChannels;

	private DomainEventSenderGateway sut;

	@Before
	public void setUp() {
		this.queueChannel = mock(QueueChannel.class);
		this.domainEventIntegrationChannels = mock(DomainEventIntegrationChannels.class);
		when(domainEventIntegrationChannels.eventGatewayInputChannel()).thenReturn(queueChannel);
	}

	@Test
	public void shouldHandleJobAdvertisementEvent() {
		//given
		DomainEventSenderGateway domainEventSenderGateway = new DomainEventSenderGateway(
				domainEventIntegrationChannels,
				Collections.singleton(JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_PUBLIC.getDomainEventType())
		);

		JobAdvertisementEvent jobAdvertisementEvent = new JobAdvertisementEvent(
				JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_PUBLIC,
				testJobAdvertisement().build()
		);

		// when
		domainEventSenderGateway.handleJobAdvertisementEvent(jobAdvertisementEvent);

		// then
		ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
		verify(queueChannel, times(1)).send(messageArgumentCaptor.capture());

		Message message = messageArgumentCaptor.getValue();
		Object payload = message.getPayload();
		assertThat(payload).isInstanceOf(JobAdvertisementEventDto.class);
	}

	@Test
	public void shouldIgnoreEvent() {
		//given
		DomainEventSenderGateway domainEventSenderGateway = new DomainEventSenderGateway(
				domainEventIntegrationChannels,
				Collections.singleton(JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_PUBLIC.getDomainEventType())
		);

		JobAdvertisementEvent ignoredEvent = new JobAdvertisementEvent(
				JobAdvertisementEvents.JOB_ADVERTISEMENT_CREATED,
				testJobAdvertisement().build()
		);

		// when
		domainEventSenderGateway.handleJobAdvertisementEvent(ignoredEvent);

		// then
		verify(queueChannel, never()).send(any());
	}
}
