package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.events;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageSystem.JOB_AD_SERVICE;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import ch.admin.seco.alv.shared.spring.integration.IntegrationBasisConfig;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders;

@Configuration
class DomainEventGatewayFlowConfig {

	private final QueueChannel inputChannel;

	private final MessageChannel outputChannel;

	private final IntegrationBasisConfig integrationBasisConfig;

	DomainEventGatewayFlowConfig(DomainEventIntegrationChannels domainEventIntegrationChannels, MessageBrokerChannels messageBrokerChannels, IntegrationBasisConfig integrationBasisConfig) {
		this.inputChannel = domainEventIntegrationChannels.eventGatewayInputChannel();
		this.outputChannel = messageBrokerChannels.jobAdEventChannel();
		this.integrationBasisConfig = integrationBasisConfig;
	}

	@Bean
	IntegrationFlow eventGatewayIntegrationFlow() {
		return IntegrationFlows.from(this.inputChannel)
				.enrichHeaders(h -> h
						.role(IntegrationBasisConfig.DEFAULT_INTEGRATION_ROLE_NAME)
						.poller(this.integrationBasisConfig.pollerFactory().builder().retryAware(true).build())
						.header(MessageHeaders.SOURCE_SYSTEM, JOB_AD_SERVICE.name())
						.header(MessageHeaders.PAYLOAD_TYPE, JobAdvertisementEventDto.class.getSimpleName())
						.<JobAdvertisementEventDto>headerFunction(MessageHeaders.PARTITION_KEY, message -> message.getPayload().getId())
						.<JobAdvertisementEventDto>headerFunction(MessageHeaders.RELEVANT_ID, message -> message.getPayload().getId())
						.<JobAdvertisementEventDto>headerFunction(MessageHeaders.EVENT, message -> message.getPayload().getEventType())
				)
				.handle(this::send)
				.get();
	}

	private void send(Message<?> message) {
		outputChannel.send(message);
	}

}