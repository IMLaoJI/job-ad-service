package ch.admin.seco.jobs.services.jobadservice;

import ch.admin.seco.jobs.services.jobadservice.application.BusinessLogger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.application.security.TestingCurrentUserContext;

@SpringBootApplication
public class TestApplicationConfig {

    @MockBean
    private MailSenderService mailSenderService;

    @MockBean
    private BusinessLogger businessLogger;

    @Bean
    public TestingCurrentUserContext testingCurrentUserContext() {
        return new TestingCurrentUserContext("junitest-1");
    }

}
