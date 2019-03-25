package ch.admin.seco.jobs.services.jobadservice.infrastructure.mail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Ignore
public class MailIntegrationFlowConfigTest {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailSendingTaskRepository mailSendingTaskRepository;

    @Before
    public void setUp() {
        this.mailSendingTaskRepository.deleteAll();
    }

    @Test
    public void shouldSendEmailIfThereIsMailSendingTask() {
        // given
        MailSendingTask mailSendingTask = new MailSendingTask(MailDataTestFactory.createDummyMailSendingTaskData());

        // when
        mailSendingTaskRepository.save(mailSendingTask);

        // then
        await().atMost(10, SECONDS)
                .untilAsserted(() -> verify(mailSender, only()).send(any(MimeMessagePreparator.class)));
    }

    @Test
    public void testRetryTemplateWithBackoff() {
        // given
        MailSendingTask mailSendingTask = new MailSendingTask(MailDataTestFactory.createDummyMailSendingTaskData());

        doThrow(new RuntimeException("TEST")).when(mailSender).send(any(MimeMessagePreparator.class));

        // when
        mailSendingTaskRepository.save(mailSendingTask);

        // then
        await().atMost(7, SECONDS)
                .untilAsserted(() -> verify(mailSender, times(4)).send(any(MimeMessagePreparator.class)));
    }

    @TestConfiguration
    static class TestConfig {

        @MockBean
        JavaMailSender mailSender;
    }
}