package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;


import ch.admin.seco.jobs.services.jobadservice.application.LocationService;
import ch.admin.seco.jobs.services.jobadservice.application.ProfessionService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ApiSearchRequestDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.CreatedJobAdvertisementIdWithTokenDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.ConditionException;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventMockUtils;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Company;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.CreateJobAdvertisementDtoTestFixture.testCreateJobAdvertisementDto;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.CompanyFixture.testCompany;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.LocationFixture.testLocation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class JobAdvertisementApplicationServiceForAPITest {

    private static final String TEST_STELLEN_NUMMER_EGOV = "1000000";

    @Autowired
    private JobAdvertisementRepository jobAdvertisementRepository;

    @Autowired
    private JobAdvertisementApplicationService service;

    @Autowired
    private ProfessionService professionService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private DataFieldMaxValueIncrementer egovNumberGenerator;

    private DomainEventMockUtils domainEventMockUtils;

    @Before
    public void setUp() {
        domainEventMockUtils = new DomainEventMockUtils();
        when(locationService.enrichCodes(any())).thenReturn(testLocation().build());
        when(locationService.isLocationValid(any())).thenReturn(true);
        when(professionService.isKnownAvamCode(any())).thenReturn(true);
        when(egovNumberGenerator.nextStringValue()).thenReturn(TEST_STELLEN_NUMMER_EGOV);
    }

    @After
    public void tearDown() {
        domainEventMockUtils.clearEvents();
    }

    @Test
    public void createFromApi() {
        //given
        Company company = testCompany().build();
        CreateJobAdvertisementDto createJobAdvertisementDto = testCreateJobAdvertisementDto(company);

        //when
        CreatedJobAdvertisementIdWithTokenDto result = service.createFromApi(createJobAdvertisementDto);

        //then
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(new JobAdvertisementId(result.getJobAdvertisementId()));

        assertThat(result.getToken()).isNotBlank();
        assertThat(jobAdvertisement).isNotNull();
        assertThat(jobAdvertisement.getStatus()).isEqualTo(JobAdvertisementStatus.CREATED);
        assertThat(jobAdvertisement.getSourceSystem()).isEqualTo(SourceSystem.API);
        assertThat(jobAdvertisement.getStellennummerEgov()).isEqualTo(TEST_STELLEN_NUMMER_EGOV);
        assertThat(jobAdvertisement.getPublication().isEuresAnonymous()).isFalse();
        assertThat(jobAdvertisement.getPublication().isCompanyAnonymous()).isFalse();
        assertThat(jobAdvertisement.isReportingObligation()).isFalse();
        assertThat(jobAdvertisement.getJobContent().getDisplayCompany()).isEqualTo(company);
        assertThat(jobAdvertisement.getJobCenterCode()).isNull();

        domainEventMockUtils.assertSingleDomainEventPublished(JobAdvertisementEvents.JOB_ADVERTISEMENT_CREATED.getDomainEventType());
        verify(locationService, times(1)).isLocationValid(any());
    }

    @Test
    public void createFromApiWithUnknownAvamCode() {
        //given
        Company company = testCompany().build();
        CreateJobAdvertisementDto createJobAdvertisementDto = testCreateJobAdvertisementDto(company);

        //when
        when(professionService.isKnownAvamCode(createJobAdvertisementDto.getOccupation().getAvamOccupationCode())).thenReturn(false);

        //then
		assertThatThrownBy(() -> service.createFromApi(createJobAdvertisementDto)).isInstanceOf(ConditionException.class);
        verify(professionService, times(1)).isKnownAvamCode(any());
    }


        @Test
    public void searchFromApi() {
        //given
        Company company = testCompany().build();
        CreateJobAdvertisementDto createJobAdvertisementDto = testCreateJobAdvertisementDto(company);
        service.createFromApi(createJobAdvertisementDto);
        ApiSearchRequestDto apiSearchRequestDto = new ApiSearchRequestDto();
        Set<JobAdvertisementStatus> statuses = new HashSet<>();
        statuses.add(JobAdvertisementStatus.CREATED);
        statuses.add(JobAdvertisementStatus.PUBLISHED_PUBLIC);
        apiSearchRequestDto.setStatus(statuses);
        final PageRequest pageRequest = PageRequest.of(0, 25, Sort.by(Sort.Order.desc("updatedTime")));

        //when
        Page<JobAdvertisementDto> jobAdvertisementDtos = service.findJobAdvertisementsByStatus(pageRequest, apiSearchRequestDto);

        //then
        assertThat(jobAdvertisementDtos.getContent()).isNotNull();
        assertThat(jobAdvertisementDtos.getContent()).isNotEmpty();
        assertThat(jobAdvertisementDtos.getContent().size()).isEqualTo(1);
        assertThat(jobAdvertisementDtos.getContent().get(0).getStatus()).isEqualTo(JobAdvertisementStatus.CREATED);
        assertThat(jobAdvertisementDtos.getContent().get(0).getSourceSystem()).isEqualTo(SourceSystem.API);
        assertThat(jobAdvertisementDtos.getContent().get(0).getStellennummerEgov()).isEqualTo(TEST_STELLEN_NUMMER_EGOV);
        assertThat(jobAdvertisementDtos.getContent().get(0).getPublication().isEuresAnonymous()).isFalse();
        assertThat(jobAdvertisementDtos.getContent().get(0).getPublication().isCompanyAnonymous()).isFalse();
        assertThat(jobAdvertisementDtos.getContent().get(0).isReportingObligation()).isFalse();
        assertThat(jobAdvertisementDtos.getContent().get(0).getJobCenterCode()).isNull();

        domainEventMockUtils.assertSingleDomainEventPublished(JobAdvertisementEvents.JOB_ADVERTISEMENT_CREATED.getDomainEventType());
        verify(locationService, times(1)).isLocationValid(any());
    }
}
