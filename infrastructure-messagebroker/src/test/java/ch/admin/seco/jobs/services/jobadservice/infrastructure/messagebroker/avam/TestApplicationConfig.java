package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

@SpringBootApplication
public class TestApplicationConfig {

    @MockBean
    MailSenderService mailSenderService;

    @MockBean
    JobAdvertisementApplicationService jobAdvertisementApplicationService;

    @MockBean
    MessageBrokerChannels messageBrokerChannels;

    @MockBean
    JobCenterService jobCenterService;

    @MockBean
    JobAdvertisementRepository jobAdvertisementRepository;

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("mails/i18n/mail-messages");
        return source;
    }

}
