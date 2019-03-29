package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.TestUtil;
import org.codehaus.jettison.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class FavouriteItemRestControllerIntTest {

    private static final String URL = "/api/favourite-items";

    @Autowired
    private JobAdvertisementRepository jobAdvertisementRepository;

    @Autowired
    private FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository;

    @Autowired
    private JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

    @Autowired
    private FavouriteItemRepository favouriteItemRepository;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.favouriteItemRepository.deleteAll();
        this.jobAdvertisementRepository.deleteAll();
        this.favouriteItemElasticsearchRepository.deleteAll();
        this.jobAdvertisementElasticsearchRepository.deleteAll();
    }

    @Test
    @WithJobSeeker
    public void testCreateFavouriteItem() throws Exception {
        // given
        JobAdvertisement jobAdvertisement = this.jobAdvertisementRepository.save(JobAdvertisementFixture.testJobAdvertisement().build());

        //when
        String id = createTestFavouriteItem(jobAdvertisement);


        // then check that the document is now in the repository & elasticsearch
        assertThat(this.favouriteItemRepository.findById(new FavouriteItemId(id))).isPresent();
        //   await().until(() -> favouriteItemElasticsearchRepository.findByIdAndParent(jobAdvertisement.getId().getValue(), id).isPresent());
    }

    @Test
    @WithJobSeeker
    public void testUpdateFavouriteItem() throws Exception {
        // given
        JobAdvertisement jobAdvertisement = this.jobAdvertisementRepository.save(JobAdvertisementFixture.testJobAdvertisement().build());
        String id = createTestFavouriteItem(jobAdvertisement);
        FavouriteItemRestController.FavouriteItemUpdateResource favouriteItemUpdateResource = new FavouriteItemRestController.FavouriteItemUpdateResource();
        String adjustedNote = "My adjusted note";
        favouriteItemUpdateResource.note = adjustedNote;

        //when
        this.mockMvc.perform(
                MockMvcRequestBuilders.put(URL + "/" + id + "/note")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(favouriteItemUpdateResource)));

        // then check that the updated document is now in repository & elasticsearch
        Optional<FavouriteItem> favouriteItem = this.favouriteItemRepository.findById(new FavouriteItemId(id));
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
    public void testDeleteFavouriteItem() throws Exception {
        // given
        JobAdvertisement jobAdvertisement = this.jobAdvertisementRepository.save(JobAdvertisementFixture.testJobAdvertisement().build());
        String id = createTestFavouriteItem(jobAdvertisement);
        // await().until(() -> favouriteItemElasticsearchRepository.findById(id).isPresent());

        // when
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete(URL + "/" + id)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        assertThat(this.favouriteItemRepository.findById(new FavouriteItemId(id))).isNotPresent();

        // await().until(() -> !favouriteItemElasticsearchRepository.findByIdAndParent(jobAdvertisement.getId().getValue(), id).isPresent());
    }

    @Test
    @WithJobSeeker
    public void testFindByFavouriteItemId() throws Exception {
        // given
        JobAdvertisement jobAdvertisement = this.jobAdvertisementRepository.save(JobAdvertisementFixture.testJobAdvertisement().build());
        String id = createTestFavouriteItem(jobAdvertisement);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/" + id)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(equalTo(id)));
    }

    @Test
    @WithJobSeeker
    public void testFindByJobAdIdAndUserId() throws Exception {
        // given
        JobAdvertisement jobAdvertisement = this.jobAdvertisementRepository.save(JobAdvertisementFixture.testJobAdvertisement().build());

        String favouriteItemId = createTestFavouriteItem(jobAdvertisement);

        FavouriteItemRestController.SearchByJobAdIdAndUserIdResource searchByJobAdIdAndUserIdResource = new FavouriteItemRestController.SearchByJobAdIdAndUserIdResource();
        searchByJobAdIdAndUserIdResource.jobAdvertisementId = jobAdvertisement.getId().getValue();
        searchByJobAdIdAndUserIdResource.userId = WithJobSeeker.USER_ID;

        // when
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/_search/byJobAdvertisementIdAndUserId")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchByJobAdIdAndUserIdResource)));

        // then
        resultActions
                .andExpect(jsonPath("$.id").value(equalTo(favouriteItemId)))
                .andExpect(jsonPath("$.note").value(equalTo("Test Note")));
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
        return this.mockMvc.perform(
                MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
        );
    }


}