package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
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

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJob;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
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
        this.index(createJob(job01.id()));

        //when
        FavouriteItemRestController.CreateFavouriteItemResource createFavouriteItemResource = new FavouriteItemRestController.CreateFavouriteItemResource();
        createFavouriteItemResource.jobAdvertisementId = job01.id().getValue();
        createFavouriteItemResource.userId = WithJobSeeker.USER_ID;
        createFavouriteItemResource.note = "Test Note";

        ResultActions post = post(createFavouriteItemResource, URL);
        post.andExpect(status().isCreated());

        String contentAsString = post.andReturn().getResponse().getContentAsString();
        JSONArray ja = new JSONArray("[" + contentAsString + "]");
        String id = ja.getJSONObject(0).getString("value");

        // then check that the document is now in the repository & elasticsearch
        assertThat(this.favouriteItemRepository.findById(new FavouriteItemId(id))).isPresent();
        await().until(() -> favouriteItemElasticsearchRepository.findById(job01.id(), new FavouriteItemId(id)).isPresent());
    }

    @Test
    @WithJobSeeker
    public void testUpdateFavouriteItem() throws Exception {
        // given
        this.index(createJob(job01.id()));
        final FavouriteItemId fav01 = new FavouriteItemId("fav-01");
        this.index(createTestFavouriteItem(fav01, job01.id(), WithJobSeeker.USER_ID, "Test Note"));

        FavouriteItemRestController.FavouriteItemUpdateResource favouriteItemUpdateResource = new FavouriteItemRestController.FavouriteItemUpdateResource();
        String adjustedNote = "My adjusted note";
        favouriteItemUpdateResource.note = adjustedNote;

        //when
        ResultActions put = this.mockMvc.perform(
                MockMvcRequestBuilders.put(URL + "/" + fav01.getValue() + "/note")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(favouriteItemUpdateResource)));
        put.andExpect(status().isNoContent());

        // then check that the updated document is now in repository & elasticsearch
        Optional<FavouriteItem> favouriteItem = this.favouriteItemRepository.findById(fav01);
        assertThat(favouriteItem).isPresent();
        assertThat(favouriteItem.get().getNote()).isEqualTo(adjustedNote);

        await().untilAsserted(() -> {
            Optional<FavouriteItemDocument> favouriteItemDocumentOptional = favouriteItemElasticsearchRepository.findById(job01.id(), fav01);
            favouriteItemDocumentOptional
                    .ifPresent(favouriteItemDocument -> assertThat(favouriteItemDocument.getFavouriteItem().getNote()).isEqualTo(adjustedNote));
        });
    }

    @Test
    @WithJobSeeker
    public void testDeleteFavouriteItem() throws Exception {
        // given
        this.index(createJob(job01.id()));
        final FavouriteItemId fav01 = new FavouriteItemId("fav-01");
        this.index(createTestFavouriteItem(fav01, job01.id(), WithJobSeeker.USER_ID, "Test Note"));
        await().until(() -> favouriteItemElasticsearchRepository.findById(job01.id(), fav01).isPresent());

        // when
        ResultActions delete = this.mockMvc.perform(
                MockMvcRequestBuilders.delete(URL + "/" + fav01.getValue())
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));
        delete.andExpect(status().isNoContent());

        // then
        assertThat(this.favouriteItemRepository.findById(fav01)).isNotPresent();
        await().until(() -> !favouriteItemElasticsearchRepository.findById(job01.id(), fav01).isPresent());
    }

    @Test
    @WithJobSeeker
    public void testFindByFavouriteItemId() throws Exception {
        // given
        this.index(createJob(job01.id()));
        final FavouriteItemId fav01 = new FavouriteItemId("fav-01");
        this.index(createTestFavouriteItem(fav01, job01.id(), WithJobSeeker.USER_ID, "Test Note"));

        // when
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/" + fav01.getValue())
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(equalTo(fav01.getValue())));
        await().until(() -> favouriteItemElasticsearchRepository.findById(job01.id(), fav01).isPresent());
    }

    @Test
    @WithJobSeeker
    public void testFindByJobAdIdAndUserId() throws Exception {
        // given
        this.index(createJob(job01.id()));
        final FavouriteItemId fav01 = new FavouriteItemId("fav-01");
        this.index(createTestFavouriteItem(fav01, job01.id(), WithJobSeeker.USER_ID, "Test Note"));


        FavouriteItemRestController.SearchByJobAdIdAndUserIdResource searchByJobAdIdAndUserIdResource = new FavouriteItemRestController.SearchByJobAdIdAndUserIdResource();
        searchByJobAdIdAndUserIdResource.jobAdvertisementId = job01.id().getValue();
        searchByJobAdIdAndUserIdResource.userId = WithJobSeeker.USER_ID;

        // when
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/_search/byJobAdvertisementIdAndUserId")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchByJobAdIdAndUserIdResource)));

        // then
        resultActions
                .andExpect(jsonPath("$.id").value(equalTo(fav01.getValue())))
                .andExpect(jsonPath("$.note").value(equalTo("Test Note")));
    }

    @Test
    @WithJobSeeker
    public void testSearchByUserId() throws Exception {
        // given
        this.index(createJob(job01.id()));
        this.index(createJob(job02.id()));
        this.index(createJob(job03.id()));
        this.index(createJob(job04.id()));
        this.index(createJob(job05.id()));

        this.index(createTestFavouriteItem(new FavouriteItemId("fav-01"), job01.id(), WithJobSeeker.USER_ID, "Meine grosse Notiz"));
        this.index(createTestFavouriteItem(new FavouriteItemId("fav-02"), job05.id(), WithJobSeeker.USER_ID, "Meine kleine Notiz"));

        this.index(createTestFavouriteItem(new FavouriteItemId("fav-03"), job01.id(), "job-seeker-2", "Eine andere Notiz"));
        this.index(createTestFavouriteItem(new FavouriteItemId("fav-04"), job03.id(), "job-seeker-2", "Eine weitere Notiz"));
        this.index(createTestFavouriteItem(new FavouriteItemId("fav-05"), job04.id(), "job-seeker-2", "Eine Notiz"));

        await().until(() -> favouriteItemElasticsearchRepository.count() >= 5);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/_search/byUserId")
                        .param("userId", WithJobSeeker.USER_ID)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo(job05.id().getValue())))
                .andExpect(jsonPath("$.[0].favouriteItem.id").value(equalTo("fav-02")))
                .andExpect(jsonPath("$.[1].jobAdvertisement.id").value(equalTo(job01.id().getValue())))
                .andExpect(jsonPath("$.[1].favouriteItem.id").value(equalTo("fav-01")));
    }

    @Test
    @WithJobSeeker
    public void testSearchByUserIdAndQuery() throws Exception {
        // given
        this.index(createJob(job01.id()));
        this.index(createJob(job02.id()));
        this.index(createJob(job03.id()));
        this.index(createJob(job04.id()));
        this.index(createJob(job05.id()));

        this.index(createTestFavouriteItem(new FavouriteItemId("fav-01"), job01.id(), WithJobSeeker.USER_ID, "Meine grosse Notiz"));
        this.index(createTestFavouriteItem(new FavouriteItemId("fav-02"), job05.id(), WithJobSeeker.USER_ID, "Meine kleine Notiz"));

        this.index(createTestFavouriteItem(new FavouriteItemId("fav-03"), job01.id(), "job-seeker-2", "Eine andere Notiz"));
        this.index(createTestFavouriteItem(new FavouriteItemId("fav-04"), job03.id(), "job-seeker-2", "Eine weitere Notiz"));
        this.index(createTestFavouriteItem(new FavouriteItemId("fav-05"), job04.id(), "job-seeker-2", "Eine Notiz"));

        await().until(() -> favouriteItemElasticsearchRepository.count() >= 5);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/_search/byUserId")
                        .param("userId", WithJobSeeker.USER_ID)
                        .param("query", "kleine")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo(job05.id().getValue())))
                .andExpect(jsonPath("$.[0].favouriteItem.id").value(equalTo("fav-02")));
    }

    private void index(JobAdvertisement jobAdvertisement) {
        this.jobAdvertisementRepository.save(jobAdvertisement);
        this.jobAdvertisementElasticsearchRepository.save(new JobAdvertisementDocument(jobAdvertisement));
    }

    private void index(FavouriteItem favouriteItem) {
        this.favouriteItemRepository.save(favouriteItem);
        this.favouriteItemElasticsearchRepository.save(new FavouriteItemDocument(favouriteItem));
    }

    private FavouriteItem createTestFavouriteItem(FavouriteItemId favouriteItemId, JobAdvertisementId jobAdvertisementId, String ownerId, String note) {
        return new FavouriteItem.Builder()
                .setId(favouriteItemId)
                .setOwnerId(ownerId)
                .setJobAdvertisementId(jobAdvertisementId)
                .setNote(note)
                .build();
    }

    private ResultActions post(Object request, String urlTemplate) throws Exception {
        return this.mockMvc.perform(
                MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
        );
    }


}