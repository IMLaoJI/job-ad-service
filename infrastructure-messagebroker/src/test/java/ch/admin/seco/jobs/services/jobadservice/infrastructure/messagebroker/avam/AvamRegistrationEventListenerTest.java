package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.PublicationFixture.testPublication;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import org.springframework.integration.channel.QueueChannel;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementCancelledEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobContentFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.OwnerFixture;


public class AvamRegistrationEventListenerTest {

	private static JobAdvertisementId ID = new JobAdvertisementId("id");

	private JobAdvertisement JOBADVERTISEMENT = new JobAdvertisement.Builder()
			.setId(ID)
			.setStatus(JobAdvertisementStatus.CREATED)
			.setSourceSystem(SourceSystem.JOBROOM)
			.setJobContent(JobContentFixture.of(ID).build())
			.setOwner(OwnerFixture.of(ID)
					.build())
			.setPublication(testPublication()
					.setPublicDisplay(true)
					.build())
			.setReportToAvam(true)
			.setReportingObligation(true)
			.build();

	private AvamDomainEventSenderGateway sut; //System Under Test

	private QueueChannel queueChannel;

	@Before
	public void setUp() {
		AvamIntegrationChannels avamIntegrationChannels = mock(AvamIntegrationChannels.class);
		queueChannel = mock(QueueChannel.class);
		when(avamIntegrationChannels.avamInputChannel()).thenReturn(queueChannel);
		sut = new AvamDomainEventSenderGateway(avamIntegrationChannels);
	}

	@Test
	public void shouldHandleCancel() {
		// GIVEN
		JobAdvertisementCancelledEvent event = new JobAdvertisementCancelledEvent(JOBADVERTISEMENT, SourceSystem.JOBROOM);

		// WHEN
		sut.handle(event);

		// THEN
		verify(queueChannel).send(any());
	}

	@Test
	public void shouldNotHandleCancel() {
		// GIVEN
		JobAdvertisementCancelledEvent event = new JobAdvertisementCancelledEvent(JOBADVERTISEMENT, SourceSystem.RAV);

		// WHEN
		sut.handle(event);

		// THEN
		verify(queueChannel, never()).send(any());
	}

}
