package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AvamConfig {

    private final MessageBrokerChannels messageBrokerChannels;

    private final JobAdvertisementApplicationService jobAdvertisementApplicationService;

    public AvamConfig(MessageBrokerChannels messageBrokerChannels, JobAdvertisementApplicationService jobAdvertisementApplicationService) {
        this.messageBrokerChannels = messageBrokerChannels;
        this.jobAdvertisementApplicationService = jobAdvertisementApplicationService;
    }

    @Bean
    public AvamService avamService() {
        return new AvamService(
                jobAdvertisementApplicationService,
                messageBrokerChannels.jobAdIntEventChannel()
        );
    }

}
