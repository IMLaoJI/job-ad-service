package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.LocationService;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.TestUtil;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.WithApiUser;
import org.codehaus.jettison.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJob;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.ApiCreateJobAdvertisementFixture.createJobAdvertismentDto01;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.ApiCreateJobAdvertisementFixture.phoneFormatted;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.when;
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
        ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDto = createJobAdvertismentDto01();

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
    public void testCheckPhoneNumberFormat() throws Exception {
        // given
        ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDto = createJobAdvertismentDto01();

        //when
        when(locationService.isLocationValid(ArgumentMatchers.any())).thenReturn(true);
        when(locationService.enrichCodes(ArgumentMatchers.any())).then(returnsFirstArg());

        ResultActions post = post(apiCreateJobAdvertisementDto, URL);

        String contentAsString = post.andReturn().getResponse().getContentAsString();
        JSONArray ja = new JSONArray("[" + contentAsString + "]");
        // get applyChannel phone through: jobContent.applyChannel.phoneNumber
        String applyChannelPhone = ja.getJSONObject(0).getJSONObject("jobContent").getJSONObject("applyChannel").getString("phoneNumber");
        // get company phone through: jobContent.company.phone
        String phoneCompany = ja.getJSONObject(0).getJSONObject("jobContent").getJSONObject("company").getString("phone");

        // then
        post.andExpect(status().isCreated());
        assertThat(post.andReturn().getResponse().getHeader("token")).isNotBlank();
        assertThat(applyChannelPhone).isEqualTo(phoneFormatted);
        assertThat(phoneCompany).isEqualTo(phoneFormatted);
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
