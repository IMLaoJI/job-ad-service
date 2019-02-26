package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_PUBLIC;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_RESTRICTED;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.REJECTED;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem.API;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem.EXTERN;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture.testJobAdvertisement;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job02;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job03;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job04;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job05;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job06;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job07;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job08;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job09;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job10;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createArchivedJob;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJob;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithCompanyName;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithContractType;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithDescription;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithDescriptionAndOwnerCompanyId;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithLanguageSkills;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithLocation;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithOccupation;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithOwnerAndPublicationStartDate;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithPublicDisplayAndWithRestrictedDisplay;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithPublicDisplayAndWithoutRestrictedDisplay;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithWorkload;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithX28Code;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithoutPublicDisplayAndWithRestrictedDisplay;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithoutPublicDisplayAndWithoutRestrictedDisplay;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createRestrictedJob;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createRestrictedJobWithoutPublicDisplayAndWithRestrictedDisplay;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createRestrictedJobWithoutPublicDisplayAndWithoutRestrictedDisplay;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobDescriptionFixture.testJobDescription;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.LocationFixture.testLocation;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.PublicationFixture.testPublication;
import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.CombinableMatcher.both;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.stream.Stream;

import ch.admin.seco.jobs.services.jobadservice.Application;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.GeoPointDto;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.*;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobContentFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.OwnerFixture;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.ElasticsearchConfiguration;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.read.jobadvertisement.*;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.read.jobadvertisement.RadiusSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.jobadvertisement.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.jobadvertisement.JobAdvertisementElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.TestUtil;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.errors.ExceptionTranslator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import reactor.util.function.Tuples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
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

import java.util.List;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.JobAdvertisementWithLocationsFixture.listOfJobAdsForAbroadSearchTests;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.JobAdvertisementWithLocationsFixture.listOfJobAdsForGeoDistanceTests;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("dev")
public class JobAdvertisementSearchControllerIntTest {

    private static final String DEFAULT_AVAM_CODE = "11111";

    private static final String DEFAULT_BFS_CODE = "11111111";

    private static final String API_JOB_ADVERTISEMENTS = "/api/jobAdvertisements";

    private static final String LAUSANNE_COMMUNAL_CODE = "5586";

    private static final String BERN_COMMUNAL_CODE = "351";

    private static final String SION_COMMUNAL_CODE = "6266";

    private static final String ABROAD_COMMUNAL_CODE = "9999";

    private static final GeoPointDto BERN_GEO_POINT = new GeoPointDto().setLat(46.948).setLon(7.441);

    private static final GeoPointDto LAUSANNE_GEO_POINT = new GeoPointDto().setLat(46.552043).setLon(6.6523078);

    private static final GeoPointDto SION_GEO_POINT =  new GeoPointDto().setLat(46.234).setLon(7.359);

    @Autowired
    private JobAdvertisementRepository jobAdvertisementJpaRepository;

    @Autowired
    private JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ElasticsearchConfiguration.CustomEntityMapper customEntityMapper;

    @Autowired
    private FormattingConversionService formattingConversionService;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private JobAdvertisementSearchService jobAdvertisementSearchService;

    private CurrentUserContext mockCurrentUserContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.jobAdvertisementElasticsearchRepository.deleteAll();
        this.jobAdvertisementJpaRepository.deleteAll();
        this.mockCurrentUserContext = mock(CurrentUserContext.class);

        this.jobAdvertisementSearchService = new JobAdvertisementSearchService(mockCurrentUserContext,
                this.elasticsearchTemplate,
                this.customEntityMapper,
                this.jobAdvertisementElasticsearchRepository
        );

        JobAdvertisementSearchController jobAdvertisementSearchController
                = new JobAdvertisementSearchController(jobAdvertisementSearchService);

