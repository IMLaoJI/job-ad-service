package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.events;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;

import ch.admin.seco.alv.shared.spring.integration.actuator.QueueChannelHealthProvider;

@Configuration
@EnableConfigurationProperties(DomainEventGatewayProperties.class)
class DomainEventGatewayConfig {

	private final DomainEventGatewayProperties domainEventGatewayProperties;

	private final DomainEventIntegrationChannels domainEventIntegrationChannels;

	DomainEventGatewayConfig(DomainEventGatewayProperties domainEventGatewayProperties, DomainEventIntegrationChannels domainEventIntegrationChannels) {
		this.domainEventGatewayProperties = domainEventGatewayProperties;
		this.domainEventIntegrationChannels = domainEventIntegrationChannels;
	}

	@Bean
	public DomainEventSenderGateway domainEventGateway() {
		return new DomainEventSenderGateway(this.domainEventIntegrationChannels, this.domainEventGatewayProperties.getRelevantEventTypes());
	}

	@Bean
	QueueChannelHealthProvider eventQueueChannelHealthProvider() {
		return new QueueChannelHealthProvider() {
			@Override
			public QueueChannel queueChannel() {
				return domainEventIntegrationChannels.eventGatewayInputChannel();
			}

			@Override
			public int threshold() {
				return 0;
			}
		};

	}

}
