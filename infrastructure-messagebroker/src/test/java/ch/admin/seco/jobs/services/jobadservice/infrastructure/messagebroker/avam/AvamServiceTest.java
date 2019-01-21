package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.CancellationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture.testJobAdvertisement;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.AvamCancellationDtoFixture.testAvamCancellationDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class AvamServiceTest {

    @Autowired
    private AvamService avamService;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private AvamMailSender avamMailSender;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private JobAdvertisementApplicationService jobAdvertisementApplicationService;

    @Autowired
    private JobCenterService jobCenterService;

    @Before
    public void setUp() {
        when(jobCenterService.findJobCenterByCode((any()))).thenReturn(new JobCenter());
    }

    @Test
    public void sendEmailIfCancellationDtoNotFound() {
        when(jobAdvertisementApplicationService.findByStellennummerEgovOrAvam(any(), any())).thenReturn(null);
        // given
        AvamCancellationDto cancellationDto = testAvamCancellationDto().setStellennummerEgov(null).setStellennummerAvam("12345");

        // when
        avamService.handleCancelAction(cancellationDto);

        // then
        ArgumentCaptor<MailSenderData> mailSenderDataArgumentCaptor = ArgumentCaptor.forClass(MailSenderData.class);

        verify(mailSenderService, times(1)).send(mailSenderDataArgumentCaptor.capture());

        MailSenderData mailSenderData = mailSenderDataArgumentCaptor.getValue();
        assertThat(mailSenderData).isNotNull();
        assertThat(mailSenderData.getTemplateVariables()).isNotEmpty();
        assertThat(mailSenderData.getTemplateName()).isEqualToIgnoringCase("JobAdCancelledMail_multilingual.html");
    }

    @Test
    public void dontSendCancellationEmailDirectlyIfFound() {
        JobAdvertisement jobAdvertisement = testJobAdvertisement().build();
        JobAdvertisementDto jobAdvertisementDto = JobAdvertisementDto.toDto(jobAdvertisement);
        when(jobAdvertisementApplicationService.findByStellennummerEgovOrAvam(any(), any())).thenReturn(jobAdvertisementDto);
        // given
        AvamCancellationDto avamCancellationDto = testAvamCancellationDto();

        // when
        avamService.handleCancelAction(avamCancellationDto);

        // then
        verify(jobAdvertisementApplicationService, times(1)).cancel(any(JobAdvertisementId.class), any(CancellationDto.class), isNull());
    }
}
