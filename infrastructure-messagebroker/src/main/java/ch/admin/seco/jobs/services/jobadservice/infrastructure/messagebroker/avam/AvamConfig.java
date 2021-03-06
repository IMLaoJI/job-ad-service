package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;

@Configuration
public class AvamConfig {

	private final JobAdvertisementApplicationService jobAdvertisementApplicationService;

	private final JobCenterService jobCenterService;

	private final AvamMailSender avamMailSender;

	private final AvamIntegrationChannels avamIntegrationChannels;

	public AvamConfig(JobAdvertisementApplicationService jobAdvertisementApplicationService,
			JobCenterService jobCenterService,
			AvamMailSender avamMailSender, AvamIntegrationChannels avamIntegrationChannels) {
		this.jobAdvertisementApplicationService = jobAdvertisementApplicationService;
		this.jobCenterService = jobCenterService;
		this.avamMailSender = avamMailSender;
		this.avamIntegrationChannels = avamIntegrationChannels;
	}

	@Bean
	public AvamEventReceiverGateway avamService() {
		return new AvamEventReceiverGateway(
				jobAdvertisementApplicationService,
				jobCenterService,
				avamMailSender
		);
	}

	@Bean
	AvamDomainEventSenderGateway avamDomainEventSenderGateway() {
		return new AvamDomainEventSenderGateway(avamIntegrationChannels);
	}
}

