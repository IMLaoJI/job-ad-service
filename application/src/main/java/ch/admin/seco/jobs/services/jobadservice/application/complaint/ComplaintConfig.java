package ch.admin.seco.jobs.services.jobadservice.application.complaint;

import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ComplaintProperties.class)
public class ComplaintConfig {

    private final ComplaintProperties complaintProperties;

    private final MailSenderService mailSenderService;

    private final MessageSource messageSource;


    public ComplaintConfig(ComplaintProperties complaintProperties, MailSenderService mailSenderService, MessageSource messageSource) {
        this.complaintProperties = complaintProperties;
        this.mailSenderService = mailSenderService;
        this.messageSource = messageSource;
    }

    @Bean
    public ComplaintApplicationService complaintApplicationService() {
        return new ComplaintApplicationService(this.mailSenderService, this.messageSource, this.complaintProperties);
    }
}