        this.mockMvc = MockMvcBuilders.standaloneSetup(jobAdvertisementSearchController)
                .setConversionService(formattingConversionService)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setMessageConverters(jacksonMessageConverter)
                .build();
    }

    @Test
    public void shouldSearchWithoutQuery() throws Exception {
        // GIVEN
        index(createJob(job01.id()));
        index(createJob(job02.id()));
        index(createJob(job03.id()));

        // WHEN
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(new JobAdvertisementSearchRequest()))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"));
    }

    @Test
    public void shouldSearchForAbroadJobs() throws Exception {
        // GIVEN
        index(listOfJobAdsForAbroadSearchTests());

        // WHEN
        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest();
        jobAdvertisementSearchRequest.setCommunalCodes(new String[]{"9999"});
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(jobAdvertisementSearchRequest))
        );

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.[0].id").value(equalTo("job02")))
                .andExpect(jsonPath("$.[0].jobContent.location.city").value(equalTo("Ausland")))
                .andExpect(jsonPath("$.[1].id").value(equalTo("job03")))
                .andExpect(jsonPath("$.[1].jobContent.location.city").value(equalTo("Ausland")))
                .andExpect(jsonPath("$.[2].id").value(equalTo("job04")))
                .andExpect(jsonPath("$.[2].jobContent.location.city").value(equalTo("Ausland")));

    }

    @Test
    public void shouldIgnoreGeoDistanceWhenNoRadiusSearchRequestIsProvided() throws Exception {
        // GIVEN
        index(listOfJobAdsForGeoDistanceTests());

        // WHEN
        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest();
        jobAdvertisementSearchRequest.setRadiusSearchRequest(null);
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(jobAdvertisementSearchRequest))
        );

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "4"));
    }

    @Test
    public void shouldIgnoreJobWithoutGeoPoint() throws Exception {
        // GIVEN
        index(createJobWithLocation(job01.id(),
                testLocation()
                        .setCity("Bern")
                        .setCommunalCode(BERN_COMMUNAL_CODE)
                        .setRegionCode("BE01")
                        .setCantonCode("BE")
                        .setPostalCode("3000")
                        .setCountryIsoCode("CH")
                        .setCoordinates(null)
                        .build()));

        // WHEN
        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest();
        jobAdvertisementSearchRequest.setRadiusSearchRequest(new RadiusSearchRequest().setGeoPoint(BERN_GEO_POINT).setDistance(150));
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(jobAdvertisementSearchRequest))
        );

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "0"));
    }


    @Test
    public void shouldSearchForJobsIn20KmRadiusOfBern() throws Exception {
        // GIVEN
        index(listOfJobAdsForGeoDistanceTests());

        // WHEN
        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest();
        jobAdvertisementSearchRequest.setCommunalCodes(new String[]{BERN_COMMUNAL_CODE});
        jobAdvertisementSearchRequest.setRadiusSearchRequest(new RadiusSearchRequest().setGeoPoint(BERN_GEO_POINT).setDistance(20));
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(jobAdvertisementSearchRequest))
        );

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.[0].id").value(equalTo("job01")))
                .andExpect(jsonPath("$.[0].jobContent.location.city").value(equalTo("Bern")));
    }

    @Test
    public void shouldSearchForJobsIn20KmRadiusOfLausanne() throws Exception {
        // GIVEN
        index(listOfJobAdsForGeoDistanceTests());

        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest();
        jobAdvertisementSearchRequest.setCommunalCodes(new String[]{LAUSANNE_COMMUNAL_CODE});
        jobAdvertisementSearchRequest.setRadiusSearchRequest(new RadiusSearchRequest().setGeoPoint(LAUSANNE_GEO_POINT).setDistance(20));

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(jobAdvertisementSearchRequest))
        );

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.[0].id").value(equalTo("job04")))
                .andExpect(jsonPath("$.[0].jobContent.location.city").value(equalTo("Lausanne")));
    }

    @Test
    public void shouldSearchForJobsIn80KmRadiusOfSion() throws Exception {
        // GIVEN
        index(listOfJobAdsForGeoDistanceTests());


        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest();
        jobAdvertisementSearchRequest.setCommunalCodes(new String[]{SION_COMMUNAL_CODE});
        jobAdvertisementSearchRequest.setRadiusSearchRequest(new RadiusSearchRequest().setGeoPoint(SION_GEO_POINT).setDistance(80));

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(jobAdvertisementSearchRequest))
        );

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.[0].id").value(equalTo("job01")))
                .andExpect(jsonPath("$.[0].jobContent.location.city").value(equalTo("Bern")))
                .andExpect(jsonPath("$.[1].id").value(equalTo("job03")))
                .andExpect(jsonPath("$.[1].jobContent.location.city").value(equalTo("Sion")))
                .andExpect(jsonPath("$.[2].id").value(equalTo("job04")))
                .andExpect(jsonPath("$.[2].jobContent.location.city").value(equalTo("Lausanne")));
    }

    @Test
    public void shouldSearchForAbroadAndBernJobs() throws Exception {
        // GIVEN
        index(listOfJobAdsForAbroadSearchTests());

        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest();
        jobAdvertisementSearchRequest.setCommunalCodes(new String[]{ABROAD_COMMUNAL_CODE, BERN_COMMUNAL_CODE});
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(jobAdvertisementSearchRequest))
        );

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "4"))
                .andExpect(jsonPath("$.[0].id").value(equalTo("job01")))
                .andExpect(jsonPath("$.[0].jobContent.location.city").value(equalTo("Bern")))
                .andExpect(jsonPath("$.[1].id").value(equalTo("job02")))
                .andExpect(jsonPath("$.[1].jobContent.location.city").value(equalTo("Ausland")))
                .andExpect(jsonPath("$.[2].id").value(equalTo("job03")))
                .andExpect(jsonPath("$.[2].jobContent.location.city").value(equalTo("Ausland")))
                .andExpect(jsonPath("$.[3].id").value(equalTo("job04")))
                .andExpect(jsonPath("$.[3].jobContent.location.city").value(equalTo("Ausland")));

    }

    @Test
    public void shouldNotSearchPeaJobsWithoutQuery() throws Exception {
        // GIVEN
        index(createJob(job01.id()));
        index(createJob(job02.id()));
        index(createJob(job03.id()));

        // WHEN
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search/pea")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(new PeaJobAdvertisementSearchRequest()))
        );

        // THEN
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldSearchPeaJobsWithoutJobTitleInRequest() throws Exception {
        // GIVEN
        index(createJobWithDescriptionAndOwnerCompanyId(job01.id(), "c++ developer", "c++ & java entwickler", "company-1"));
        index(createJobWithDescriptionAndOwnerCompanyId(job02.id(), "java & javascript developer", "jee entwickler", "company-1"));
        index(createJobWithDescriptionAndOwnerCompanyId(job03.id(), "php programmierer", "php programierer", "company-2"));
        PeaJobAdvertisementSearchRequest request = new PeaJobAdvertisementSearchRequest();
        request.setCompanyId("company-1");

        // WHEN
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search/pea")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"));
    }

    @Test
    public void shouldSearchPeaJobsByPublicationDate() throws Exception {
        // GIVEN
        index(createJobWithOwnerAndPublicationStartDate(job01.id(), "company-1", now().minusDays(30)));
        index(createJobWithOwnerAndPublicationStartDate(job02.id(), "company-1", now().minusDays(7)));
        index(createJobWithOwnerAndPublicationStartDate(job03.id(), "company-1", now()));
        index(createJob(job04.id()));
        PeaJobAdvertisementSearchRequest request = new PeaJobAdvertisementSearchRequest();
        request.setCompanyId("company-1");
        request.setOnlineSinceDays(7);

        // WHEN
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search/pea")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"));
    }

    @Test
    public void shouldSearchPeaJobsByJobTitle() throws Exception {
        // GIVEN
        index(createJobWithDescriptionAndOwnerCompanyId(job01.id(), "c++ developer", "c++ & java entwickler", "company-1"));
        index(createJobWithDescriptionAndOwnerCompanyId(job02.id(), "java & javascript developer", "jee entwickler", "company-1"));
        index(createJobWithDescriptionAndOwnerCompanyId(job03.id(), "php programmierer", "php programierer", "company-2"));
        index(createJobWithDescriptionAndOwnerCompanyId(job04.id(), "javascript developer", "javascript developer", "company-3"));
        index(createArchivedJob(job05.id(), "scala developer", "scala developer", "company-1"));
        PeaJobAdvertisementSearchRequest request = new PeaJobAdvertisementSearchRequest();
        request.setCompanyId("company-1");
        request.setJobTitle("developer");

        // WHEN
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search/pea")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"));
    }

    @Test
    public void shouldSearchByKeyword() throws Exception {
        // GIVEN
        index(createJobWithDescription(job01.id(), "c++ developer", "c++ & java entwickler"));
        index(createJobWithDescription(job02.id(), "java & javascript developer", "jee entwickler"));
        index(createJobWithDescription(job03.id(), "php programmierer", "php programierer"));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();
        searchRequest.setKeywords(new String[]{"entwickler", "java"});

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))

                .andExpect(jsonPath("$.[0].id").value(equalTo(job01.name())))
                .andExpect(jsonPath("$.[0].jobContent.jobDescriptions[0].title").value(equalTo("c++ developer")))
                .andExpect(jsonPath("$.[0].jobContent.jobDescriptions[0].description").value(equalTo("c++ &amp; <em>java</em> <em>entwickler</em>")))

                .andExpect(jsonPath("$.[1].id").value(equalTo(job02.name())))
                .andExpect(jsonPath("$.[1].jobContent.jobDescriptions[0].title").value(equalTo("<em>java</em> & <em>javascript</em> developer")))
                .andExpect(jsonPath("$.[1].jobContent.jobDescriptions[0].description").value(equalTo("jee <em>entwickler</em>")));
    }

    @Test
    public void shouldSearchBySourceSystemKeyword() throws Exception {
        // GIVEN
        index(createJobWithDescription(job01.id(), "c++ developer", "c++ & java entwickler", EXTERN, OwnerFixture.of(job01.id()).build()));
        index(createJobWithDescription(job02.id(), "java & javascript developer", "jee entwickler", API, OwnerFixture.of(job02.id()).build()));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();
        searchRequest.setKeywords(new String[]{"*extern"});

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))

                .andExpect(jsonPath("$.[0].id").value(equalTo(job01.name())))
                .andExpect(jsonPath("$.[0].jobContent.jobDescriptions[0].title").value(equalTo("c++ developer")))
                .andExpect(jsonPath("$.[0].jobContent.jobDescriptions[0].description").value(equalTo("c++ &amp; java entwickler")));
    }

    @Test
    public void shouldSearchByLanguageSkillKeyword() throws Exception {
        // GIVEN
        final LanguageSkill da = new LanguageSkill.Builder().setLanguageIsoCode("da").setSpokenLevel(LanguageLevel.PROFICIENT).setWrittenLevel(LanguageLevel.INTERMEDIATE).build();
        final LanguageSkill en = new LanguageSkill.Builder().setLanguageIsoCode("en").setSpokenLevel(LanguageLevel.PROFICIENT).setWrittenLevel(LanguageLevel.INTERMEDIATE).build();

        index(createJobWithLanguageSkills(job01.id(), "c++ developer", "c++ & java entwickler", EXTERN, da));
        index(createJobWithLanguageSkills(job02.id(), "java & javascript developer", "jee entwickler", API, en));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();
        searchRequest.setKeywords(new String[]{"dänisch"});

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"));
    }

    @Test
    public void shouldSearchByOccupation_BFS() throws Exception {

        // GIVEN
        Occupation occupation1 = new Occupation.Builder().setAvamOccupationCode(DEFAULT_AVAM_CODE).setBfsCode(DEFAULT_BFS_CODE).build();
        Occupation occupation2 = new Occupation.Builder().setAvamOccupationCode(DEFAULT_AVAM_CODE).build();
        Occupation occupation3 = new Occupation.Builder().setAvamOccupationCode(DEFAULT_AVAM_CODE).setBfsCode("dummy").build();
        Occupation occupation4 = new Occupation.Builder().setAvamOccupationCode(DEFAULT_AVAM_CODE).setBfsCode(DEFAULT_BFS_CODE).build();

        index(createJobWithOccupation(job01.id(), occupation1));
        index(createJobWithOccupation(job02.id(), occupation2));
        index(createJobWithOccupation(job03.id(), occupation3));
        index(createJobWithOccupation(job04.id(), occupation4));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();
        searchRequest.setProfessionCodes(new ProfessionCode[]{new ProfessionCode(ProfessionCodeType.BFS, DEFAULT_BFS_CODE)});

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job01.name())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job04.name())))
        ;
    }

    @Test
    public void shouldSearchByOccupation_X28() throws Exception {

        // GIVEN
        index(createJobWithX28Code(job01.id(), "1111,2222"));
        index(createJobWithX28Code(job02.id(), "1111"));
        index(createJobWithX28Code(job03.id(), "3333"));
        index(createJobWithX28Code(job04.id(), "4444"));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();
        searchRequest.setProfessionCodes(new ProfessionCode[]{
                new ProfessionCode(ProfessionCodeType.X28, "1111"),
                new ProfessionCode(ProfessionCodeType.X28, "44")
        });

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job01.name())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job02.name())));
    }

    @Test
    public void shouldFilterByCantons() throws Exception {
        // GIVEN
        Location location1 = new Location.Builder().setRemarks("remarks").setCity("city").setPostalCode("postalCode").setCommunalCode("communalCode")
                .setRegionCode("regionCode").setCountryIsoCode("ch")
                .setCantonCode("BE").build();
        Location location2 = new Location.Builder().setRemarks("remarks").setCity("city").setPostalCode("postalCode").setCommunalCode("communalCode")
                .setRegionCode("regionCode").setCountryIsoCode("ch")
                .setCantonCode("BE").build();
        Location location3 = new Location.Builder().setRemarks("remarks").setCity("city").setPostalCode("postalCode").setCommunalCode("communalCode")
                .setRegionCode("regionCode").setCountryIsoCode("ch")
                .setCantonCode("ZH").build();

        index(createJobWithLocation(job01.id(), location1));
        index(createJobWithLocation(job02.id(), location2));
        index(createJobWithLocation(job03.id(), location3));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();
        searchRequest.setCantonCodes(new String[]{"BE"});

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job01.name())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job02.name())))
        ;
    }

    @Test
    public void shouldFilterByWorkingTimeMinMax() throws Exception {
        // GIVEN
        index(createJobWithWorkload(job01.id(), 1, 100));
        index(createJobWithWorkload(job02.id(), 80, 100));
        index(createJobWithWorkload(job03.id(), 50, 50));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();
        searchRequest.setWorkloadPercentageMin(60);
        searchRequest.setWorkloadPercentageMax(80);

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job01.name())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job02.name())))
        ;
    }

    @Test
    public void shouldFilterByCompanyName() throws Exception {
        // GIVEN
        index(createJobWithCompanyName(job01.id(), "Siemens AG"));
        index(createJobWithCompanyName(job02.id(), "Gösser"));
        index(createJobWithCompanyName(job03.id(), "Goessip"));
        index(createJobWithCompanyName(job04.id(), "AG Gösser Zurich"));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();
        searchRequest.setCompanyName("goes");

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job02.name())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job03.name())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job04.name())));
    }

    @Test
    public void shouldFilterByPermanentContractType() throws Exception {
        // GIVEN
        index(createJob(job01.id()));
        index(createJobWithContractType(job02.id(), true));
        index(createJob(job03.id()));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();
        searchRequest.setPermanent(true);

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.*.id").value(job02.name()))
        ;
    }

    @Test
    public void shouldFilterByDisplayRestricted() throws Exception {
        // GIVEN
        when(this.mockCurrentUserContext.hasAnyRoles(ArgumentMatchers.any())).thenReturn(true);
        index(createJob(job01.id()));
        index(createRestrictedJob(job02.id()));
        index(createJob(job03.id()));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();
        searchRequest.setDisplayRestricted(true);

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.*.id").value(job02.name()))
        ;
    }

    @Test
    public void shouldFilterByPublicationForJobSeeker() throws Exception {
        // GIVEN
        when(this.mockCurrentUserContext.hasAnyRoles(ArgumentMatchers.any())).thenReturn(true);
        //-------------------------------------------------------------------------------publicDisplay  restrictedDisplay
        index(createJobWithoutPublicDisplayAndWithoutRestrictedDisplay(job01.id()));   //0 0
        index(createJobWithoutPublicDisplayAndWithRestrictedDisplay(job02.id()));      //0 1
        index(createJobWithPublicDisplayAndWithRestrictedDisplay(job03.id()));         //1 1
        index(createJobWithPublicDisplayAndWithoutRestrictedDisplay(job04.id()));      //1 0

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job02.name())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job03.name())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job04.name())))
        ;

    }

    @Test
    public void shouldFilterByPublicationForAnonymusUser() throws Exception {
        // GIVEN
        //-------------------------------------------------------------------------------publicDisplay  restrictedDisplay
        index(createJobWithoutPublicDisplayAndWithoutRestrictedDisplay(job01.id()));   //0 0
        index(createJobWithoutPublicDisplayAndWithRestrictedDisplay(job02.id()));      //0 1
        index(createJobWithPublicDisplayAndWithRestrictedDisplay(job03.id()));         //1 1
        index(createJobWithPublicDisplayAndWithoutRestrictedDisplay(job04.id()));      //1 0

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job03.name())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job04.name())))
        ;

    }

    @Test
    public void shouldNotShowRestrictedJobsForAnonymusUsers() throws Exception {
        // GIVEN
        index(createRestrictedJob(job01.id()));
        index(createJob(job02.id()));
        index(createRestrictedJob(job03.id()));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.*.id").value(job02.name()));
    }

    @Test
    public void shouldShowRestrictedJobsForJobSeekers() throws Exception {
        // GIVEN
        //-----------------------------------------------------------------------------------publicDisplay restrictedDisplay status
        when(this.mockCurrentUserContext.hasAnyRoles(ArgumentMatchers.any())).thenReturn(true);
        index(createJob(job01.id()));                                                           //1 0 PUBLISHED_PUBLIC
        index(createRestrictedJob(job02.id()));                                                 //1 0 PUBLISHED_RESTRICTED
        index(createRestrictedJobWithoutPublicDisplayAndWithoutRestrictedDisplay(job03.id()));  //0 0 PUBLISHED_RESTRICTED
        index(createRestrictedJobWithoutPublicDisplayAndWithRestrictedDisplay(job04.id()));     //0 1 PUBLISHED_RESTRICTED
        index(createJobWithoutPublicDisplayAndWithoutRestrictedDisplay(job05.id()));            //0 0 PUBLISHED_PUBLIC
        index(createJobWithoutPublicDisplayAndWithRestrictedDisplay(job06.id()));               //0 1 PUBLISHED_PUBLIC

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "5"))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job01.name())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job02.name())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job03.name())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job04.name())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(job06.name())));
    }

    @Test
    public void countJobs() throws Exception {
        // GIVEN
        index(createJob(job01.id()));
        index(createJob(job02.id()));
        index(createJobWithContractType(job03.id(), true));
        index(createJobWithWorkload(job04.id(), 20, 50));
        index(createJobWithDescription(job05.id(), "xxx", "yyyy"));
        index(createJob(job06.id()));
        index(createJob(job07.id()));
        index(createJob(job08.id()));
        index(createJob(job09.id()));
        index(createJob(job10.id()));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();
        searchRequest.setPermanent(false);
        searchRequest.setWorkloadPercentageMin(70);
        searchRequest.setCantonCodes(new String[]{"BE"});
        searchRequest.setProfessionCodes(new ProfessionCode[]{new ProfessionCode(ProfessionCodeType.AVAM, "avamOccupationCode")});
        searchRequest.setKeywords(new String[]{"title"});

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_count")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.totalCount").value(7))
        ;
    }

    @Test
    public void shouldSearchEuresJobAdvertisementsMarkedForPublication() throws Exception {
        // GIVEN
        // id, publicDisplay, restrictedDisplay, euresDisplay
        Stream.of(
                Tuples.of(job01, true, true, true, PUBLISHED_PUBLIC),
                Tuples.of(job02, false, true, true, PUBLISHED_PUBLIC),
                Tuples.of(job03, true, false, true, PUBLISHED_PUBLIC),
                Tuples.of(job04, true, false, true, PUBLISHED_RESTRICTED),
                Tuples.of(job05, true, true, false, PUBLISHED_PUBLIC),
                Tuples.of(job06, false, true, false, PUBLISHED_PUBLIC),
                Tuples.of(job07, true, false, false, PUBLISHED_PUBLIC),
                Tuples.of(job08, true, false, false, PUBLISHED_RESTRICTED)
        )
                .forEach(jobAdParam -> index(
                        testJobAdvertisement()
                                .setId(jobAdParam.getT1().id())
                                .setPublication(
                                        testPublication()
                                                .setPublicDisplay(jobAdParam.getT2())
                                                .setRestrictedDisplay(jobAdParam.getT3())
                                                .setEuresDisplay(jobAdParam.getT4())
                                                .build()
                                )
                                .setStatus(jobAdParam.getT5())
                                .build()
                        )
                );

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();
        searchRequest.setEuresDisplay(true);

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.[*].id").value(
                        both(containsInAnyOrder(
                                job01.name(),
                                job02.name(),
                                job03.name())
                        ).and(not(containsInAnyOrder(
                                job04.name(),
                                job05.name(),
                                job06.name(),
                                job07.name(),
                                job08.name())
                        ))));
    }

    @Test
    public void shouldSearchNotEuresJobAdvertisements() throws Exception {
        // GIVEN
        // id, publicDisplay, restrictedDisplay, euresDisplay
        Stream.of(
                Tuples.of(job01, true, true, true, PUBLISHED_PUBLIC),
                Tuples.of(job02, false, true, true, PUBLISHED_PUBLIC),
                Tuples.of(job03, true, false, true, PUBLISHED_PUBLIC),
                Tuples.of(job04, true, false, true, PUBLISHED_RESTRICTED),
                Tuples.of(job05, true, true, false, PUBLISHED_PUBLIC),
                Tuples.of(job06, false, true, false, PUBLISHED_PUBLIC),
                Tuples.of(job07, true, false, false, PUBLISHED_PUBLIC),
                Tuples.of(job08, true, false, false, PUBLISHED_RESTRICTED)
        )
                .forEach(jobAdParam -> index(
                        testJobAdvertisement()
                                .setId(jobAdParam.getT1().id())
                                .setPublication(
                                        testPublication()
                                                .setPublicDisplay(jobAdParam.getT2())
                                                .setRestrictedDisplay(jobAdParam.getT3())
                                                .setEuresDisplay(jobAdParam.getT4())
                                                .build()
                                )
                                .setStatus(jobAdParam.getT5())
                                .build()
                        )
                );

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();
        searchRequest.setEuresDisplay(false);

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        );

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "4"))
                .andExpect(jsonPath("$.[*].id").value(
                        both(containsInAnyOrder(
                                job01.name(),
                                job03.name(),
                                job05.name(),
                                job07.name())
                        ).and(not(containsInAnyOrder(
                                job02.name(),
                                job04.name(),
                                job06.name(),
                                job08.name())
                        ))));
    }

    @Test
    public void shouldSearchManagedJobAdsReturnBadRequestIfEmptyRequest() throws Exception {
        // GIVEN
        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest();

        // WHEN
        post(request, API_JOB_ADVERTISEMENTS + "/_search/managed")
                .andExpect(
                        status().isBadRequest());
    }

    @Test
    @Ignore // TODO configure prepost test
    public void shouldSearchManagedJobAdsReturnBadRequestIfCurrentUserIsNotMemberOfCompany() throws Exception {
        // GIVEN
        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId("companyId");

        // WHEN
        post(request, API_JOB_ADVERTISEMENTS + "/_search/managed")
                .andExpect(
                        status().isBadRequest());
    }

    @Test
    public void shouldSearchManagedJobAdsBeFilteredByPublicationStartDate() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(
                JobAdvertisementFixture.of(job01.id()),
                JobAdvertisementFixture.of(job02.id())
                        .setPublication(testPublication().setStartDate(LocalDate.now().minusDays(10)).build()),
                JobAdvertisementFixture.of(job03.id())
                        .setPublication(testPublication().setStartDate(LocalDate.now().minusDays(11)).build())
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId("companyId")
                .setOnlineSinceDays(10);

        // WHEN
        post(request, API_JOB_ADVERTISEMENTS + "/_search/managed")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"));
    }

    @Test
    public void shouldSearchManagedJobAdsBeFilteredByOwnerUserId() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(
                JobAdvertisementFixture.of(job01.id()),
                JobAdvertisementFixture.of(job02.id())
                        .setOwner(
                                OwnerFixture.of(job02.id()).setUserId("OwnerUserId").build()
                        )
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId("companyId")
                .setOwnerUserId("OwnerUserId");

        // WHEN
        post(request, API_JOB_ADVERTISEMENTS + "/_search/managed")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"));
    }

    @Test
    public void shouldSearchManagedJobAdsBeFilteredByOwnerCompanyId() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(
                JobAdvertisementFixture.of(job01.id()),
                JobAdvertisementFixture.of(job02.id())
                        .setOwner(
                                OwnerFixture.of(job02.id()).setCompanyId("OwnerCompanyId").build()
                        )
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId("OwnerCompanyId");

        // WHEN
        post(request, API_JOB_ADVERTISEMENTS + "/_search/managed")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"));
    }

    @Test
    public void shouldSearchManagedJobAdsBeFilteredByStatus() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(
                JobAdvertisementFixture.of(job01.id()),
                JobAdvertisementFixture.of(job02.id()).setStatus(REJECTED)
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId("companyId").setState(REJECTED);

        // WHEN
        post(request, API_JOB_ADVERTISEMENTS + "/_search/managed")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"));
    }

    @Test
    public void shouldSearchManagedJobAdBeSearchedByKeywords() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(
                JobAdvertisementFixture.of(job01.id()),
                JobAdvertisementFixture.of(job02.id())
                        .setOwner(
                                OwnerFixture.of(job02.id())
                                        .setUserDisplayName("OwnerUserDisplayName")
                                        .build()),
                JobAdvertisementFixture.of(job03.id())
                        .setJobContent(
                                JobContentFixture.of(job03.id())
                                        .setLocation(testLocation().setCity("Adliswil").build())
                                        .build()),
                JobAdvertisementFixture.of(job04.id())
                        .setJobContent(
                                JobContentFixture.of(job04.id())
                                        .setJobDescriptions(asList(testJobDescription().setTitle("JobDescTitle").build()))
                                        .build()),
                JobAdvertisementFixture.of(job05.id()).setStellennummerAvam("test"),
                JobAdvertisementFixture.of(job06.id())
                        .setStellennummerEgov("StellennummerEgov")
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId("companyId")
                .setKeywordsText(String.join(" ",
                        "OwnerUserDisplayName", "Adli", "JobDescT",
                        "test", "StellennummerEgov"));

        // WHEN
        ResultActions resultActions = post(request, API_JOB_ADVERTISEMENTS + "/_search/managed")
                .andExpect(status().isOk());

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "5"))
                .andExpect(jsonPath("$.[*].owner.userDisplayName").value(hasItem("<em>OwnerUserDisplayName</em>")))
                .andExpect(jsonPath("$.[*].jobContent.location.city").value(hasItem("<em>Adliswil</em>")))
                .andExpect(jsonPath("$.[*].jobContent.jobDescriptions.[*].title").value(hasItem("<em>JobDescTitle</em>")))
                .andExpect(jsonPath("$.[*].stellennummerAvam").value(hasItem("<em>test</em>")))
                .andExpect(jsonPath("$.[*].stellennummerEgov").value(hasItem("<em>StellennummerEgov</em>")));
    }

    @Test
    public void shouldSearchManagedJobAdBeSearchedAndSortedByDateAndCreatedTime() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(

                JobAdvertisementFixture.of(job01.id())
                        .setJobContent(JobContentFixture.of(job01.id())
                                .setJobDescriptions(asList(
                                        testJobDescription().setTitle("desc1").build()
                                ))
                                .build())
                        .setPublication(testPublication().setStartDate(LocalDate.now()).build()),

                JobAdvertisementFixture.of(job02.id())
                        .setJobContent(JobContentFixture.of(job02.id())
                                .setJobDescriptions(asList(
                                        testJobDescription().setTitle("desc2").build()
                                ))
                                .build())
                        .setPublication(testPublication().setStartDate(LocalDate.now()).build()),

                JobAdvertisementFixture.of(job03.id())
                        .setJobContent(JobContentFixture.of(job03.id())
                                .setJobDescriptions(asList(
                                        testJobDescription().setTitle("desc3").build()
                                ))
                                .build())
                        .setPublication(testPublication().setStartDate(LocalDate.now().minusDays(10)).build()),
                JobAdvertisementFixture.of(job04.id())
                        .setJobContent(JobContentFixture.of(job04.id())
                                .setJobDescriptions(asList(
                                        testJobDescription().setTitle("desc4").build()
                                ))
                                .build())
                        .setPublication(testPublication().setStartDate(LocalDate.now()).build()),

                JobAdvertisementFixture.of(job05.id())
                        .setJobContent(JobContentFixture.of(job05.id())
                                .setJobDescriptions(asList(
                                        testJobDescription().setTitle("desc5").build()
                                ))
                                .build())
                        .setPublication(testPublication().setStartDate(LocalDate.now()).build())
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId("companyId");

        // WHEN SORTED ASCENDING
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search/managed")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
                        .param("sort", "jobAdvertisement.publication.startDate,ASC")
        ).andExpect(status().isOk());

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "5"))
                .andExpect(jsonPath("$.[0].jobContent.jobDescriptions[0].title").value(equalTo("desc3")))
                .andExpect(jsonPath("$.[1].jobContent.jobDescriptions[0].title").value(equalTo("desc5")))
                .andExpect(jsonPath("$.[2].jobContent.jobDescriptions[0].title").value(equalTo("desc4")))
                .andExpect(jsonPath("$.[3].jobContent.jobDescriptions[0].title").value(equalTo("desc2")))
                .andExpect(jsonPath("$.[4].jobContent.jobDescriptions[0].title").value(equalTo("desc1")));

        // WHEN SORTED DESCENDING
        resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search/managed")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
                        .param("sort", "jobAdvertisement.publication.startDate,DESC")
        ).andExpect(status().isOk());

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "5"))
                .andExpect(jsonPath("$.[0].jobContent.jobDescriptions[0].title").value(equalTo("desc5")))
                .andExpect(jsonPath("$.[1].jobContent.jobDescriptions[0].title").value(equalTo("desc4")))
                .andExpect(jsonPath("$.[2].jobContent.jobDescriptions[0].title").value(equalTo("desc2")))
                .andExpect(jsonPath("$.[3].jobContent.jobDescriptions[0].title").value(equalTo("desc1")))
                .andExpect(jsonPath("$.[4].jobContent.jobDescriptions[0].title").value(equalTo("desc3")));
    }

    @Test
    public void shouldSearchManagedJobAdBeSearchedAndSortedByTitle() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(
                JobAdvertisementFixture.of(job01.id()),
                JobAdvertisementFixture.of(job02.id())
                        .setJobContent(
                                JobContentFixture.of(job02.id())
                                        .setJobDescriptions(asList(testJobDescription().setTitle("desc1").build()))
                                        .build()),
                JobAdvertisementFixture.of(job03.id())
                        .setJobContent(
                                JobContentFixture.of(job03.id())
                                        .setJobDescriptions(asList(testJobDescription().setTitle("desc3").build()))
                                        .build()),
                JobAdvertisementFixture.of(job04.id())
                        .setJobContent(
                                JobContentFixture.of(job04.id())
                                        .setJobDescriptions(asList(testJobDescription().setTitle("desc2").build()))
                                        .build())
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId("companyId")
                .setKeywordsText("desc");

        // WHEN
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search/managed")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
                        .param("sort", "jobAdvertisement.jobContent.jobDescriptions.title.keyword,DESC")
        )
                .andExpect(status().isOk());

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.[0].jobContent.jobDescriptions[0].title").value(equalTo("<em>desc3</em>")))
                .andExpect(jsonPath("$.[1].jobContent.jobDescriptions[0].title").value(equalTo("<em>desc2</em>")))
                .andExpect(jsonPath("$.[2].jobContent.jobDescriptions[0].title").value(equalTo("<em>desc1</em>")));
    }

    @Test
    public void shouldSearchManagedJobAdBeSearchedNotSortedDesByTitleIfMultipleDescriptions() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(
                JobAdvertisementFixture.of(job01.id()),
                JobAdvertisementFixture.of(job02.id())
                        .setJobContent(
                                JobContentFixture.of(job02.id())
                                        .setJobDescriptions(asList( // be aware of multiple descriptions which break sorting
                                                testJobDescription().setTitle("desc1").build(),
                                                testJobDescription().setTitle("descA").build(),
                                                testJobDescription().setTitle("test").build()
                                        ))
                                        .build()),
                JobAdvertisementFixture.of(job03.id())
                        .setJobContent(
                                JobContentFixture.of(job03.id())
                                        .setJobDescriptions(asList(testJobDescription().setTitle("desc3").build()))
                                        .build()),
                JobAdvertisementFixture.of(job04.id())
                        .setJobContent(
                                JobContentFixture.of(job04.id())
                                        .setJobDescriptions(asList(testJobDescription().setTitle("desc2").build()))
                                        .build())
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId("companyId")
                .setKeywordsText("desc");

        // WHEN
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search/managed")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
                        .param("sort", "jobAdvertisement.jobContent.jobDescriptions.title.keyword,DESC")
        )
                .andExpect(status().isOk());

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.[0].jobContent.jobDescriptions[0].title").value(equalTo("<em>desc1</em>"))) // this title is 1st due to multiple descriptions
                .andExpect(jsonPath("$.[1].jobContent.jobDescriptions[0].title").value(equalTo("<em>desc3</em>")))
                .andExpect(jsonPath("$.[2].jobContent.jobDescriptions[0].title").value(equalTo("<em>desc2</em>")));
    }

    @Test
    public void shouldSearchManagedJobAdBeSortedDescByCity() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(
                JobAdvertisementFixture.of(job01.id()),
                JobAdvertisementFixture.of(job02.id())
                        .setJobContent(
                                JobContentFixture.of(job02.id())
                                        .setLocation(testLocation().setCity("Zurich").build())
                                        .build()),
                JobAdvertisementFixture.of(job03.id())
                        .setJobContent(
                                JobContentFixture.of(job03.id())
                                        .setLocation(testLocation().setCity("Munich").build())
                                        .build()),
                JobAdvertisementFixture.of(job04.id())
                        .setJobContent(
                                JobContentFixture.of(job04.id())
                                        .setLocation(testLocation().setCity("Adliswil").build())
                                        .build()),
                JobAdvertisementFixture.of(job05.id())
                        .setJobContent(
                                JobContentFixture.of(job05.id())
                                        .setLocation(testLocation().setCity("denver").build())
                                        .build())
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId("companyId");

        // WHEN
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search/managed")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
                        .param("sort", "jobAdvertisement.jobContent.location.city.keyword,DESC")
        )
                .andExpect(status().isOk());

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "5"))
                .andExpect(jsonPath("$.[0].jobContent.location.city").value(equalTo("Zurich")))
                .andExpect(jsonPath("$.[1].jobContent.location.city").value(equalTo("Munich")))
                .andExpect(jsonPath("$.[2].jobContent.location.city").value(equalTo("denver")))
                .andExpect(jsonPath("$.[3].jobContent.location.city").value(equalTo("city")))
                .andExpect(jsonPath("$.[4].jobContent.location.city").value(equalTo("Adliswil")));
    }

    @Test
    public void shouldSearchManagedJobAdBeSearchedAndSortedDescByCity() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(
                JobAdvertisementFixture.of(job01.id()),
                JobAdvertisementFixture.of(job02.id())
                        .setJobContent(
                                JobContentFixture.of(job02.id())
                                        .setLocation(testLocation().setCity("ZurichA").build())
                                        .build()),
                JobAdvertisementFixture.of(job03.id())
                        .setJobContent(
                                JobContentFixture.of(job03.id())
                                        .setLocation(testLocation().setCity("ZurichZ").build())
                                        .build()),
                JobAdvertisementFixture.of(job04.id())
                        .setJobContent(
                                JobContentFixture.of(job04.id())
                                        .setLocation(testLocation().setCity("ZurichB").build())
                                        .build())
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId("companyId")
                .setKeywordsText("Zur");

        // WHEN
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search/managed")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
                        .param("sort", "jobAdvertisement.jobContent.location.city.keyword,DESC")
        )
                .andExpect(status().isOk());

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.[0].jobContent.location.city").value(equalTo("<em>ZurichZ</em>")))
                .andExpect(jsonPath("$.[1].jobContent.location.city").value(equalTo("<em>ZurichB</em>")))
                .andExpect(jsonPath("$.[2].jobContent.location.city").value(equalTo("<em>ZurichA</em>")));
    }

    private void saveJobAdvertisementDocuments(JobAdvertisement.Builder... jobAdvertisementBuilders) {
        for (JobAdvertisement.Builder jobAdvertisementBuilder : jobAdvertisementBuilders) {
            index(jobAdvertisementBuilder.build());
        }
    }

    private ResultActions post(Object request, String urlTemplate) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
        );
    }

    private void index(List<JobAdvertisement> jobAdvertisements) {
        jobAdvertisements.forEach(this::index);
    }

    private void index(JobAdvertisement jobAdvertisement) {
        this.jobAdvertisementElasticsearchRepository.save(new JobAdvertisementDocument(jobAdvertisement));
    }
}
