package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.alv.shared.logger.business.BusinessLogData;
import ch.admin.seco.jobs.services.jobadservice.application.security.Role;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.TestUtil;
import org.codehaus.jettison.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static ch.admin.seco.jobs.services.jobadservice.application.BusinessLogConstants.STATUS_ADDITIONAL_DATA;
import static ch.admin.seco.jobs.services.jobadservice.application.BusinessLogEventType.JOB_ADVERTISEMENT_FAVORITE_EVENT;
import static ch.admin.seco.jobs.services.jobadservice.application.BusinessLogObjectType.JOB_ADVERTISEMENT_LOG;
import static ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemIdFixture.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJob;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithDescription;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
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

    @SpyBean
    private ch.admin.seco.alv.shared.logger.business.BusinessLogger businessLogger;

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
        createFavouriteItemResource.jobAdvertisementId = job01.id();
        createFavouriteItemResource.userId = WithJobSeeker.USER_ID;
        createFavouriteItemResource.note = "Test Note";

        ResultActions post = post(createFavouriteItemResource, URL);
        post.andExpect(status().isCreated());

        String contentAsString = post.andReturn().getResponse().getContentAsString();
        JSONArray ja = new JSONArray("[" + contentAsString + "]");
        String id = ja.getJSONObject(0).getString("id");

        // then
        ArgumentCaptor<BusinessLogData> argumentCaptor = ArgumentCaptor.forClass(BusinessLogData.class);
        verify(businessLogger).log(argumentCaptor.capture());
        BusinessLogData logData = argumentCaptor.getValue();

        assertThat(logData.getEventType()).isEqualTo(JOB_ADVERTISEMENT_FAVORITE_EVENT.getTypeName());
        assertThat(logData.getObjectType()).isEqualTo(JOB_ADVERTISEMENT_LOG.getTypeName());
        assertThat(logData.getObjectId()).isEqualTo(job01.id().getValue());
        assertThat(logData.getAuthorities()).isEqualTo(Role.JOBSEEKER_CLIENT.getValue());
        assertThat(logData.getAdditionalData().get(STATUS_ADDITIONAL_DATA)).isEqualTo(JobAdvertisementStatus.PUBLISHED_PUBLIC);

        assertThat(this.favouriteItemRepository.findById(new FavouriteItemId(id))).isPresent();
        await().until(() -> favouriteItemElasticsearchRepository.findById(job01.id(), new FavouriteItemId(id)).isPresent());
    }

    @Test
    @WithJobSeeker
    public void testUpdateFavouriteItem() throws Exception {
        // given
        this.index(createJob(job01.id()));
        this.index(createTestFavouriteItem(favItem01.id(), job01.id(), WithJobSeeker.USER_ID, "Test Note"));

        FavouriteItemRestController.FavouriteItemUpdateResource favouriteItemUpdateResource = new FavouriteItemRestController.FavouriteItemUpdateResource();
        String adjustedNote = "My adjusted note";
        favouriteItemUpdateResource.note = adjustedNote;

        //when
        ResultActions put = this.mockMvc.perform(
                MockMvcRequestBuilders.put(URL + "/" + favItem01.id().getValue() + "/note")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(favouriteItemUpdateResource)));
        put.andExpect(status().isOk());

        // then
        Optional<FavouriteItem> favouriteItem = this.favouriteItemRepository.findById(favItem01.id());
        assertThat(favouriteItem).isPresent();
        assertThat(favouriteItem.get().getNote()).isEqualTo(adjustedNote);

        await().untilAsserted(() -> {
            Optional<FavouriteItemDocument> favouriteItemDocumentOptional = favouriteItemElasticsearchRepository.findById(job01.id(), favItem01.id());
            favouriteItemDocumentOptional
                    .ifPresent(favouriteItemDocument -> assertThat(favouriteItemDocument.getFavouriteItem().getNote()).isEqualTo(adjustedNote));
        });
    }

    @Test
    @WithJobSeeker
    public void testDeleteFavouriteItem() throws Exception {
        // given
        this.index(createJob(job01.id()));
        this.index(createTestFavouriteItem(favItem01.id(), job01.id(), WithJobSeeker.USER_ID, "Test Note"));
        await().until(() -> favouriteItemElasticsearchRepository.findById(job01.id(), favItem01.id()).isPresent());

        // when
        ResultActions delete = this.mockMvc.perform(
                MockMvcRequestBuilders.delete(URL + "/" + favItem01.id().getValue())
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));
        delete.andExpect(status().isNoContent());

        // then
        assertThat(this.favouriteItemRepository.findById(favItem01.id())).isNotPresent();
        await().until(() -> !favouriteItemElasticsearchRepository.findById(job01.id(), favItem01.id()).isPresent());
    }

    @Test
    @WithJobSeeker
    public void testFindByFavouriteItemId() throws Exception {
        // given
        this.index(createJob(job01.id()));
        this.index(createTestFavouriteItem(favItem01.id(), job01.id(), WithJobSeeker.USER_ID, "Test Note"));

        // when
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/" + favItem01.id().getValue())
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(equalTo(favItem01.id().getValue())));
        await().until(() -> favouriteItemElasticsearchRepository.findById(job01.id(), favItem01.id()).isPresent());
    }

    @Test
    @WithJobSeeker
    public void testFindByJobAdIdAndUserId() throws Exception {
        // given
        this.index(createJob(job01.id()));
        this.index(createTestFavouriteItem(favItem01.id(), job01.id(), WithJobSeeker.USER_ID, "Test Note"));

        // when
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/_search/byJobAdvertisementIdAndUserId?userId=" + WithJobSeeker.USER_ID + "&jobAdvertisementId=" + job01.id().getValue())
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(jsonPath("$.id").value(equalTo(favItem01.id().getValue())))
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

        this.index(createTestFavouriteItem(favItem01.id(), job01.id(), WithJobSeeker.USER_ID, "Meine grosse Notiz"));
        this.index(createTestFavouriteItem(favItem02.id(), job05.id(), WithJobSeeker.USER_ID, "Meine kleine Notiz"));
        this.index(createTestFavouriteItem(favItem06.id(), job04.id(), WithJobSeeker.USER_ID, "Meine andere Notiz"));

        this.index(createTestFavouriteItem(favItem03.id(), job01.id(), "job-seeker-2", "Eine andere Notiz"));
        this.index(createTestFavouriteItem(favItem04.id(), job03.id(), "job-seeker-2", "Eine weitere Notiz"));
        this.index(createTestFavouriteItem(favItem05.id(), job04.id(), "job-seeker-2", "Eine Notiz"));

        await().until(() -> favouriteItemElasticsearchRepository.count() >= 6);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/_search/byUserId")
                        .param("userId", WithJobSeeker.USER_ID)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo(job05.id().getValue())))
                .andExpect(jsonPath("$.[0].favouriteItem.id").value(equalTo(favItem02.id().getValue())))
                .andExpect(jsonPath("$.[1].jobAdvertisement.id").value(equalTo(job04.id().getValue())))
                .andExpect(jsonPath("$.[1].favouriteItem.id").value(equalTo(favItem06.id().getValue())))
                .andExpect(jsonPath("$.[2].jobAdvertisement.id").value(equalTo(job01.id().getValue())))
                .andExpect(jsonPath("$.[2].favouriteItem.id").value(equalTo(favItem01.id().getValue())))
        ;
    }

    @Test
    @WithJobSeeker
    public void testSearchByUserIdAndQuery() throws Exception {
        // given
        this.index(createJobWithDescription(job01.id(), "Java Engineer", "Description java angular"));
        this.index(createJobWithDescription(job02.id(), "Software Engineer", "Description c++"));
        this.index(createJobWithDescription(job03.id(), "Java Entwickler", "Description java angular"));
        this.index(createJobWithDescription(job04.id(), "Developer", "Description c# "));
        this.index(createJobWithDescription(job05.id(), "Software Developer", "Description oracle"));
        this.index(createJobWithDescription(job06.id(), "Java Developer", "Description angular java jee"));
        this.index(createJobWithDescription(job07.id(), "Java Engineer", "Description java jee"));
        this.index(createJobWithDescription(job08.id(), "Software Entwickler", "Description c++ c#"));

        this.index(createTestFavouriteItem(favItem01.id(), job01.id(), WithJobSeeker.USER_ID, "Meine grosse Notiz"));
        this.index(createTestFavouriteItem(favItem02.id(), job05.id(), WithJobSeeker.USER_ID, "Meine kleine Notiz"));
        this.index(createTestFavouriteItem(favItem06.id(), job07.id(), WithJobSeeker.USER_ID, "Meine andere Notiz"));
        this.index(createTestFavouriteItem(favItem10.id(), job08.id(), WithJobSeeker.USER_ID, "Eine Notiz"));
        this.index(createTestFavouriteItem(favItem07.id(), job04.id(), WithJobSeeker.USER_ID, "Meine andere Kommentar"));
        this.index(createTestFavouriteItem(favItem08.id(), job02.id(), WithJobSeeker.USER_ID, "Meine kleine Kommentar"));
        this.index(createTestFavouriteItem(favItem09.id(), job06.id(), WithJobSeeker.USER_ID, "Meine grosse Kommentar"));

        this.index(createTestFavouriteItem(favItem03.id(), job01.id(), "job-seeker-2", "Eine andere Notiz"));
        this.index(createTestFavouriteItem(favItem04.id(), job06.id(), "job-seeker-2", "Eine grosse Notiz"));
        this.index(createTestFavouriteItem(favItem05.id(), job04.id(), "job-seeker-2", "Eine Notiz"));

        await().until(() -> favouriteItemElasticsearchRepository.count() >= 10);

        String queryKeywordParameter;
        ResultActions resultActions;

        // when
        queryKeywordParameter = "Mei"; // FAVOURITE-ITEM.NOTE
        resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/_search/byUserId")
                        .param("userId", WithJobSeeker.USER_ID)
                        .param("query", queryKeywordParameter)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "6"))
                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo(job07.id().getValue())))
                .andExpect(jsonPath("$.[0].favouriteItem.id").value(equalTo(favItem06.id().getValue())))
                .andExpect(jsonPath("$.[1].jobAdvertisement.id").value(equalTo(job06.id().getValue())))
                .andExpect(jsonPath("$.[1].favouriteItem.id").value(equalTo(favItem09.id().getValue())))
                .andExpect(jsonPath("$.[2].jobAdvertisement.id").value(equalTo(job05.id().getValue())))
                .andExpect(jsonPath("$.[2].favouriteItem.id").value(equalTo(favItem02.id().getValue())))
                .andExpect(jsonPath("$.[3].jobAdvertisement.id").value(equalTo(job04.id().getValue())))
                .andExpect(jsonPath("$.[3].favouriteItem.id").value(equalTo(favItem07.id().getValue())))
                .andExpect(jsonPath("$.[4].jobAdvertisement.id").value(equalTo(job02.id().getValue())))
                .andExpect(jsonPath("$.[4].favouriteItem.id").value(equalTo(favItem08.id().getValue())))
                .andExpect(jsonPath("$.[5].jobAdvertisement.id").value(equalTo(job01.id().getValue())))
                .andExpect(jsonPath("$.[5].favouriteItem.id").value(equalTo(favItem01.id().getValue())))
        ;

        // when
        queryKeywordParameter = "Engin"; // JOB-ADVERTISEMENT.JOB-CONTENT.JOB-DESCRIPTIONS.TITLE
        resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/_search/byUserId")
                        .param("userId", WithJobSeeker.USER_ID)
                        .param("query", queryKeywordParameter)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo(job07.id().getValue())))
                .andExpect(jsonPath("$.[0].favouriteItem.id").value(equalTo(favItem06.id().getValue())))
                .andExpect(jsonPath("$.[1].jobAdvertisement.id").value(equalTo(job02.id().getValue())))
                .andExpect(jsonPath("$.[1].favouriteItem.id").value(equalTo(favItem08.id().getValue())))
                .andExpect(jsonPath("$.[2].jobAdvertisement.id").value(equalTo(job01.id().getValue())))
                .andExpect(jsonPath("$.[2].favouriteItem.id").value(equalTo(favItem01.id().getValue())))
        ;

        // when
        queryKeywordParameter = "angul"; // JOB-ADVERTISEMENT.JOB-CONTENT.JOB-DESCRIPTIONS.DESCRIPTION
        resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/_search/byUserId")
                        .param("userId", WithJobSeeker.USER_ID)
                        .param("query", queryKeywordParameter)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo(job06.id().getValue())))
                .andExpect(jsonPath("$.[0].favouriteItem.id").value(equalTo(favItem09.id().getValue())))
                .andExpect(jsonPath("$.[1].jobAdvertisement.id").value(equalTo(job01.id().getValue())))
                .andExpect(jsonPath("$.[1].favouriteItem.id").value(equalTo(favItem01.id().getValue())))
        ;
    }

    private void index(JobAdvertisement jobAdvertisement) {
        this.jobAdvertisementRepository.save(jobAdvertisement);
        this.jobAdvertisementElasticsearchRepository.save(new JobAdvertisementDocument(jobAdvertisement));
    }

    private void index(FavouriteItem favouriteItem) {
        this.favouriteItemRepository.save(favouriteItem);
        this.favouriteItemElasticsearchRepository.save(new FavouriteItemDocument(favouriteItem));
    }

    private FavouriteItem createTestFavouriteItem(FavouriteItemId favouriteItemId, JobAdvertisementId jobAdvertisementId, String ownerUserId, String note) {
        return new FavouriteItem.Builder()
                .setId(favouriteItemId)
                .setOwnerUserId(ownerUserId)
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