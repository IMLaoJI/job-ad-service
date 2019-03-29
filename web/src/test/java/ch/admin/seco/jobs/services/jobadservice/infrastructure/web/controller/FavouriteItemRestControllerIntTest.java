package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.Application;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemElasticsearchRepository;
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
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private FavouriteItemRepository favouriteItemRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(favouriteItemRestController)
                .setConversionService(formattingConversionService)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setMessageConverters(jacksonMessageConverter)
                .build();
    }

    @Test
    @WithJobSeeker
    public void createFavouriteItem() throws Exception {
        // given
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.save(JobAdvertisementFixture.testJobAdvertisement().build());

        //when
        String id = createTestFavouriteItem(jobAdvertisement);


        // then check that the document is now in the repository & elasticsearch
        assertThat(favouriteItemRepository.findById(new FavouriteItemId(id))).isPresent();
        //   await().until(() -> favouriteItemElasticsearchRepository.findByIdAndParent(jobAdvertisement.getId().getValue(), id).isPresent());
    }

    @Test
    @WithJobSeeker
    public void updateFavouriteItem() throws Exception {
        // given
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.save(JobAdvertisementFixture.testJobAdvertisement().build());
        String id = createTestFavouriteItem(jobAdvertisement);
        FavouriteItemRestController.FavouriteItemUpdateResource favouriteItemUpdateResource = new FavouriteItemRestController.FavouriteItemUpdateResource();
        String adjustedNote = "My adjusted note";
        favouriteItemUpdateResource.note = adjustedNote;

        //when
        mockMvc.perform(
                MockMvcRequestBuilders.put(URL + "/" + id + "/note")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(favouriteItemUpdateResource)));

        // then check that the updated document is now in repository & elasticsearch
        Optional<FavouriteItem> favouriteItem = favouriteItemRepository.findById(new FavouriteItemId(id));
        assertThat(favouriteItem).isPresent();
        assertThat(favouriteItem.get().getNote()).isEqualTo(adjustedNote);

//        await().until(() -> {
//            Optional<FavouriteItemDocument> favouriteItemDocumentOptional = favouriteItemElasticsearchRepository.findByIdAndParent(jobAdvertisement.getId().getValue(), id);
//            if (favouriteItemDocumentOptional.isPresent()) {
//                assertThat(favouriteItemDocumentOptional.get().getFavouriteItem().getNote()).isEqualTo(adjustedNote);
//                return true;
//            } else {
//                return false;
//            }
//        });
    }

    @Test
    @WithJobSeeker
    public void deleteFavouriteItem() throws Exception {
        // given
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.save(JobAdvertisementFixture.testJobAdvertisement().build());
        String id = createTestFavouriteItem(jobAdvertisement);
        // await().until(() -> favouriteItemElasticsearchRepository.findById(id).isPresent());

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(URL + "/" + id)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "0"));
        // await().until(() -> !favouriteItemElasticsearchRepository.findByIdAndParent(jobAdvertisement.getId().getValue(), id).isPresent());
    }

    @Test
    @WithJobSeeker
    public void findByFavouriteItemId() throws Exception {
        // given
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.save(JobAdvertisementFixture.testJobAdvertisement().build());
        String id = createTestFavouriteItem(jobAdvertisement);
        FavouriteItemRestController.SearchByJobAdIdAndUserIdResource searchByJobAdIdAndUserIdResource = new FavouriteItemRestController.SearchByJobAdIdAndUserIdResource();


        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/" + id)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.[0].id").value(equalTo(id)));
        // await().until(() -> favouriteItemElasticsearchRepository.findByIdAndParent(jobAdvertisement.getId().getValue(), id).isPresent());
    }

    @Test
    @WithJobSeeker
    public void findByJobAdIdAndUserId() throws Exception {
        // given
        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.save(JobAdvertisementFixture.testJobAdvertisement().build());

        FavouriteItemRestController.CreateFavouriteItemResource createFavouriteItemResource = new FavouriteItemRestController.CreateFavouriteItemResource();
        createFavouriteItemResource.jobAdvertisementId = jobAdvertisement.getId().getValue();
        createFavouriteItemResource.userId = WithJobSeeker.USER_ID;
        createFavouriteItemResource.note = "Test Note 1";
        ResultActions post = post(createFavouriteItemResource, URL);
        post.andExpect(status().isCreated());
        String contentAsString = post.andReturn().getResponse().getContentAsString();
        JSONArray ja = new JSONArray("[" + contentAsString + "]");
        String id1 = ja.getJSONObject(0).getString("value");

        createFavouriteItemResource = new FavouriteItemRestController.CreateFavouriteItemResource();
        createFavouriteItemResource.jobAdvertisementId = jobAdvertisement.getId().getValue();
        createFavouriteItemResource.userId = WithJobSeeker.USER_ID;
        createFavouriteItemResource.note = "Test Note 2";
        post = post(createFavouriteItemResource, URL);
        post.andExpect(status().isCreated());
        contentAsString = post.andReturn().getResponse().getContentAsString();
        ja = new JSONArray("[" + contentAsString + "]");
        String id2 = ja.getJSONObject(0).getString("value");

        // await().until(() -> favouriteItemElasticsearchRepository.findById(id1).isPresent());
        // await().until(() -> favouriteItemElasticsearchRepository.findById(id2).isPresent());

        FavouriteItemRestController.SearchByJobAdIdAndUserIdResource searchByJobAdIdAndUserIdResource = new FavouriteItemRestController.SearchByJobAdIdAndUserIdResource();
        searchByJobAdIdAndUserIdResource.jobAdvertisementId = jobAdvertisement.getId().getValue();
        searchByJobAdIdAndUserIdResource.userId = WithJobSeeker.USER_ID;

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/_search/byJobAdvertisementIdAndUserId")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchByJobAdIdAndUserIdResource)));

        // then
        resultActions.andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[0].id").value(equalTo(id1)))
                .andExpect(jsonPath("$.[1].id").value(equalTo(id2)))
                .andExpect(jsonPath("$.[0].note").value(equalTo("Test Note 1")))
                .andExpect(jsonPath("$.[0].note").value(equalTo("Test Note 2")));
    }

    private String createTestFavouriteItem(JobAdvertisement jobAdvertisement) throws Exception {
        FavouriteItemRestController.CreateFavouriteItemResource createFavouriteItemResource = new FavouriteItemRestController.CreateFavouriteItemResource();
        createFavouriteItemResource.jobAdvertisementId = jobAdvertisement.getId().getValue();
        createFavouriteItemResource.userId = WithJobSeeker.USER_ID;
        createFavouriteItemResource.note = "Test Note";

        ResultActions post = post(createFavouriteItemResource, URL);
        post.andExpect(status().isCreated());

        String contentAsString = post.andReturn().getResponse().getContentAsString();
        JSONArray ja = new JSONArray("[" + contentAsString + "]");
        return ja.getJSONObject(0).getString("value");
    }

    private ResultActions post(Object request, String urlTemplate) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
        );
    }


}