package ch.admin.seco.jobs.services.jobadservice.infrastructure.mail;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Locale;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class DefaultMailSenderServiceTest {

    @Autowired
    private MailSenderService mailSenderService;

    @Test
    public void testSend() {
        MailSenderData mailSenderData = new MailSenderData.Builder()
                .setSubject("Test-Subject")
                .setFrom("no-reply@example.com")
                .setTo("test@example.com")
                .setLocale(Locale.ENGLISH)
                .setTemplateName("TestMail.html")
                .build();
        this.mailSenderService.send(mailSenderData);
    }

    @Test
    public void testSendInvalidFails() {
        MailSenderData mailSenderData = new MailSenderData.Builder()
                .setSubject("Test-Subject")
                .setFrom("invalid.com")
                .setTo("invalid.com")
                .setLocale(Locale.ENGLISH)
                .setTemplateName("TestMail.html")
                .build();

        assertThatThrownBy(() -> this.mailSenderService.send(mailSenderData))
                .isInstanceOf(ConstraintViolationException.class);

    }

    @TestConfiguration
    static class TestConfig {

        @MockBean
        JavaMailSender mailSender;
    }
}