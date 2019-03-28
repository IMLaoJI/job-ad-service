package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.Application;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.TestUtil;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.errors.ExceptionTranslator;
import org.codehaus.jettison.json.JSONArray;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job02;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument.JOB_ADVERTISEMENT;
import static org.awaitility.Awaitility.await;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("dev")
public class FavouriteItemRestControllerIntTest {

    private static final String URL = "/api/favourite-items";

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

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

    @Autowired
    private ElasticsearchIndexService elasticsearchIndexService;

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
        createFavouriteItemResource.jobAdvertisementId=jobAdvertisement.getId().getValue();
        createFavouriteItemResource.userId=WithJobSeeker.USER_ID;
        createFavouriteItemResource.note="Test Note";

        ResultActions post = post(createFavouriteItemResource, URL);
        post.andExpect(status().isCreated());

        String contentAsString = post.andReturn().getResponse().getContentAsString();
        JSONArray ja = new JSONArray("["+contentAsString+"]");
        String id = ja.getJSONObject(0).getString("value");

        // check in elasticsearch
        // existById / findById etc. only works for the PARENT !! Child needs routing parameter, which isn't supported in ElasticsearchRepository
        await().until(() -> this.jobAdvertisementElasticsearchRepository.existsById(jobAdvertisement.getId().getValue()));
//        await().until(() -> this.favouriteItemElasticsearchRepository.existsById(id));
    }

    @Test
    @WithJobSeeker
    public void indexAndSearch() throws Exception {
        // given
        JobAdvertisement jobAdvertisement = this.jobAdvertisementRepository.save(JobAdvertisementFixture.of(job02.id()).build());
        this.jobAdvertisementElasticsearchRepository.index(new JobAdvertisementDocument(jobAdvertisement));

        FavouriteItem createFavouriteItem = new FavouriteItem.Builder()
                .setId(new FavouriteItemId("child-01"))
                .setOwnerId(WithJobSeeker.USER_ID)
                .setNote("hurray")
                .setJobAdvertisementId(jobAdvertisement.getId())
                .build();

        FavouriteItem favouriteItem = this.favouriteItemRepository.save(createFavouriteItem);
        this.elasticsearchIndexService.saveChildWithUpdateRequest(new FavouriteItemDocument(favouriteItem), jobAdvertisement.getId().getValue(), INDEX_NAME_JOB_ADVERTISEMENT);

        //when
        QueryBuilder query = matchAllQuery();
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(query)
                .withFilter(JoinQueryBuilders.hasParentQuery(JOB_ADVERTISEMENT, query, true))
                .build();

        List<String> favouriteItemListId = this.elasticsearchTemplate.queryForIds(searchQuery);
        assertThat(favouriteItemListId, is(notNullValue()));
        assertThat(favouriteItemListId, is(hasSize(1)));

    }

    private ResultActions post(Object request, String urlTemplate) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
        );
    }
}