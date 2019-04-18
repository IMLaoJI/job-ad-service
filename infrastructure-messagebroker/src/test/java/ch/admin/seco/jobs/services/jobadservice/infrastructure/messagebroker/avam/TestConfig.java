package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.NullChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels;

@SpringBootApplication
public class TestConfig {

	@MockBean
	MailSenderService mailSenderService;

	@MockBean
	JobAdvertisementApplicationService jobAdvertisementApplicationService;

	@MockBean
	JobCenterService jobCenterService;

	@MockBean
	JobAdvertisementRepository jobAdvertisementRepository;

	@Bean
	MessageBrokerChannels messageBrokerChannels() {
		return new MessageBrokerChannels() {
			@Override
			public SubscribableChannel jobAdIntActionChannel() {
				return new DirectChannel();
			}

			@Override
			public MessageChannel jobAdIntEventChannel() {
				return new NullChannel();
			}

			@Override
			public MessageChannel jobAdEventChannel() {
				return new NullChannel();
			}
		};
	}
}
