package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.eures;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.JOB_AD_EVENT_CHANNEL;

@Configuration
public class EuresConfig {

    @Bean
    public EuresMessagingService euresService(JobAdvertisementApplicationService jobAdvertisementApplicationService,  @Qualifier(JOB_AD_EVENT_CHANNEL) MessageChannel jobAdEventChannel) {
        return new EuresMessagingService(jobAdvertisementApplicationService, jobAdEventChannel);
    }
}
