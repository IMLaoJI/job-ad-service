package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.store.ChannelMessageStore;
import org.springframework.integration.store.MessageGroupQueue;

@Configuration
class AvamIntegrationChannels {

	private final ChannelMessageStore channelMessageStore;

	AvamIntegrationChannels(ChannelMessageStore channelMessageStore) {
		this.channelMessageStore = channelMessageStore;
	}

	@Bean
	QueueChannel avamInputChannel() {
		return MessageChannels.queue("avam-input-channel", new MessageGroupQueue(this.channelMessageStore, "AVAM_GROUP")).get();
	}

}