package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents.JOB_ADVERTISEMENT_CANCELLED;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents.JOB_ADVERTISEMENT_INSPECTING;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.EVENT;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.PARTITION_KEY;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.PAYLOAD_TYPE;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.RELEVANT_ID;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.SOURCE_SYSTEM;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageHeaders.TARGET_SYSTEM;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageSystem.AVAM;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.messages.MessageSystem.JOB_AD_SERVICE;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.HeaderEnricherSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import ch.admin.seco.alv.shared.spring.integration.IntegrationBasisConfig;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels;

@Configuration
public class AvamIntegrationFlowConfig {

	private final Logger LOG = LoggerFactory.getLogger(AvamIntegrationFlowConfig.class);

	private final JobAdvertisementRepository jobAdvertisementRepository;

	private final IntegrationBasisConfig integrationBasisConfig;

	private final QueueChannel inputChannel;

	private final MessageChannel outputChannel;

	public AvamIntegrationFlowConfig(
			AvamIntegrationChannels avamIntegrationChannels,
			MessageBrokerChannels messageBrokerChannels,
			JobAdvertisementRepository jobAdvertisementRepository,
			IntegrationBasisConfig integrationBasisConfig) {
		this.jobAdvertisementRepository = jobAdvertisementRepository;
		this.integrationBasisConfig = integrationBasisConfig;
		this.inputChannel = avamIntegrationChannels.avamInputChannel();
		this.outputChannel = messageBrokerChannels.jobAdIntEventChannel();
	}

	@Bean
	public IntegrationFlow avamOutputGatewayFlow() {
		return IntegrationFlows
				.from(this.inputChannel)
				.transform(this::toJobAdvertisement, c -> c
						.role(IntegrationBasisConfig.DEFAULT_INTEGRATION_ROLE_NAME)
						.poller(integrationBasisConfig.pollerFactory().builder().retryAware(true).build()))
				.enrichHeaders(h -> h.headerFunction(EVENT, AvamIntegrationFlowConfig::extractEventHeader))
				.enrichHeaders(AvamIntegrationFlowConfig::applyMessageBrokerHeader)
				.handle(this::send)
				.get();
	}

	private void send(Message<?> message) {
		outputChannel.send(message);
	}

	private JobAdvertisement toJobAdvertisement(AvamTaskData avamTaskData) {
		LOG.debug("Find AVAM-task for JobAdversiementId: '{}'", avamTaskData.getJobAdvertisementId().getValue());
		Optional<JobAdvertisement> jobAdvertisement = jobAdvertisementRepository.findById(avamTaskData.getJobAdvertisementId());
		if (!jobAdvertisement.isPresent()) {
			LOG.error("Missing JobAdvertisementId '{}'. AVAM-Task will be removed", avamTaskData.getJobAdvertisementId().getValue());
			return null;
		}
		return jobAdvertisement.get();
	}

	private static void applyMessageBrokerHeader(HeaderEnricherSpec h) {
		h.header(SOURCE_SYSTEM, JOB_AD_SERVICE.name())
				.header(TARGET_SYSTEM, AVAM.name())
				.header(PAYLOAD_TYPE, JobAdvertisement.class.getSimpleName())
				.<JobAdvertisement>headerFunction(PARTITION_KEY, jobAdvertisementMessage -> {
					return jobAdvertisementMessage.getPayload().getId().getValue();
				})
				.<JobAdvertisement>headerFunction(RELEVANT_ID, jobAdvertisementMessage -> {
					return jobAdvertisementMessage.getPayload().getId().getValue();
				});
	}

	private static String extractEventHeader(Message<AvamTaskData> message) {
		AvamTaskType avamTaskType = message.getPayload().getType();
		switch (avamTaskType) {
			case REGISTER:
				return JOB_ADVERTISEMENT_INSPECTING.getDomainEventType().getValue();
			case DEREGISTER:
				return JOB_ADVERTISEMENT_CANCELLED.getDomainEventType().getValue();
			default:
				throw new UnsupportedOperationException(avamTaskType + " unknown");
		}
	}

}
