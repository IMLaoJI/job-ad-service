package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Salutation;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.TestUtil;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.WithApiUser;
import org.codehaus.jettison.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJob;
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

    @Before
    public void setUp() {
        this.jobAdvertisementRepository.deleteAll();
        this.jobAdvertisementElasticsearchRepository.deleteAll();
    }

    @Test
    @WithApiUser
    public void testCreateFavouriteItem() throws Exception {
        // given
        this.index(createJob(job01.id()));

        //when
        ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDto = new ApiCreateJobAdvertisementDto();

        ApiContactDto apiContactDto = new ApiContactDto();
        apiContactDto.setSalutation(Salutation.MR);
        apiContactDto.setFirstName("Test First Name");
        apiContactDto.setLastName("Test Last Name");
        apiContactDto.setPhone("+41123123123");
        apiContactDto.setEmail("Test@mail.com");
        apiContactDto.setLanguageIsoCode("en");
        apiCreateJobAdvertisementDto.setContact(apiContactDto);

        ApiPublicationDto apiPublicationDto = new ApiPublicationDto();
        apiPublicationDto.setStartDate(LocalDate.now());
        apiCreateJobAdvertisementDto.setPublication(apiPublicationDto);

        ApiJobDescriptionDto apiJobDescriptionDto = new ApiJobDescriptionDto();
        apiJobDescriptionDto.setLanguageIsoCode("en");
        apiJobDescriptionDto.setDescription("Test Description");
        apiJobDescriptionDto.setTitle("Test Description");
        List<ApiJobDescriptionDto> apiJobDescriptionDtos = new ArrayList<>();
        apiJobDescriptionDtos.add(apiJobDescriptionDto);
        apiCreateJobAdvertisementDto.setJobDescriptions(apiJobDescriptionDtos);


        ApiCompanyDto apiCompanyDto = new ApiCompanyDto();
        apiCompanyDto.setName("Test company name");
        apiCompanyDto.setPostalCode("3001");
        apiCompanyDto.setCity("Bern");
        apiCompanyDto.setCountryIsoCode("CH");
        apiCreateJobAdvertisementDto.setCompany(apiCompanyDto);

        ApiEmploymentDto apiEmploymentDto = new ApiEmploymentDto();
        apiEmploymentDto.setWorkloadPercentageMin(11);
        apiEmploymentDto.setWorkloadPercentageMax(11);
        apiCreateJobAdvertisementDto.setEmployment(apiEmploymentDto);

        ApiCreateLocationDto apiCreateLocationDto = new ApiCreateLocationDto();
        apiCreateLocationDto.setCity("Bern");
        apiCreateLocationDto.setPostalCode("3012");
        apiCreateLocationDto.setCountryIsoCode("CH");
        apiCreateJobAdvertisementDto.setLocation(apiCreateLocationDto);

        ApiOccupationDto apiOccupationDto = new ApiOccupationDto();
        apiOccupationDto.setAvamOccupationCode("123456789");
        apiCreateJobAdvertisementDto.setOccupation(apiOccupationDto);

        apiCreateJobAdvertisementDto.setApplyChannel(new ApiApplyChannelDto());

        ResultActions post = post(apiCreateJobAdvertisementDto, URL);
        post.andExpect(status().isCreated());

        String contentAsString = post.andReturn().getResponse().getContentAsString();
        JSONArray ja = new JSONArray("[" + contentAsString + "]");

        // then
        // TODO
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
}
