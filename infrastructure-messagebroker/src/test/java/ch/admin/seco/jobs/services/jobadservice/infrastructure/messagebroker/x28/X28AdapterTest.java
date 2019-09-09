package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.x28;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.x28.*;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.*;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;
import static java.time.LocalDate.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class X28AdapterTest {

    @Autowired
    private X28MessageLogRepository x28MessageLogRepository;

    @MockBean
    private JobAdvertisementRepository jobAdvertisementRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockBean
    private JobAdvertisementApplicationService jobAdvertisementApplicationService;

    private X28Adapter sut; //System Under Test

    @Before
    public void setUp() {
        this.sut = new X28Adapter(jobAdvertisementApplicationService, jobAdvertisementRepository, transactionTemplate, x28MessageLogRepository);
    }

    @Test
    public void shouldCreateJobAdvertisementFromX28() {
        when(jobAdvertisementRepository.findByStellennummerEgov(any())).thenReturn(Optional.empty());
        when(jobAdvertisementRepository.findByStellennummerAvam(any())).thenReturn(Optional.empty());
        when(jobAdvertisementRepository.findByFingerprint(any())).thenReturn(Optional.empty());
        X28CreateJobAdvertisementDto x28Dto = createJobAdvertisementFromX28Dto();

        sut.handleCreateFromX28Action(x28Dto);

        verify(jobAdvertisementApplicationService, times(1)).createFromExtern(any());
        verify(jobAdvertisementApplicationService, never()).enrichFromExtern(any(), any(), any());
        verify(jobAdvertisementApplicationService, never()).updateFromExtern(any(), any());
    }

    @Test
    public void shouldEnrichJobroomJobAdvertisementFromX28() {
        when(jobAdvertisementRepository.findByStellennummerEgov(any())).thenReturn(Optional.of(createJobRoomJobAdvertisement(job01.id())));
        when(jobAdvertisementRepository.findByStellennummerAvam(any())).thenReturn(Optional.empty());
        X28CreateJobAdvertisementDto x28Dto = createJobAdvertisementFromX28Dto();

        sut.handleCreateFromX28Action(x28Dto);

        verify(jobAdvertisementApplicationService, never()).createFromExtern(any());
        verify(jobAdvertisementApplicationService, times(1)).enrichFromExtern(any(), any(), any());
        verify(jobAdvertisementApplicationService, never()).updateFromExtern(any(), any());
    }

    @Test
    public void shouldEnrichAVAMJobAdvertisementFromX28() {
        when(jobAdvertisementRepository.findByStellennummerEgov(any())).thenReturn(Optional.empty());
        when(jobAdvertisementRepository.findByStellennummerAvam(any())).thenReturn(Optional.of(createAVAMJobAdvertisement(job01.id())));
        X28CreateJobAdvertisementDto x28Dto = createJobAdvertisementFromX28Dto();

        sut.handleCreateFromX28Action(x28Dto);

        verify(jobAdvertisementApplicationService, never()).createFromExtern(any());
        verify(jobAdvertisementApplicationService, times(1)).enrichFromExtern(any(), any(), any());
        verify(jobAdvertisementApplicationService, never()).updateFromExtern(any(), any());
    }

    @Test
    public void shouldUpdateExternalJobAdvertisementFromX28() {
        when(jobAdvertisementRepository.findByStellennummerEgov(any())).thenReturn(Optional.empty());
        when(jobAdvertisementRepository.findByStellennummerAvam(any())).thenReturn(Optional.empty());
        when(jobAdvertisementRepository.findByFingerprint(any())).thenReturn(Optional.of(createExternalJobAdvertisement(job01.id(), "fingerprint")));
        X28CreateJobAdvertisementDto x28Dto = createJobAdvertisementFromX28Dto();

        sut.handleCreateFromX28Action(x28Dto);

        verify(jobAdvertisementApplicationService, never()).createFromExtern(any());
        verify(jobAdvertisementApplicationService, never()).enrichFromExtern(any(), any(), any());
        verify(jobAdvertisementApplicationService, times(1)).updateFromExtern(any(), any());
    }

    private JobAdvertisement createExternalJobAdvertisement(JobAdvertisementId jobAdvertisementId, String fingerprint) {
        return creatJobAdvertisement(jobAdvertisementId, fingerprint, JobAdvertisementStatus.PUBLISHED_PUBLIC, SourceSystem.EXTERN);
    }

    private JobAdvertisement createJobRoomJobAdvertisement(JobAdvertisementId jobAdvertisementId) {
        return creatJobAdvertisement(jobAdvertisementId, null, JobAdvertisementStatus.PUBLISHED_PUBLIC, SourceSystem.JOBROOM);
    }

    private JobAdvertisement createAVAMJobAdvertisement(JobAdvertisementId jobAdvertisementId) {
        return creatJobAdvertisement(jobAdvertisementId, null, JobAdvertisementStatus.PUBLISHED_PUBLIC, SourceSystem.RAV);
    }

    private JobAdvertisement creatJobAdvertisement(JobAdvertisementId jobAdvertisementId, String fingerprint, JobAdvertisementStatus status, SourceSystem sourceSystem) {
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

    private X28CreateJobAdvertisementDto createJobAdvertisementFromX28Dto() {
        return new X28CreateJobAdvertisementDto()
                .setStellennummerEgov("stellennummerEgov")
                .setStellennummerAvam("stellennummerAvam")
                .setTitle("title")
                .setDescription("description")
                .setNumberOfJobs("numberOfJobs")
                .setFingerprint("fingerprint")
                .setExternalUrl("externalUrl")
                .setJobCenterCode("jobCenterCode")
                .setContact(new X28ContactDto(Salutation.MR, "firstName", "lastName", "phone", "email", "de"))
                .setEmployment(
                        new EmploymentDto()
                                .setStartDate(LocalDate.of(2018, 1, 1))
                                .setEndDate(LocalDate.of(2018, 12, 31))
                                .setShortEmployment(false)
                                .setImmediately(false)
                                .setPermanent(false)
                                .setWorkloadPercentageMin(100)
                                .setWorkloadPercentageMax(100)
                                .setWorkForms(null))
                .setCompany(new X28CompanyDto("companyName", "companyStreet", "companyHouseNumber", "companyPostalCode", "companyCity", "CH", null, null, null, "companyPhone", "companyEmail", "companyWebside", false))
                .setLocation(new X28LocationDto(null, "locationCity", "locationPostalCode", "CH"))
                .setOccupations(Collections.singletonList(new X28OccupationDto().setAvamOccupationCode("avamOccupationCode")
                        .setWorkExperience(WorkExperience.MORE_THAN_1_YEAR)
                        .setEducationCode("educationCode")
                        .setQualificationCode(Qualification.SKILLED)))
                .setProfessionCodes("professionCodes")
                .setLanguageSkills(Collections.singletonList(new X28LanguageSkillDto("de", LanguageLevel.PROFICIENT, LanguageLevel.INTERMEDIATE)))
                .setPublicationStartDate(LocalDate.of(2018, 1, 1))
                .setPublicationEndDate(LocalDate.of(2018, 12, 31))
                .setCompanyAnonymous(false);
    }

}
