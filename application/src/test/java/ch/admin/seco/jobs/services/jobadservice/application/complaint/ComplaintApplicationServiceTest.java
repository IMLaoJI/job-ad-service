package ch.admin.seco.jobs.services.jobadservice.application.complaint;

import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementEventListener;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementMailEventListener;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementFactory;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static ch.admin.seco.jobs.services.jobadservice.application.complaint.dto.fixture.ComplaintDtoFixture.testComplaintDto;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture.testJobAdvertisement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ComplaintApplicationServiceTest {


    @Autowired
    ComplaintProperties complaintProperties;

    @Autowired
    private MailSenderService mailSenderService;

    @MockBean
    JobAdvertisementRepository jobAdvertisementRepository;

    @MockBean
    MailSenderData mailSenderData;

    private ComplaintApplicationService complaintApplicationService;

    @MockBean
    JobAdvertisementApplicationService jobAdvertisementApplicationService;

    @MockBean
    JobAdvertisementEventListener jobAdvertisementEventListener;

    @MockBean
    JobAdvertisementMailEventListener jobAdvertisementMailEventListener;

    @MockBean
    JobAdvertisementFactory jobAdvertisementFactory;

    @Before
    public void setUp() {
        this.complaintProperties.setReceiverEmailAddress("test");
        this.complaintApplicationService = new ComplaintApplicationService(mailSenderService, complaintProperties, jobAdvertisementRepository);
    }

    @Test
    public void sendComplaint() {
        //given
        ComplaintDto complaint = testComplaintDto();
        JobAdvertisement jobAdvertisement = testJobAdvertisement().build();
        when(jobAdvertisementRepository.findById(new JobAdvertisementId(testComplaintDto().getJobAdvertisementId())))
                .thenReturn(Optional.of(jobAdvertisement));

        //when
        this.complaintApplicationService.sendComplaint(complaint);

        // then
        ArgumentCaptor<MailSenderData> mailSenderDataArgumentCaptor = ArgumentCaptor.forClass(MailSenderData.class);

        verify(mailSenderService, times(1)).send(mailSenderDataArgumentCaptor.capture());

        MailSenderData mailSenderData = mailSenderDataArgumentCaptor.getValue();
        assertThat(mailSenderData).isNotNull();
        assertThat(mailSenderData.getTemplateVariables()).isNotEmpty();
        assertThat(mailSenderData.getTemplateName()).isEqualToIgnoringCase("Complaint.html");
        assertThat(mailSenderData.getSubject()).isEqualToIgnoringCase("mail.complaint.subject");
    }
}
