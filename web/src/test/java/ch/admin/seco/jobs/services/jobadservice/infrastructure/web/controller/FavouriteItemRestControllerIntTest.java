package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.Application;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.TestUtil;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.errors.ExceptionTranslator;
import org.codehaus.jettison.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job02;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("dev")
public class FavouriteItemRestControllerIntTest {

    private static final String URL = "/api/favourite-items";

    @Autowired
    private FavouriteItemRestController favouriteItemRestController;

    @Autowired
    private FormattingConversionService formattingConversionService;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private JobAdvertisementRepository jobAdvertisementRepository;

    @Autowired
    private FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository;

    @Autowired
    private JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

    @Autowired
    private FavouriteItemRepository favouriteItemRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {

        this.favouriteItemRepository.deleteAll();
        this.jobAdvertisementRepository.deleteAll();
        this.favouriteItemElasticsearchRepository.deleteAll();
        this.jobAdvertisementElasticsearchRepository.deleteAll();

        this.mockMvc = MockMvcBuilders.standaloneSetup(favouriteItemRestController)
                .setConversionService(formattingConversionService)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setMessageConverters(jacksonMessageConverter)
                .build();
    }

    @Test
    @WithJobSeeker
    public void create() throws Exception {
        // given
        JobAdvertisement jobAdvertisement = this.jobAdvertisementRepository.save(JobAdvertisementFixture.of(job02.id()).build());
        this.jobAdvertisementElasticsearchRepository.index(new JobAdvertisementDocument(jobAdvertisement));

        //when
        FavouriteItemRestController.CreateFavouriteItemResource createFavouriteItemResource = new FavouriteItemRestController.CreateFavouriteItemResource();
        createFavouriteItemResource.jobAdvertisementId = jobAdvertisement.getId().getValue();
        createFavouriteItemResource.userId = WithJobSeeker.USER_ID;
        createFavouriteItemResource.note = "Test Note";

        ResultActions post = post(createFavouriteItemResource, URL);
        post.andExpect(status().isCreated());

        String contentAsString = post.andReturn().getResponse().getContentAsString();
        String favouriteItemId = new JSONArray("[" + contentAsString + "]").getJSONObject(0).getString("value");

        // check in elasticsearch
        await().until(() -> this.jobAdvertisementElasticsearchRepository.existsById(jobAdvertisement.getId().getValue()));
        await().until(() -> this.favouriteItemElasticsearchRepository.findById(jobAdvertisement.getId().getValue(), favouriteItemId).isPresent());
    }


    private ResultActions post(Object request, String urlTemplate) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
        );
    }
}