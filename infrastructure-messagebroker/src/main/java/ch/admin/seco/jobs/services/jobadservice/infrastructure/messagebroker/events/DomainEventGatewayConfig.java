package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.events;

import ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DomainEventGatewayProperties.class)
class DomainEventGatewayConfig {

    private final DomainEventGatewayProperties domainEventGatewayProperties;

    private final MessageBrokerChannels messageBrokerChannels;

    DomainEventGatewayConfig(DomainEventGatewayProperties domainEventGatewayProperties, MessageBrokerChannels messageBrokerChannels) {
        this.domainEventGatewayProperties = domainEventGatewayProperties;
        this.messageBrokerChannels = messageBrokerChannels;
    }

    @Bean
    public DomainEventGateway domainEventGateway() {
        return new DomainEventGateway(
                this.messageBrokerChannels.jobAdEventChannel(),
                this.domainEventGatewayProperties.getRelevantEventTypes()
        );
    }

}
