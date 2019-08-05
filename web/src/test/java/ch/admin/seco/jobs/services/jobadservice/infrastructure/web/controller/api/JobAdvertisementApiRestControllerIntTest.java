package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.LocationService;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.TestUtil;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.WithApiUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.CREATED;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.INSPECTING;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_PUBLIC;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job02;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job03;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJob;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.ApiCreateJobAdvertisementFixture.createJobAdvertisementDto;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.ApiCreateJobAdvertisementFixture.phoneFormatted;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class JobAdvertisementApiRestControllerIntTest {
    private static final String URL = "/api/public/jobAdvertisements/v1";

    @Autowired
    private JobAdvertisementRepository jobAdvertisementRepository;

    @Autowired
    private JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @Before
    public void setUp() {
        this.jobAdvertisementRepository.deleteAll();
        this.jobAdvertisementElasticsearchRepository.deleteAll();
    }

    @Test
    @WithApiUser
    public void testCreateApiJobAdvertisement() throws Exception {
        // given
        this.index(createJob(job01.id()));
        ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDto = createJobAdvertisementDto();

        //when
        when(locationService.isLocationValid(ArgumentMatchers.any())).thenReturn(true);
        when(locationService.enrichCodes(ArgumentMatchers.any())).then(returnsFirstArg());

        ResultActions post = post(apiCreateJobAdvertisementDto, URL);

        // then
        post.andExpect(status().isCreated());
        assertThat(post.andReturn().getResponse().getHeader("token")).isNotBlank();
    }


    @Test
    @WithApiUser
    public void testGetApiJobAdvertisementByStatus() throws Exception {
        // given
        this.index(createJob(job01.id()));

        //when
        when(locationService.isLocationValid(ArgumentMatchers.any())).thenReturn(true);
        when(locationService.enrichCodes(ArgumentMatchers.any())).then(returnsFirstArg());

        ApiSearchRequestDto apiSearchRequestDto = new ApiSearchRequestDto();

        String[] statuses = {CREATED.name(), INSPECTING.name()};
        apiSearchRequestDto.setStatus(statuses);
        ResultActions post = post(apiSearchRequestDto, URL + "/_search");

        String[] statusPublished = {PUBLISHED_PUBLIC.name()};
        apiSearchRequestDto.setStatus(statusPublished);
        ResultActions postPublished = post(apiSearchRequestDto, URL + "/_search");

        // then
        post.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(equalTo("0")));
        postPublished.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].id").value(equalTo(job01.id().getValue())));
    }


    @Test
    @WithApiUser
    public void testGetApiJobAdvertisementByStatusInAscOrder() throws Exception {
        // given
        this.index(createJob(job01.id()));
        this.index(createJob(job02.id()));
        this.index(createJob(job03.id()));

        //when
        when(locationService.isLocationValid(ArgumentMatchers.any())).thenReturn(true);
        when(locationService.enrichCodes(ArgumentMatchers.any())).then(returnsFirstArg());

        ApiSearchRequestDto apiSearchRequestDto = new ApiSearchRequestDto();
        String[] statuses = {PUBLISHED_PUBLIC.name()};
        apiSearchRequestDto.setStatus(statuses);

        ResultActions   post = this.mockMvc.perform(
                MockMvcRequestBuilders.post(URL + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(apiSearchRequestDto))
                        .param("sort", Sort.DATE_ASC.getValue())
        );


        // then
        post.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].id").value(equalTo(job01.id().getValue())))
                .andExpect(jsonPath("$.content.[1].id").value(equalTo(job02.id().getValue())))
                .andExpect(jsonPath("$.content.[2].id").value(equalTo(job03.id().getValue())));
    }

    @Test
    @WithApiUser
    public void testGetApiJobAdvertisementByStatusWithInvalidValue() throws Exception {
        // given
        this.index(createJob(job01.id()));
        ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDto = createJobAdvertisementDto();
        when(locationService.isLocationValid(ArgumentMatchers.any())).thenReturn(true);
        when(locationService.enrichCodes(ArgumentMatchers.any())).then(returnsFirstArg());
        ResultActions post = post(apiCreateJobAdvertisementDto, URL);
        post.andExpect(status().isCreated());
        assertThat(post.andReturn().getResponse().getHeader("token")).isNotBlank();

        //when
        when(locationService.isLocationValid(ArgumentMatchers.any())).thenReturn(true);
        when(locationService.enrichCodes(ArgumentMatchers.any())).then(returnsFirstArg());

        ApiSearchRequestDto apiSearchRequestDto = new ApiSearchRequestDto();
        String[] statuses = {"INVALID VALUE"};
        apiSearchRequestDto.setStatus(statuses);

        post = post(apiSearchRequestDto, URL + "/_search");

        // then
        post.andExpect(status().isBadRequest());
    }

    @Test
    @WithApiUser
    public void testCreateApiJobAdvertisementWithSurrogateFlag() throws Exception {
        // given
        this.index(createJob(job01.id()));
        ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDtoWithoutEmployer = createJobAdvertisementDto();
        apiCreateJobAdvertisementDtoWithoutEmployer.getCompany().setSurrogate(false);
        apiCreateJobAdvertisementDtoWithoutEmployer.setEmployer(null);

        ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDtoWithEmployer = createJobAdvertisementDto();
        apiCreateJobAdvertisementDtoWithEmployer.getCompany().setSurrogate(true);
        ApiEmployerDto apiEmployerDto = new ApiEmployerDto();
        apiEmployerDto.setCountryIsoCode("DE");
        apiEmployerDto.setName("test name");
        apiEmployerDto.setCity("test city");
        apiEmployerDto.setPostalCode("1000");
        apiCreateJobAdvertisementDtoWithEmployer.setEmployer(apiEmployerDto);

        ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDtoWithInvalidEmployer = createJobAdvertisementDto();
        apiCreateJobAdvertisementDtoWithInvalidEmployer.getCompany().setSurrogate(true);
        ApiEmployerDto invalidApiEmployerDto = new ApiEmployerDto();
        apiCreateJobAdvertisementDtoWithInvalidEmployer.setEmployer(invalidApiEmployerDto);

        //when
        when(locationService.isLocationValid(ArgumentMatchers.any())).thenReturn(true);
        when(locationService.enrichCodes(ArgumentMatchers.any())).then(returnsFirstArg());

        ResultActions validPostWithoutEmployer = post(apiCreateJobAdvertisementDtoWithoutEmployer, URL);
        ResultActions validPostWithEmployer = post(apiCreateJobAdvertisementDtoWithEmployer, URL);
        ResultActions invalidPostWithEmployer = post(apiCreateJobAdvertisementDtoWithInvalidEmployer, URL);

        // then
        validPostWithoutEmployer.andExpect(status().isCreated());
        validPostWithEmployer.andExpect(status().isCreated());
        invalidPostWithEmployer.andExpect(status().isBadRequest());
    }

    @Test
    @WithApiUser
    public void testCheckPhoneNumberFormat() throws Exception {
        // given
        ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDto = createJobAdvertisementDto();

        //when
        when(locationService.isLocationValid(ArgumentMatchers.any())).thenReturn(true);
        when(locationService.enrichCodes(ArgumentMatchers.any())).then(returnsFirstArg());

        // then
        post(apiCreateJobAdvertisementDto, URL)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.jobContent.applyChannel.phoneNumber").value(equalTo(phoneFormatted)))
                .andExpect(jsonPath("$.jobContent.company.phone").value(equalTo(phoneFormatted)));
    }

    private void index(JobAdvertisement jobAdvertisement) {
        this.jobAdvertisementRepository.save(jobAdvertisement);
        this.jobAdvertisementElasticsearchRepository.save(new JobAdvertisementDocument(jobAdvertisement));
    }


    private ResultActions post(Object request, String urlTemplate) throws Exception {
        return this.mockMvc.perform(
                MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
        );
    }

    private class ApiSearchRequestDto {
        private String[] status;

        public String[] getStatus() {
            return status;
        }

        public ApiSearchRequestDto setStatus(String[] status) {
            this.status = status;
            return this;
        }
    }
}
