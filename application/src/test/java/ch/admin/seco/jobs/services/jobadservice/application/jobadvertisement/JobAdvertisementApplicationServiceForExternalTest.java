package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;


import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.LocationService;
import ch.admin.seco.jobs.services.jobadservice.application.ProfessionService;
import ch.admin.seco.jobs.services.jobadservice.application.ReportingObligationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.CompanyDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.OccupationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventMockUtils;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Occupation;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService.COUNTRY_ISO_CODE_SWITZERLAND;
import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.CreateJobAdvertisementFromAvamDtoTestFixture.testCreateJobAdvertisementDto;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.CREATED;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture.testJobAdvertisement;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.LocationFixture.testLocation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class JobAdvertisementApplicationServiceForExternalTest {

    private static final String TEST_STELLEN_NUMMER_EGOV = "1000000";

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
        when(egovNumberGenerator.nextStringValue()).thenReturn(TEST_STELLEN_NUMMER_EGOV);
    }

    @After
    public void tearDown() {
        domainEventMockUtils.clearEvents();
    }

    @Test
    public void createFromExtern() {
        //given
        CreateJobAdvertisementDto createJobAdvertisementDto = testCreateJobAdvertisementDto();

        //when
        JobAdvertisementId jobAdvertisementId = sut.createFromExtern(createJobAdvertisementDto);

        //then
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(jobAdvertisementId);
        assertThat(jobAdvertisement).isNotNull();
        assertThat(jobAdvertisement.getStatus()).isEqualTo(JobAdvertisementStatus.PUBLISHED_PUBLIC);
        assertThat(jobAdvertisement.getSourceSystem()).isEqualTo(SourceSystem.EXTERN);
        assertThat(jobAdvertisement.getStellennummerEgov()).isNull();
        assertThat(jobAdvertisement.getPublication().isEuresAnonymous()).isFalse();
        assertThat(jobAdvertisement.getPublication().isCompanyAnonymous()).isFalse();

        assertThat(jobAdvertisement.isReportingObligation()).isFalse();
        assertThat(jobAdvertisement.getJobContent().getDisplayCompany()).isNotNull();

        final CompanyDto company = createJobAdvertisementDto.getCompany();
        assertThat(jobAdvertisement.getJobContent().getDisplayCompany().getName()).isEqualTo(company.getName());
        assertThat(jobAdvertisement.getJobContent().getDisplayCompany().getCity()).isEqualTo(company.getCity());
        assertThat(jobAdvertisement.getJobContent().getDisplayCompany().getStreet()).isEqualTo(company.getStreet());

        Occupation occupation = jobAdvertisement.getJobContent().getOccupations().get(0);
        final OccupationDto singleOccupation = createJobAdvertisementDto.getSingleOccupation();
        assertThat(occupation.getWorkExperience()).isEqualByComparingTo(singleOccupation.getWorkExperience());
        assertThat(occupation.getAvamOccupationCode()).isEqualToIgnoringCase(singleOccupation.getAvamOccupationCode());
        assertThat(occupation.getEducationCode()).isEqualToIgnoringCase(singleOccupation.getEducationCode());
        assertThat(occupation.getQualificationCode()).isEqualByComparingTo(singleOccupation.getQualificationCode());

        domainEventMockUtils.assertSingleDomainEventPublished(JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_PUBLIC.getDomainEventType());
    }

    @Test
    public void createFromExternWithEmptyCountry() {
        //given
        CreateJobAdvertisementDto createJobAdvertisementDto = testCreateJobAdvertisementDto();

        //when
        JobAdvertisementId jobAdvertisementId = sut.createFromExtern(createJobAdvertisementDto);

        //then
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(jobAdvertisementId);
        assertThat(jobAdvertisement).isNotNull();
        assertThat(jobAdvertisement.getJobContent().getLocation().getCountryIsoCode()).isEqualTo(COUNTRY_ISO_CODE_SWITZERLAND);
    }

    @Test
    public void enrichFromExtern() {
        // given
        jobAdvertisementRepository.save(
                testJobAdvertisement()
                        .setStatus(CREATED)
                        .build());

        // when
        sut.enrichFromExtern(job01.id(), "fingerprint", "external");

        // then
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(job01.id());
        assertThat(jobAdvertisement.getFingerprint()).isEqualTo("fingerprint");
        assertThat(jobAdvertisement.getJobContent().getX28OccupationCodes()).isEqualTo("external");
        domainEventMockUtils.assertSingleDomainEventPublished(JobAdvertisementEvents.JOB_ADVERTISEMENT_UPDATED.getDomainEventType());
    }

}
