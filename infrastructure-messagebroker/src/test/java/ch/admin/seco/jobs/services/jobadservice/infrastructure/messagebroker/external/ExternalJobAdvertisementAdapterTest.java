package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.external;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Publication;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.ContactFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobContentFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.OwnerFixture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.external.CreateJobAdvertisementDtoTestFixture.testCreateJobAdvertisementDto;
import static java.time.LocalDate.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ExternalJobAdvertisementAdapterTest {

    @Autowired
    private ExternalMessageLogRepository externalMessageLogRepository;

    @MockBean
    private JobAdvertisementRepository jobAdvertisementRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockBean
    private JobAdvertisementApplicationService jobAdvertisementApplicationService;

    private ExternalJobAdvertisementAdapter sut; //System Under Test

    @Before
    public void setUp() {
        this.sut = new ExternalJobAdvertisementAdapter(jobAdvertisementApplicationService, jobAdvertisementRepository, transactionTemplate, externalMessageLogRepository);
    }

    @Test
    public void shouldCreateJobAdvertisementFromExternal() {
        when(jobAdvertisementRepository.findByStellennummerEgov(any())).thenReturn(Optional.empty());
        when(jobAdvertisementRepository.findByStellennummerAvam(any())).thenReturn(Optional.empty());
        when(jobAdvertisementRepository.findByFingerprint(any())).thenReturn(Optional.empty());
        CreateJobAdvertisementDto externalDto = testCreateJobAdvertisementDto();

        sut.handleCreateFromExternalAction(externalDto);

        verify(jobAdvertisementApplicationService, times(1)).createFromExtern(any());
        verify(jobAdvertisementApplicationService, never()).enrichFromExtern(any(), any(), any());
        verify(jobAdvertisementApplicationService, never()).updateFromExtern(any(), any());
    }

    @Test
    public void shouldEnrichJobroomJobAdvertisementFromExternal() {
        when(jobAdvertisementRepository.findByStellennummerEgov(any())).thenReturn(Optional.of(createJobRoomJobAdvertisement(job01.id())));
        when(jobAdvertisementRepository.findByStellennummerAvam(any())).thenReturn(Optional.empty());
        CreateJobAdvertisementDto externalDto = testCreateJobAdvertisementDto();

        sut.handleCreateFromExternalAction(externalDto);

        verify(jobAdvertisementApplicationService, never()).createFromExtern(any());
        verify(jobAdvertisementApplicationService, times(1)).enrichFromExtern(any(), any(), any());
        verify(jobAdvertisementApplicationService, never()).updateFromExtern(any(), any());
    }

    @Test
    public void shouldEnrichAVAMJobAdvertisementFromExternal() {
        when(jobAdvertisementRepository.findByStellennummerEgov(any())).thenReturn(Optional.empty());
        when(jobAdvertisementRepository.findByStellennummerAvam(any())).thenReturn(Optional.of(createAVAMJobAdvertisement(job01.id())));
        CreateJobAdvertisementDto externalDto = testCreateJobAdvertisementDto();

        sut.handleCreateFromExternalAction(externalDto);

        verify(jobAdvertisementApplicationService, never()).createFromExtern(any());
        verify(jobAdvertisementApplicationService, times(1)).enrichFromExtern(any(), any(), any());
        verify(jobAdvertisementApplicationService, never()).updateFromExtern(any(), any());
    }

    @Test
    public void shouldUpdateExternalJobAdvertisementFromExternal() {
        when(jobAdvertisementRepository.findByStellennummerEgov(any())).thenReturn(Optional.empty());
        when(jobAdvertisementRepository.findByStellennummerAvam(any())).thenReturn(Optional.empty());
        when(jobAdvertisementRepository.findByFingerprint(any())).thenReturn(Optional.of(createExternalJobAdvertisement(job01.id(), "fingerprint")));
        CreateJobAdvertisementDto externalDto = testCreateJobAdvertisementDto();

        sut.handleCreateFromExternalAction(externalDto);

        verify(jobAdvertisementApplicationService, never()).createFromExtern(any());
        verify(jobAdvertisementApplicationService, never()).enrichFromExtern(any(), any(), any());
        verify(jobAdvertisementApplicationService, times(1)).updateFromExtern(any(), any());
    }

    private JobAdvertisement createExternalJobAdvertisement(JobAdvertisementId jobAdvertisementId, String fingerprint) {
        return createJobAdvertisement(jobAdvertisementId, fingerprint, JobAdvertisementStatus.PUBLISHED_PUBLIC, SourceSystem.EXTERN);
    }

    private JobAdvertisement createJobRoomJobAdvertisement(JobAdvertisementId jobAdvertisementId) {
        return createJobAdvertisement(jobAdvertisementId, null, JobAdvertisementStatus.PUBLISHED_PUBLIC, SourceSystem.JOBROOM);
    }

    private JobAdvertisement createAVAMJobAdvertisement(JobAdvertisementId jobAdvertisementId) {
        return createJobAdvertisement(jobAdvertisementId, null, JobAdvertisementStatus.PUBLISHED_PUBLIC, SourceSystem.RAV);
    }

    private JobAdvertisement createJobAdvertisement(JobAdvertisementId jobAdvertisementId, String fingerprint, JobAdvertisementStatus status, SourceSystem sourceSystem) {
        return new JobAdvertisement.Builder()
                .setId(jobAdvertisementId)
                .setFingerprint(fingerprint)
                .setOwner(OwnerFixture.of(jobAdvertisementId).build())
                .setContact(ContactFixture.of(jobAdvertisementId).build())
                .setJobContent(JobContentFixture.of(jobAdvertisementId).build())
                .setPublication(new Publication.Builder().setEndDate(now()).build())
                .setSourceSystem(sourceSystem)
                .setStellennummerEgov(jobAdvertisementId.getValue())
                .setStellennummerAvam(null)
                .setStatus(status)
                .build();
    }

}