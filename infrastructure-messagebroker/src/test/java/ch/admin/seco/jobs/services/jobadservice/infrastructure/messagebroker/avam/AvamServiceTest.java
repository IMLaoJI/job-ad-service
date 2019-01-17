package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.AvamCancellationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture.testJobAdvertisement;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.AvamCancellationDtoFixture.testAvamCancellationDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class AvamServiceTest {


    @Autowired
    private AvamService avamService;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private JobAdvertisementRepository jobAdvertisementRepository;

    @Autowired
    private MessageSource messageSource;

    @Test
    public void sendEmailIfCancellationDtoNotFound() throws IOException {
        // given
        JobAdvertisement jobAdvertisement = testJobAdvertisement().build();
        jobAdvertisementRepository.save(jobAdvertisement);

        AvamCancellationDto cancellationDto = testAvamCancellationDto();
        cancellationDto.setStellennummerAvam("NOTFOUND").setStellennummerEgov("NOTFOUND");
        // when
        avamService.handleCancelAction(cancellationDto);

        // then
        ArgumentCaptor<MailSenderData> mailSenderDataArgumentCaptor = ArgumentCaptor.forClass(MailSenderData.class);

        verify(mailSenderService, times(1)).send(mailSenderDataArgumentCaptor.capture());

        MailSenderData mailSenderData = mailSenderDataArgumentCaptor.getValue();
        assertThat(mailSenderData).isNotNull();
        assertThat(mailSenderData.getTemplateVariables()).isNotEmpty();
    }


}
