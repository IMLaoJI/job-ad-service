package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.dlq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementTestDataProvider;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class DLQItemServiceTest {

    @Autowired
    private DLQItemService dlqItemService;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private DLQItemRepository dlqItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testHandleDLQMessage() throws IOException {
        // given
        JobAdvertisement testingJobAd = this.prepareJobAdvertisment();

        Message<JobAdvertisement> message = MessageBuilder.withPayload(testingJobAd)
                .setHeader(DLQItemService.X_EXCEPTION_MESSAGE, "Test-Exception-Message")
                .setHeader(DLQItemService.KAFKA_RECEIVED_TIMESTAMP, 1536573600000L)
                .setHeader(DLQItemService.X_EXCEPTION_STACKTRACE, "Test-Stacktrace")
                .setHeader(DLQItemService.X_ORIGINAL_TOPIC, "Test-Original-Topic")
                .setHeader(DLQItemService.RELEVANT_ID_KEY, testingJobAd.getId().getValue())
                .build();

        // when
        dlqItemService.handleEventDLQMessage(message);

        // then
        ArgumentCaptor<MailSenderData> mailSenderDataArgumentCaptor = ArgumentCaptor.forClass(MailSenderData.class);

        verify(mailSenderService, times(1)).send(mailSenderDataArgumentCaptor.capture());

        MailSenderData mailSenderData = mailSenderDataArgumentCaptor.getValue();
        assertThat(mailSenderData).isNotNull();
        assertThat(mailSenderData.getTemplateVariables()).isNotEmpty();

        List<DLQItem> dlqItems = dlqItemRepository.findByRelevantId(testingJobAd.getId().getValue());
        assertThat(dlqItems).hasSize(1);

        DLQItem dlqItem = dlqItems.get(0);
        assertThat(dlqItem.getErrorTime()).isEqualTo(LocalDateTime.ofInstant(Instant.ofEpochMilli(1536573600000L), ZoneId.systemDefault()));

        JobAdvertisement savedJobAdPayload = objectMapper.readValue(dlqItem.getPayload(), JobAdvertisement.class);
        assertThat(savedJobAdPayload).isEqualTo(testingJobAd);

        Map headers = objectMapper.readValue(dlqItem.getPayload(), Map.class);
        assertThat(headers).isNotEmpty();
    }


    private JobAdvertisement prepareJobAdvertisment() {
        JobAdvertisementTestDataProvider jobAdvertisementTestDataProvider = new JobAdvertisementTestDataProvider();
        return jobAdvertisementTestDataProvider.getTestData().get(0);
    }

}