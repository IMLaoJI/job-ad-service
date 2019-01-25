package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AvamConfig {

    private final MessageBrokerChannels messageBrokerChannels;

    private final JobAdvertisementApplicationService jobAdvertisementApplicationService;

    private final JobCenterService jobCenterService;

    private final AvamMailSender avamMailSender;

    public AvamConfig(MessageBrokerChannels messageBrokerChannels,
                      JobAdvertisementApplicationService jobAdvertisementApplicationService,
                      JobCenterService jobCenterService,
                      AvamMailSender avamMailSender) {
        this.messageBrokerChannels = messageBrokerChannels;
        this.jobAdvertisementApplicationService = jobAdvertisementApplicationService;
        this.jobCenterService = jobCenterService;
        this.avamMailSender = avamMailSender;
    }

    @Bean
    public AvamService avamService() {
        return new AvamService(
                jobAdvertisementApplicationService,
                messageBrokerChannels.jobAdIntEventChannel(),
                jobCenterService,
                avamMailSender);
    }

}
