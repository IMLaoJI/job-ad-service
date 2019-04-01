package ch.admin.seco.jobs.services.jobadservice.application.complaint;

import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ComplaintProperties.class)
public class ComplaintConfig {

    private final ComplaintProperties complaintProperties;

    private final MailSenderService mailSenderService;

    private final JobAdvertisementRepository jobAdvertisementRepository;

    public ComplaintConfig(ComplaintProperties complaintProperties, MailSenderService mailSenderService, JobAdvertisementRepository jobAdvertisementRepository) {
        this.complaintProperties = complaintProperties;
        this.mailSenderService = mailSenderService;
        this.jobAdvertisementRepository = jobAdvertisementRepository;
    }

    @Bean
    public ComplaintApplicationService complaintApplicationService() {
        return new ComplaintApplicationService(this.mailSenderService, this.complaintProperties, this.jobAdvertisementRepository);
    }
}
