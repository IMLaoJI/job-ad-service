package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.Application;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.FavouriteItemSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.FavouriteItemSearchService;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.ElasticsearchConfiguration;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.read.ElasticFavouriteItemSearchService;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.TestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJob;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("dev")
public class FavouriteItemSearchControllerIntTest {

    private static final String API_FAVOURITE_ITEM = "/api/favourite-items";

    @Qualifier("jobAdvertisementRepository")
    @Autowired
    private JobAdvertisementRepository jobAdvertisementJpaRepository;

    @Autowired
    private JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

    @Autowired
    private FavouriteItemRepository favouriteItemRepository;

    @Autowired
    private FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository;

    @Autowired
    private ElasticsearchConfiguration.CustomEntityMapper customEntityMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ElasticsearchIndexService elasticsearchIndexService;

    private FavouriteItemSearchService favouriteItemSearchService;

    private CurrentUserContext mockCurrentUserContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.favouriteItemElasticsearchRepository.deleteAll();
        this.jobAdvertisementElasticsearchRepository.deleteAll();
        this.favouriteItemRepository.deleteAll();
        this.jobAdvertisementJpaRepository.deleteAll();
        this.mockCurrentUserContext = mock(CurrentUserContext.class);

        this.favouriteItemSearchService = new ElasticFavouriteItemSearchService(mockCurrentUserContext,
                this.elasticsearchTemplate, this.customEntityMapper,
                this.favouriteItemElasticsearchRepository, this.jobAdvertisementElasticsearchRepository
        );

        // TODO FavouriteItemSearchController

        // TODO this.mockMvc
    }

    @Test
    public void indexParentWithChildTest() throws Exception {
        //given
        index(createJob(job01.id()));
        index(createJob(job02.id()));
        index(createJob(job03.id()));
        //when
        indexChildDocument(createFavouriteItem("child-01", job01.id(), "John"));
        indexChildDocument(createFavouriteItem("child-02", job02.id(), "John"));
        indexChildDocument(createFavouriteItem("child-03", job03.id(), "John"));
        indexChildDocument(createFavouriteItem("child-04", job01.id(), "Emma"));
        indexChildDocument(createFavouriteItem("child-05", job03.id(), "Emma"));
        indexChildDocument(createFavouriteItem("child-06", job03.id(), "Tom"));
        indexChildDocument(createFavouriteItem("child-07", job01.id(), "Jane"));
        indexChildDocument(createFavouriteItem("child-08", job02.id(), "Jane"));
        indexChildDocument(createFavouriteItem("child-09", job03.id(), "Jane"));
        indexChildDocument(createFavouriteItem("child-10", job03.id(), "Paul"));
        //then

        // WHEN
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_FAVOURITE_ITEM + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(new FavouriteItemSearchRequest()))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder().build();
        List<JobAdvertisementDocument> docs = elasticsearchTemplate.queryForList(searchQuery, JobAdvertisementDocument.class);

        Assert.assertNotNull(docs);
        Assert.assertTrue(docs.size() > 0);
    }

    private void index(JobAdvertisement jobAdvertisement) {
        this.jobAdvertisementElasticsearchRepository.save(new JobAdvertisementDocument(jobAdvertisement));
    }


    private void indexChildDocument(FavouriteItem favouriteItem) {
        this.elasticsearchIndexService.saveChildWithUpdateRequest(new FavouriteItemDocument(favouriteItem),
                favouriteItem.getJobAdvertisementId().getValue(), INDEX_NAME_JOB_ADVERTISEMENT);
    }

    private FavouriteItem createFavouriteItem(String favouriteItemId, JobAdvertisementId id, String ownerId) {
        return new FavouriteItem.Builder()
                .setId(new FavouriteItemId(favouriteItemId))
                .setJobAdvertismentId(id)
                .setOwnerId(ownerId)
                .setNote("Favourite Item Note")
                .build();
    }

}
