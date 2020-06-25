package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;


import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.LocationService;
import ch.admin.seco.jobs.services.jobadservice.application.ProfessionService;
import ch.admin.seco.jobs.services.jobadservice.application.ReportingObligationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external.*;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventMockUtils;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.*;
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
import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.CreateJobAdvertisementFromExternalDtoTestFixture.createCreateJobAdvertisementDto;
import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.ExternalCompanyDtoFixture.testExternalCompanyDto;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.CREATED;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_PUBLIC;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture.testJobAdvertisement;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.testJobAdvertisementWithExternalSourceSystemAndStatus;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.LocationFixture.testLocation;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.LocationFixture.testLocationEmpty;
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
        ExternalCompanyDto externalCompanyDto = testExternalCompanyDto();
        ExternalJobAdvertisementDto createJobAdvertisementDto = createCreateJobAdvertisementDto(testExternalCompanyDto());

        //when
        JobAdvertisementId jobAdvertisementId = sut.createFromExtern(createJobAdvertisementDto);

        //then
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(jobAdvertisementId);
        assertThat(jobAdvertisement).isNotNull();
        assertThat(jobAdvertisement.getStatus()).isEqualTo(PUBLISHED_PUBLIC);
        assertThat(jobAdvertisement.getSourceSystem()).isEqualTo(SourceSystem.EXTERN);
        assertThat(jobAdvertisement.getStellennummerEgov()).isNull();
        assertThat(jobAdvertisement.getPublication().isEuresAnonymous()).isFalse();
        assertThat(jobAdvertisement.getPublication().isCompanyAnonymous()).isFalse();

        assertThat(jobAdvertisement.isReportingObligation()).isFalse();
        assertThat(jobAdvertisement.getJobContent().getDisplayCompany()).isNotNull();
        assertThat(jobAdvertisement.getJobContent().getDisplayCompany().getName()).isEqualTo(externalCompanyDto.getName());
        assertThat(jobAdvertisement.getJobContent().getDisplayCompany().getCity()).isEqualTo(externalCompanyDto.getCity());
        assertThat(jobAdvertisement.getJobContent().getDisplayCompany().getStreet()).isEqualTo(externalCompanyDto.getStreet());

        Occupation occupation = jobAdvertisement.getJobContent().getOccupations().get(0);
        ExternalOccupationDto externalOccupationDto = createJobAdvertisementDto.getOccupations().get(0);
        assertThat(occupation.getWorkExperience()).isEqualByComparingTo(externalOccupationDto.getWorkExperience());
        assertThat(occupation.getAvamOccupationCode()).isEqualToIgnoringCase(externalOccupationDto.getAvamOccupationCode());
        assertThat(occupation.getEducationCode()).isEqualToIgnoringCase(externalOccupationDto.getEducationCode());
        assertThat(occupation.getQualificationCode()).isEqualByComparingTo(externalOccupationDto.getQualificationCode());

        domainEventMockUtils.assertSingleDomainEventPublished(JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_PUBLIC.getDomainEventType());
    }

    @Test
    public void updateExtern() {
        //given
        jobAdvertisementRepository.save(testJobAdvertisementWithExternalSourceSystemAndStatus(job01.id(), "fingerprint1", PUBLISHED_PUBLIC));
        when(locationService.enrichCodes(any())).thenReturn(testLocationEmpty().setCity("testCity").setPostalCode("9999").build());

        ExternalJobAdvertisementDto updateExternalJobAdvertisementDto = createCreateJobAdvertisementDto(testExternalCompanyDto())
                .setContact(new ExternalContactDto(
                        Salutation.MR,
                        "externalContactFirstName",
                        "externalContactLastName",
                        "999999999",
                        "external.contact@test.ch",
                        "de"
                ))
                .setEmployment(new EmploymentDto()
                        .setShortEmployment(false)
                        .setStartDate(TimeMachine.now().toLocalDate())
                        .setPermanent(true)
                        .setWorkloadPercentageMax(50)
                        .setWorkloadPercentageMax(70)
                );


        //when
        sut.updateFromExtern(job01.id(), updateExternalJobAdvertisementDto);


        //then
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(job01.id());
        assertThat(jobAdvertisement).isNotNull();
        assertThat(jobAdvertisement.getJobContent().getJobDescriptions()).hasSize(1);
        assertThat(jobAdvertisement.getJobContent().getJobDescriptions().get(0))
                .extracting("title", "description")
                .contains(updateExternalJobAdvertisementDto.getTitle(), updateExternalJobAdvertisementDto.getDescription());
        assertThat(jobAdvertisement.getJobContent().getLocation())
                .extracting("city", "postalCode")
                .contains("testCity", "9999");
        assertThat(jobAdvertisement.getContact())
                .extracting("firstName", "lastName", "phone", "email")
                .contains(
                        updateExternalJobAdvertisementDto.getContact().getFirstName(),
                        updateExternalJobAdvertisementDto.getContact().getLastName(),
                        updateExternalJobAdvertisementDto.getContact().getPhone(),
                        updateExternalJobAdvertisementDto.getContact().getEmail()
                );
        assertThat(jobAdvertisement.getJobContent().getEmployment())
                .extracting("startDate", "endDate", "shortEmployment", "immediately", "permanent", "workloadPercentageMin", "workloadPercentageMax")
                .contains(
                        updateExternalJobAdvertisementDto.getEmployment().getStartDate(),
                        updateExternalJobAdvertisementDto.getEmployment().getEndDate(),
                        updateExternalJobAdvertisementDto.getEmployment().isShortEmployment(),
                        updateExternalJobAdvertisementDto.getEmployment().isImmediately(),
                        updateExternalJobAdvertisementDto.getEmployment().isPermanent(),
                        updateExternalJobAdvertisementDto.getEmployment().getWorkloadPercentageMin(),
                        updateExternalJobAdvertisementDto.getEmployment().getWorkloadPercentageMax()
                );
    }

    @Test
    public void createFromExternWithEmptyCountry() {
        //given
        ExternalJobAdvertisementDto createJobAdvertisementDto = createCreateJobAdvertisementDto(null);

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
