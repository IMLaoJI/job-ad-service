package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.LocationService;
import ch.admin.seco.jobs.services.jobadservice.application.ProfessionService;
import ch.admin.seco.jobs.services.jobadservice.application.ReportingObligationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.OccupationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.AvamCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.ApprovalDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.RejectionDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventMockUtils;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.*;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobContentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.ApprovalDtoTestFixture.testApprovalDto;
import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.CreateJobAdvertisementFromAvamDtoTestFixture.testCreateJobAdvertisementDto;
import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.CreateJobAdvertisementFromAvamDtoTestFixture.testCreateJobAdvertisementDtoWithCompanyAnonymous;
import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.PublicationDtoTestFixture.testPublicationDtoWithCompanyAnonymous;
import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.RejectionDtoTestFixture.testRejectionDto;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.ApplyChannelFixture.testApplyChannel;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.ApplyChannelFixture.testDisplayApplyChannel;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.CompanyFixture.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture.testJobAdvertisement;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.LocationFixture.testLocation;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.PublicationFixture.testPublicationEmpty;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.fixture.JobCenterTestFixture.*;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class JobAdvertisementApplicationServiceForAvamTest {

    private static final String TEST_STELLEN_NUMMER_EGOV = "1000000";

    public static final String STELLENNUMMER_AVAM = "avam";

    @Autowired
    private DataFieldMaxValueIncrementer egovNumberGenerator;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ProfessionService professionService;

    @Autowired
    private JobCenterService jobCenterService;

    @Autowired
    private ReportingObligationService reportingObligationService;

    @Autowired
    private JobAdvertisementRepository jobAdvertisementRepository;

    @Autowired
    private JobAdvertisementApplicationService sut; //System Under Test

    private DomainEventMockUtils domainEventMockUtils;

    @Before
    public void setUp() {
        domainEventMockUtils = new DomainEventMockUtils();
        when(locationService.enrichCodes(any())).thenReturn(testLocation().build());
        when(locationService.isLocationValid(any())).thenReturn(Boolean.TRUE);
        when(jobCenterService.findJobCenterByCode(any())).thenReturn(testJobCenter("jobCenter-name"));

        when(egovNumberGenerator.nextStringValue()).thenReturn(TEST_STELLEN_NUMMER_EGOV);
    }

    @After
    public void tearDown() {
        domainEventMockUtils.clearEvents();
    }

    @Test
    public void createFromAvam() {
        //given
        AvamCreateJobAdvertisementDto createJobAdvertisementDto = testCreateJobAdvertisementDto();

        //when
        JobAdvertisementId jobAdvertisementId = sut.createFromAvam(createJobAdvertisementDto);

        //then
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(jobAdvertisementId);
        assertThat(jobAdvertisement).isNotNull();
        assertThat(jobAdvertisement.getStatus()).isEqualTo(APPROVED);
        assertThat(jobAdvertisement.getSourceSystem()).isEqualTo(SourceSystem.RAV);
        assertThat(jobAdvertisement.getStellennummerAvam()).isEqualTo(STELLENNUMMER_AVAM);
        assertThat(jobAdvertisement.getPublication().isEuresAnonymous()).isFalse();
        assertThat(jobAdvertisement.getPublication().isCompanyAnonymous()).isFalse();

        assertThat(jobAdvertisement.isReportingObligation()).isTrue();
        assertThat(jobAdvertisement.getJobContent().getDisplayCompany()).isEqualTo(testCompany().build());
        assertThat(jobAdvertisement.getJobContent().getDisplayApplyChannel()).isEqualTo(testApplyChannel().build());

        Occupation occupation = jobAdvertisement.getJobContent().getOccupations().get(0);
        OccupationDto occupationDto = createJobAdvertisementDto.getOccupations().get(0);
        assertThat(occupation.getWorkExperience()).isEqualByComparingTo(occupationDto.getWorkExperience());
        assertThat(occupation.getAvamOccupationCode()).isEqualToIgnoringCase(occupationDto.getAvamOccupationCode());
        assertThat(occupation.getEducationCode()).isEqualToIgnoringCase(occupationDto.getEducationCode());
        assertThat(occupation.getQualificationCode()).isEqualByComparingTo(occupationDto.getQualificationCode());

        Employment employment = jobAdvertisement.getJobContent().getEmployment();
        EmploymentDto employmentDto = createJobAdvertisementDto.getEmployment();
        assertThat(employment.getStartDate()).isEqualTo(employmentDto.getStartDate());
        assertThat(employment.getEndDate()).isEqualTo(employmentDto.getEndDate());
        assertThat(employment.getWorkloadPercentageMin()).isEqualTo(employmentDto.getWorkloadPercentageMin());
        assertThat(employment.getWorkloadPercentageMax()).isEqualTo(employmentDto.getWorkloadPercentageMax());
        assertThat(employment.getWorkForms()).isNotNull();
        assertThat(employment.getWorkForms()).isEqualTo(employmentDto.getWorkForms());

        domainEventMockUtils.assertSingleDomainEventPublished(JOB_ADVERTISEMENT_APPROVED.getDomainEventType());
    }

    @Test
    public void createFromAvamWithCompanyAnonymous() {
        //given
        AvamCreateJobAdvertisementDto createJobAdvertisementDto = testCreateJobAdvertisementDtoWithCompanyAnonymous();

        //when
        JobAdvertisementId jobAdvertisementId = sut.createFromAvam(createJobAdvertisementDto);

        //then
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(jobAdvertisementId);
        assertThat(jobAdvertisement).isNotNull();
        assertThat(jobAdvertisement.getStatus()).isEqualTo(APPROVED);
        assertThat(jobAdvertisement.getSourceSystem()).isEqualTo(SourceSystem.RAV);
        assertThat(jobAdvertisement.getStellennummerAvam()).isEqualTo(STELLENNUMMER_AVAM);
        assertThat(jobAdvertisement.getPublication().isEuresAnonymous()).isFalse();
        assertThat(jobAdvertisement.getPublication().isCompanyAnonymous()).isTrue();

        assertThat(jobAdvertisement.isReportingObligation()).isTrue();
        assertThat(jobAdvertisement.getJobContent().getDisplayCompany()).isEqualTo(testDisplayCompany(testJobCenter("jobCenter-name"))
                .setSurrogate(true)
                .build()
        );
        assertThat(jobAdvertisement.getJobContent().getDisplayApplyChannel()).isEqualTo(testDisplayApplyChannel(testJobCenter("jobCenter-name"))
                .build()
        );

        domainEventMockUtils.assertSingleDomainEventPublished(JOB_ADVERTISEMENT_APPROVED.getDomainEventType());
    }

    @Test
    public void createFromAvamWithCompanyAnonymousAndVerifyDisplayCompany() {
        when(jobCenterService.findJobCenterByCode(any())).thenReturn(testJobCenterWithUserDetail("jobCenter-name"));
        when(jobCenterService.findJobCenterUserByJobCenterUserId(any())).thenReturn(createJobCenterUser());
        String jobCenterUserName = "Markus Meier";
        AvamCreateJobAdvertisementDto createJobAdvertisementDto = testCreateJobAdvertisementDto(testCompany().build(), testPublicationDtoWithCompanyAnonymous());

        JobAdvertisementId jobAdvertisementId = sut.createFromAvam(createJobAdvertisementDto);

        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(jobAdvertisementId);
        assertThat(jobAdvertisement).isNotNull();
        assertThat(jobAdvertisement.getStatus()).isEqualTo(APPROVED);
        assertThat(jobAdvertisement.getSourceSystem()).isEqualTo(SourceSystem.RAV);
        assertThat(jobAdvertisement.getStellennummerAvam()).isEqualTo(STELLENNUMMER_AVAM);
        assertThat(jobAdvertisement.getPublication().isEuresAnonymous()).isFalse();
        assertThat(jobAdvertisement.getPublication().isCompanyAnonymous()).isTrue();
        assertThat(jobAdvertisement.isReportingObligation()).isTrue();
        assertThat(jobAdvertisement.getJobContent().getDisplayCompany().getName()).isEqualTo(testJobCenter(jobCenterUserName).getAddress().getName());

    }

    @Test
    public void updateFromAvam() {
        // given
        JobAdvertisement inspectingJobAd = jobAdvertisementRepository.save(
                testJobAdvertisement()
                        .setStatus(PUBLISHED_PUBLIC)
                        .setJobContent(JobContentFixture.of(job01.id()).build())
                        .setStellennummerAvam(STELLENNUMMER_AVAM)
                        .setJobCenterCode("blahblah")
                        .setRejectionDate(null)
                        .setRejectionCode(null)
                        .setRejectionReason(null)
                        .setCancellationDate(null)
                        .setCancellationCode(null)
                        .build()
        );
        AvamCreateJobAdvertisementDto createJobAdvertisementDto = testCreateJobAdvertisementDto();

        // when
        sut.createFromAvam(createJobAdvertisementDto);

        // then
        JobAdvertisement repoJobAd = jobAdvertisementRepository.getOne(job01.id());
        assertThat(repoJobAd.getJobCenterCode()).isEqualTo(createJobAdvertisementDto.getJobCenterCode());
        assertThat(repoJobAd.getJobCenterUserId()).isEqualTo(createJobAdvertisementDto.getJobCenterUserId());
    }

    @Test
    public void updateFromAvamWithRefining() {
        // given
        JobAdvertisement inspectingJobAd = jobAdvertisementRepository.save(
                testJobAdvertisement()
                        .setStatus(ARCHIVED)
                        .setJobContent(JobContentFixture.of(job01.id()).build())
                        .setStellennummerAvam(STELLENNUMMER_AVAM)
                        .setRejectionDate(null)
                        .setRejectionCode(null)
                        .setRejectionReason(null)
                        .setCancellationDate(null)
                        .setCancellationCode(null)
                        .setPublication(testPublicationEmpty()
                                .setStartDate(now().minusDays(10))
                                .setEndDate(now().minusDays(2))
                                .build())
                        .build()
        );
        AvamCreateJobAdvertisementDto createJobAdvertisementDto = testCreateJobAdvertisementDto(
                inspectingJobAd.getJobContent().getCompany(),
                new PublicationDto()
                        .setStartDate(now().plusDays(10))
                        .setEndDate(now().plusDays(22))
        );

        // when
        sut.createFromAvam(createJobAdvertisementDto);

        // then
        JobAdvertisement repoJobAd = jobAdvertisementRepository.getOne(job01.id());
        assertThat(repoJobAd.getStatus()).isEqualTo(JobAdvertisementStatus.REFINING);
    }

    @Test
    public void shouldApprove() {
        // given
        JobAdvertisement inspectingJobAd = jobAdvertisementRepository.save(
                testJobAdvertisement()
                        .setStatus(INSPECTING)
                        .setJobContent(JobContentFixture.of(job01.id()).build())
                        .build()
        );
        ApprovalDto approvalDto = testApprovalDto(inspectingJobAd);

        // when
        sut.approve(approvalDto);

        // then
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(job01.id());
        assertThat(jobAdvertisement.getStellennummerAvam()).isEqualTo(approvalDto.getStellennummerAvam());
        assertThat(jobAdvertisement.getApprovalDate()).isEqualTo(approvalDto.getDate());
        assertThat(jobAdvertisement.isReportingObligation()).isEqualTo(approvalDto.isReportingObligation());
        assertThat(jobAdvertisement.getReportingObligationEndDate()).isEqualTo(approvalDto.getReportingObligationEndDate());
        assertThat(jobAdvertisement.getJobCenterCode()).isEqualTo(approvalDto.getJobCenterCode());
        assertThat(jobAdvertisement.getJobCenterUserId()).isEqualTo(approvalDto.getJobCenterUserId());
        assertThat(jobAdvertisement.getStatus()).isEqualTo(APPROVED);
        domainEventMockUtils.assertSingleDomainEventPublished(JOB_ADVERTISEMENT_APPROVED.getDomainEventType());
    }


    @Test
    public void shouldApproveWithUpdate() {
        // given
        JobAdvertisement inspectingJobAd = jobAdvertisementRepository.save(
                testJobAdvertisement()
                        .setStatus(INSPECTING)
                        .setJobContent(JobContentFixture.of(job01.id()).build())
                        .build());
        ApprovalDto approvalDto = testApprovalDto(inspectingJobAd);
        approvalDto.getUpdateJobAdvertisement().setDescription("OTHER VALUE");

        // when
        sut.approve(approvalDto);

        // then
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(inspectingJobAd.getId());
        assertThat(jobAdvertisement.getStellennummerAvam()).isEqualTo(approvalDto.getStellennummerAvam());
        assertThat(jobAdvertisement.getApprovalDate()).isEqualTo(approvalDto.getDate());
        assertThat(jobAdvertisement.isReportingObligation()).isEqualTo(approvalDto.isReportingObligation());
        assertThat(jobAdvertisement.getReportingObligationEndDate()).isEqualTo(approvalDto.getReportingObligationEndDate());
        assertThat(jobAdvertisement.getJobCenterCode()).isEqualTo(approvalDto.getJobCenterCode());
        assertThat(jobAdvertisement.getJobCenterUserId()).isEqualTo(approvalDto.getJobCenterUserId());
        assertThat(jobAdvertisement.getStatus()).isEqualTo(APPROVED);
        assertThat(jobAdvertisement.getJobContent().getJobDescriptions().get(0).getDescription()).isEqualTo("OTHER VALUE");
        domainEventMockUtils.assertMultipleDomainEventPublished(2, JOB_ADVERTISEMENT_UPDATED.getDomainEventType());
    }

    @Test
    public void shouldReject() {
        // given
        jobAdvertisementRepository.save(
                testJobAdvertisement()
                        .setStatus(INSPECTING)
                        .setJobContent(JobContentFixture.of(job01.id()).build())
                        .build());
        RejectionDto rejectionDto = testRejectionDto();

        // when
        sut.reject(rejectionDto);

        // then
        JobAdvertisement repoJobAd = jobAdvertisementRepository.getOne(job01.id());
        assertThat(repoJobAd.getStellennummerAvam()).isEqualTo(STELLENNUMMER_AVAM);
        assertThat(repoJobAd.getRejectionDate()).isEqualTo(rejectionDto.getDate());
        assertThat(repoJobAd.getRejectionCode()).isEqualTo("code");
        assertThat(repoJobAd.getRejectionReason()).isEqualTo("reason");
        assertThat(repoJobAd.getJobCenterCode()).isEqualTo("jobcenterid");
        assertThat(repoJobAd.getJobCenterUserId()).isEqualTo("14711");
        assertThat(repoJobAd.getStatus()).isEqualTo(REJECTED);
        domainEventMockUtils.assertSingleDomainEventPublished(JOB_ADVERTISEMENT_REJECTED.getDomainEventType());
    }
}
