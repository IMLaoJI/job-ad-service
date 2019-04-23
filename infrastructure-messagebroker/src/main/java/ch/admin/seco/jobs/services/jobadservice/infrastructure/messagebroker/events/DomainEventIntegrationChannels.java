package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.events;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.store.ChannelMessageStore;
import org.springframework.integration.store.MessageGroupQueue;

@Configuration
class DomainEventIntegrationChannels {

    private final ChannelMessageStore channelMessageStore;

    DomainEventIntegrationChannels(ChannelMessageStore channelMessageStore) {
        this.channelMessageStore = channelMessageStore;
    }

    @Bean
    QueueChannel eventGatewayInputChannel() {
        return MessageChannels.queue("event-gateway-input-channel", new MessageGroupQueue(this.channelMessageStore, "EVENT_GATEWAY_GROUP")).get();
    }
}