package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker;

import ch.admin.seco.jobs.services.jobadservice.application.ProfileRegistry;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.NullChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

@Configuration
public class MessageBrokerConfig {

    @Configuration
    @Profile('!' + ProfileRegistry.MESSAGE_BROKER_MOCK)
    @EnableBinding(MessageBrokerChannels.class)
    static class DefaultMessageBroker {
    }

    @Configuration
    @Profile(ProfileRegistry.MESSAGE_BROKER_MOCK)
    static class MockedMessageBroker {
        @Bean
        MessageBrokerChannels messageBrokerChannels() {
            return new MessageBrokerChannels() {

                @Override
                public SubscribableChannel jobAdIntActionChannel() {
                    return new DirectChannel();
                }

                @Override
                public SubscribableChannel userEventChannel() {
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
}
