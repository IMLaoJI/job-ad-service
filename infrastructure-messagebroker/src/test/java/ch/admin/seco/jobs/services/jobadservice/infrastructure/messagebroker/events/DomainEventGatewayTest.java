package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.events;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.Collections;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture.testJobAdvertisement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DomainEventGatewayTest {

    private MessageChannel messageChannel;

    @Before
    public void setUp() {
        this.messageChannel = mock(MessageChannel.class);
    }

    @Test
    public void shouldHandleJobAdvertisementEvent() {
        //given
        DomainEventGateway domainEventGateway = new DomainEventGateway(
                messageChannel,
                Collections.singleton(JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_PUBLIC.getDomainEventType())
        );

        JobAdvertisementEvent jobAdvertisementEvent = new JobAdvertisementEvent(
                JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_PUBLIC,
                testJobAdvertisement().build()
        );

        // when
        domainEventGateway.handleJobAdvertisementEvent(jobAdvertisementEvent);

        // then
        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageChannel, times(1)).send(messageArgumentCaptor.capture());

        Message message = messageArgumentCaptor.getValue();
        Object payload = message.getPayload();
        assertThat(payload).isInstanceOf(JobAdvertisementEventDto.class);
        assertThat(message.getHeaders()).containsKey(MessageHeaders.EVENT);
    }

    @Test
    public void shouldIgnoreEvent() {
        //given
        DomainEventGateway domainEventGateway = new DomainEventGateway(
                messageChannel,
                Collections.singleton(JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_PUBLIC.getDomainEventType())
        );

        JobAdvertisementEvent ingoredEvent = new JobAdvertisementEvent(
                JobAdvertisementEvents.JOB_ADVERTISEMENT_CREATED,
                testJobAdvertisement().build()
        );

        // when
        domainEventGateway.handleJobAdvertisementEvent(ingoredEvent);

        // then
        verify(messageChannel, never()).send(any());
    }
}
