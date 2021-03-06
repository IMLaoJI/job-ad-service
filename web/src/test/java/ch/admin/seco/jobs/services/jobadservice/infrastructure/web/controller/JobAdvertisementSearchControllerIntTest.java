package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.FavouriteItemApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.FavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.create.CreateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.ManagedJobAdSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.ProfessionCode;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.ProfessionCodeType;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.RadiusSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.GeoPointDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.search.JobAdvertisementSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.GeoPoint;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.LanguageLevel;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.LanguageSkill;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Location;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Occupation;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobContentFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.OwnerFixture;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.TestUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.stream.Stream;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_PUBLIC;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_RESTRICTED;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.REJECTED;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem.API;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem.EXTERN;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture.testJobAdvertisement;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobContentFixture.testJobContent;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobDescriptionFixture.testJobDescription;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.LocationFixture.testLocation;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.PublicationFixture.testPublication;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.JobAdvertisementWithLocationsFixture.listOfJobAdsForAbroadSearchTests;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.JobAdvertisementWithLocationsFixture.listOfJobAdsForCloseRangeDistanceTests;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.JobAdvertisementWithLocationsFixture.listOfJobAdsForDecayingScoreSearchTests;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.JobAdvertisementWithLocationsFixture.listOfJobAdsForGeoDistanceTests;
import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.CombinableMatcher.both;
import static org.junit.Assume.assumeTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
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

    private static final GeoPointDto KÖNIZ_GEO_POINT = new GeoPointDto().setLat(46.921).setLon(7.413);

    private static final GeoPointDto LAUSANNE_GEO_POINT = new GeoPointDto().setLat(46.552043).setLon(6.6523078);

    private static final GeoPointDto SION_GEO_POINT = new GeoPointDto().setLat(46.234).setLon(7.359);

    private static final String EXTERNAL_CODE_KOCH = "11000411";

    @Value("${alv.feature.toggle.isGaussDecayEnabled}")
    boolean isGaussDecayEnabled;

    @Qualifier("jobAdvertisementRepository")
    @Autowired
    private JobAdvertisementRepository jobAdvertisementJpaRepository;

    @Autowired
    private JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

    @Autowired
    private FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository;

    @Autowired
    private FavouriteItemApplicationService favouriteItemApplicationService;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.favouriteItemElasticsearchRepository.deleteAll();
        this.jobAdvertisementElasticsearchRepository.deleteAll();
        this.jobAdvertisementJpaRepository.deleteAll();
    }

    @Test
    public void shouldSearchWithoutQuery() throws Exception {
        // GIVEN
        index(createJob(job01.id()));
        index(createJob(job02.id()));
        index(createJob(job03.id()));

        // WHEN
        post(new JobAdvertisementSearchRequest(), API_JOB_ADVERTISEMENTS+ "/_search")
                // THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"));
    }


    @Test
    public void shouldDecayScoreAfterInitialOffsetOf2Km() throws Exception {

        // GIVEN
        assumeTrue(isGaussDecayEnabled);
        index(listOfJobAdsForCloseRangeDistanceTests());

        // WHEN
        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest()
        .setCommunalCodes(new String[]{"355"})
        .setRadiusSearchRequest(new RadiusSearchRequest().setGeoPoint(KÖNIZ_GEO_POINT).setDistance(30));

        post(jobAdvertisementSearchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"))

                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo("job01")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.location.city").value(equalTo("Köniz")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.jobDescriptions[0].title").value(equalTo("Koch")))

                .andExpect(jsonPath("$.[1].jobAdvertisement.id").value(equalTo("job03")))
                .andExpect(jsonPath("$.[1].jobAdvertisement.jobContent.location.city").value(equalTo("Bern")))
                .andExpect(jsonPath("$.[1].jobAdvertisement.jobContent.jobDescriptions[0].title").value(equalTo("Koch")))

                .andExpect(jsonPath("$.[2].jobAdvertisement.id").value(equalTo("job02")))
                .andExpect(jsonPath("$.[2].jobAdvertisement.jobContent.location.city").value(equalTo("Ostermundigen")))
                .andExpect(jsonPath("$.[2].jobAdvertisement.jobContent.jobDescriptions[0].title").value(equalTo("Koch")));
    }


    @Test
    public void shouldIgnoreGeoDistanceWhenNoRadiusSearchRequestIsProvided() throws Exception {
        // GIVEN
        index(listOfJobAdsForGeoDistanceTests());

        // WHEN
        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest()
        .setRadiusSearchRequest(null);

        // THEN
        this.post(jobAdvertisementSearchRequest, API_JOB_ADVERTISEMENTS + "/_search")
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
        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest()
        .setRadiusSearchRequest(new RadiusSearchRequest().setGeoPoint(BERN_GEO_POINT).setDistance(150));

        // THEN
        post(jobAdvertisementSearchRequest, API_JOB_ADVERTISEMENTS + "/_search")
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

        // THEN
        post(jobAdvertisementSearchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo("job01")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.location.city").value(equalTo("Bern")));
    }

    @Test
    public void shouldSearchForJobsIn20KmRadiusOfLausanne() throws Exception {
        // GIVEN
        index(listOfJobAdsForGeoDistanceTests());

        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest()
        .setCommunalCodes(new String[]{LAUSANNE_COMMUNAL_CODE})
        .setRadiusSearchRequest(new RadiusSearchRequest().setGeoPoint(LAUSANNE_GEO_POINT).setDistance(20));

        // THEN
        post(jobAdvertisementSearchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo("job04")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.location.city").value(equalTo("Lausanne")));
    }

    @Test
    public void shouldSearchForJobsIn80KmRadiusOfSion() throws Exception {
        // GIVEN
        index(listOfJobAdsForGeoDistanceTests());


        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest()
        .setCommunalCodes(new String[]{SION_COMMUNAL_CODE})
        .setRadiusSearchRequest(new RadiusSearchRequest().setGeoPoint(SION_GEO_POINT).setDistance(80));

        // THEN
        post(jobAdvertisementSearchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"));
    }

    @Test
    public void shouldSearchForAbroadAndBernJobs() throws Exception {
        // GIVEN
        index(listOfJobAdsForAbroadSearchTests());

        // WHEN
        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest()
        .setCommunalCodes(new String[]{ABROAD_COMMUNAL_CODE, BERN_COMMUNAL_CODE});

        // THEN
        post(jobAdvertisementSearchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "4"))
                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo("job04")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.location.city").value(equalTo("Ausland")))
                .andExpect(jsonPath("$.[1].jobAdvertisement.id").value(equalTo("job03")))
                .andExpect(jsonPath("$.[1].jobAdvertisement.jobContent.location.city").value(equalTo("Ausland")))
                .andExpect(jsonPath("$.[2].jobAdvertisement.id").value(equalTo("job02")))
                .andExpect(jsonPath("$.[2].jobAdvertisement.jobContent.location.city").value(equalTo("Ausland")))
                .andExpect(jsonPath("$.[3].jobAdvertisement.id").value(equalTo("job01")))
                .andExpect(jsonPath("$.[3].jobAdvertisement.jobContent.location.city").value(equalTo("Bern")));
    }

    @Test
    public void shouldSearchForMultipleLocationsInSwitzerland() throws Exception {
        // GIVEN
        index(listOfJobAdsForAbroadSearchTests());

        // WHEN
        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest()
        .setCommunalCodes(new String[]{LAUSANNE_COMMUNAL_CODE, BERN_COMMUNAL_CODE});

        // THEN
        post(jobAdvertisementSearchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo("job06")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.location.city").value(equalTo("Lausanne")))
                .andExpect(jsonPath("$.[1].jobAdvertisement.id").value(equalTo("job01")))
                .andExpect(jsonPath("$.[1].jobAdvertisement.jobContent.location.city").value(equalTo("Bern")));
    }

    @Test
    public void shouldDecayTheScoreOfResultsBasedOnDistanceOfInitialGeoPoint() throws Exception {

        // GIVEN
        // DISTANCES:
        // Bern - Bern - 0km
        // Bern - Lausanne 78.33km
        // Bern - Sion 79km,
        // Bern - Zurich 95km

        assumeTrue(isGaussDecayEnabled);
        index(listOfJobAdsForGeoDistanceTests());

        // WHEN
        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest()
                .setCommunalCodes(new String[]{BERN_COMMUNAL_CODE})
                .setRadiusSearchRequest(new RadiusSearchRequest().setGeoPoint(BERN_GEO_POINT).setDistance(100));

        // THEN
        post(jobAdvertisementSearchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "4"))
                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo("job01")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.location.city").value(equalTo("Bern")))


                .andExpect(jsonPath("$.[1].jobAdvertisement.id").value(equalTo("job04")))
                .andExpect(jsonPath("$.[1].jobAdvertisement.jobContent.location.city").value(equalTo("Lausanne")))

                .andExpect(jsonPath("$.[2].jobAdvertisement.id").value(equalTo("job03")))
                .andExpect(jsonPath("$.[2].jobAdvertisement.jobContent.location.city").value(equalTo("Sion")))

                .andExpect(jsonPath("$.[3].jobAdvertisement.id").value(equalTo("job02")))
                .andExpect(jsonPath("$.[3].jobAdvertisement.jobContent.location.city").value(equalTo("Zürich")));

    }


    @Test
    public void shouldDecayTheScoreOfResultsBasedOnDistanceOfInitialGeoPointOnKeywordSearch() throws Exception {



        index(listOfJobAdsForDecayingScoreSearchTests());
        assumeTrue(isGaussDecayEnabled);
        // WHEN
        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest().
                setCommunalCodes(new String[]{LAUSANNE_COMMUNAL_CODE})
                .setKeywords(new String[]{"Koch"})
                .setRadiusSearchRequest(new RadiusSearchRequest().setGeoPoint(LAUSANNE_GEO_POINT).setDistance(150));

        // THEN
        post(jobAdvertisementSearchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"))

                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo("job05")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.location.city").value(equalTo("Lausanne")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.jobDescriptions[0].title").value(equalTo("<em>Koch</em>")))

                .andExpect(jsonPath("$.[1].jobAdvertisement.id").value(equalTo("job07")))
                .andExpect(jsonPath("$.[1].jobAdvertisement.jobContent.location.city").value(equalTo("Sion")))
                .andExpect(jsonPath("$.[1].jobAdvertisement.jobContent.jobDescriptions[0].title").value(equalTo("<em>Koch</em>")))

                .andExpect(jsonPath("$.[2].jobAdvertisement.id").value(equalTo("job06")))
                .andExpect(jsonPath("$.[2].jobAdvertisement.jobContent.location.city").value(equalTo("Bern")))
                .andExpect(jsonPath("$.[2].jobAdvertisement.jobContent.jobDescriptions[0].title").value(equalTo("<em>Koch</em>")));
    }

    @Test
    public void shouldFilterJobsOutsideOfRadiusOnOccupationSearch() throws Exception {
        //GIVEN
        index(listOfJobAdsForDecayingScoreSearchTests());

        // WHEN
        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest()
        .setCommunalCodes(new String[]{LAUSANNE_COMMUNAL_CODE})
        .setProfessionCodes(new ProfessionCode[]{new ProfessionCode(ProfessionCodeType.X28, EXTERNAL_CODE_KOCH)})
        .setRadiusSearchRequest(new RadiusSearchRequest().setGeoPoint(LAUSANNE_GEO_POINT).setDistance(30));

        // THEN
        post(jobAdvertisementSearchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))

                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo("job05")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.location.city").value(equalTo("Lausanne")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.jobDescriptions[0].title").value(equalTo("Koch")));
    }

    @Test
    public void shouldFilterSwissJobsOnOccupationSearchForAbroadJobs() throws Exception {
        //GIVEN
        index(listOfJobAdsForDecayingScoreSearchTests());
        index(testJobAdvertisementWithContentAndLocation(job05.id(),
                testJobContent()
                        .setJobDescriptions(singletonList(testJobDescription().setTitle("Koch").build()))
                        .setX28OccupationCodes(EXTERNAL_CODE_KOCH)
                        .setLocation(
                                testLocation()
                                        .setCity("Ausland")
                                        .setCommunalCode(null)
                                        .setRegionCode(null)
                                        .setCantonCode(null)
                                        .setPostalCode("91244")
                                        .setCountryIsoCode("FR")
                                        .build()).build()));
        // WHEN
        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest()
        .setCommunalCodes(new String[]{ABROAD_COMMUNAL_CODE})
        .setProfessionCodes(new ProfessionCode[]{new ProfessionCode(ProfessionCodeType.X28, EXTERNAL_CODE_KOCH)});

        // THEN
        post(jobAdvertisementSearchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo("job05")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.location.city").value(equalTo("Ausland")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.jobDescriptions[0].title").value(equalTo("Koch")));
    }

    @Test
    public void jobWithMoreKeywordHitsShouldHaveHigherScore() throws Exception {
        //GIVEN
        index(testJobAdvertisementWithContentAndLocation(job05.id(),
                testJobContent()
                        .setJobDescriptions(singletonList(testJobDescription()
                                .setTitle("Koch")
                                .setDescription("keyword1, keyword3")
                                .build()))
                        .setX28OccupationCodes(EXTERNAL_CODE_KOCH)
                        .setLocation(
                                testLocation()
                                        .setCity("Lausanne")
                                        .setCommunalCode("5586")
                                        .setRegionCode("VD01")
                                        .setCantonCode("VD")
                                        .setPostalCode("1000")
                                        .setCountryIsoCode("CH")
                                        .setCoordinates(new GeoPoint(6.6523078, 46.552043))
                                        .build()).build()));
        index(testJobAdvertisementWithContentAndLocation(job06.id(),
                testJobContent()
                        .setJobDescriptions(singletonList(testJobDescription()
                                .setTitle("Koch")
                                .setDescription("keyword1, keyword2, keyword3").build()))
                        .setX28OccupationCodes(EXTERNAL_CODE_KOCH)
                        .setLocation(
                                testLocation()
                                        .setCity("Bern")
                                        .setCommunalCode("351")
                                        .setRegionCode("BE01")
                                        .setCantonCode("BE")
                                        .setPostalCode("3000")
                                        .setCountryIsoCode("CH")
                                        .setCoordinates(new GeoPoint(7.441, 46.948))
                                        .build()).build()));
        // WHEN
        JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest()
        .setCommunalCodes(new String[]{LAUSANNE_COMMUNAL_CODE})
        .setRadiusSearchRequest(new RadiusSearchRequest().setGeoPoint(LAUSANNE_GEO_POINT).setDistance(100))
        .setKeywords(new String[]{"keyword1", "keyword2", "keyword3"});

        // THEN
        post(jobAdvertisementSearchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo("job06")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.location.city").value(equalTo("Bern")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.jobDescriptions[0].title").value(equalTo("Koch")))
				.andExpect(jsonPath("$.[1].jobAdvertisement.id").value(equalTo("job05")))
				.andExpect(jsonPath("$.[1].jobAdvertisement.jobContent.location.city").value(equalTo("Lausanne")))
				.andExpect(jsonPath("$.[1].jobAdvertisement.jobContent.jobDescriptions[0].title").value(equalTo("Koch")));

    }

    @Test
    public void shouldFilterJobsOutsideOfCantonOnOccupationSearch() throws Exception {
        // GIVEN
        index(listOfJobAdsForDecayingScoreSearchTests());

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest()
        .setCantonCodes(new String[]{"BE"})
        .setProfessionCodes(new ProfessionCode[]{new ProfessionCode(ProfessionCodeType.X28, EXTERNAL_CODE_KOCH)});

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo("job06")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.location.city").value(equalTo("Bern")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.jobDescriptions[0].title").value(equalTo("Koch")));
    }

    @Test
    public void shouldShowAbroadAndCantonBernJobs() throws Exception {
        // GIVEN
        index(listOfJobAdsForAbroadSearchTests());

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest()
        .setCantonCodes(new String[]{"BE"})
        .setCommunalCodes(new String[]{ABROAD_COMMUNAL_CODE});

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "4"));
    }

    @Test
    public void shouldSearchByKeyword() throws Exception {
        // GIVEN
        index(createJobWithDescription(job01.id(), "c++ developer", "c++ & java entwickler"));
        index(createJobWithDescription(job02.id(), "java & javascript developer", "jee entwickler"));
        index(createJobWithDescription(job03.id(), "php programmierer", "php programierer"));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest()
        .setKeywords(new String[]{"entwickler", "java"});

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))

                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo(job01.name())))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.jobDescriptions[0].title").value(equalTo("c++ developer")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.jobDescriptions[0].description").value(equalTo("c++ &amp; <em>java</em> <em>entwickler</em>")))

                .andExpect(jsonPath("$.[1].jobAdvertisement.id").value(equalTo(job02.name())))
                .andExpect(jsonPath("$.[1].jobAdvertisement.jobContent.jobDescriptions[0].title").value(equalTo("<em>java</em> & <em>javascript</em> developer")))
                .andExpect(jsonPath("$.[1].jobAdvertisement.jobContent.jobDescriptions[0].description").value(equalTo("jee <em>entwickler</em>")));
    }

    @Test
    public void shouldSearchBySourceSystemKeyword() throws Exception {
        // GIVEN
        index(createJobWithDescription(job01.id(), "c++ developer", "c++ & java entwickler", EXTERN, OwnerFixture.of(job01.id()).build()));
        index(createJobWithDescription(job02.id(), "java & javascript developer", "jee entwickler", API, OwnerFixture.of(job02.id()).build()));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest()
        .setKeywords(new String[]{"*extern"});

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))

                .andExpect(jsonPath("$.[0].jobAdvertisement.id").value(equalTo(job01.name())))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.jobDescriptions[0].title").value(equalTo("c++ developer")))
                .andExpect(jsonPath("$.[0].jobAdvertisement.jobContent.jobDescriptions[0].description").value(equalTo("c++ &amp; java entwickler")));
    }

    @Test
    public void shouldSearchByLanguageSkillKeyword() throws Exception {
        // GIVEN
        final LanguageSkill da = new LanguageSkill.Builder().setLanguageIsoCode("da").setSpokenLevel(LanguageLevel.PROFICIENT).setWrittenLevel(LanguageLevel.INTERMEDIATE).build();
        final LanguageSkill en = new LanguageSkill.Builder().setLanguageIsoCode("en").setSpokenLevel(LanguageLevel.PROFICIENT).setWrittenLevel(LanguageLevel.INTERMEDIATE).build();

        index(createJobWithLanguageSkills(job01.id(), "c++ developer", "c++ & java entwickler", EXTERN, da));
        index(createJobWithLanguageSkills(job02.id(), "java & javascript developer", "jee entwickler", API, en));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest()
        .setKeywords(new String[]{"dänisch"});

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
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
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest()
        .setProfessionCodes(new ProfessionCode[]{new ProfessionCode(ProfessionCodeType.BFS, DEFAULT_BFS_CODE)});

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job01.name())))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job04.name())))
        ;
    }

    @Test
    public void shouldSearchByOccupation_External() throws Exception {

        // GIVEN
        index(createJobWithExternalCode(job01.id(), "1111,2222"));
        index(createJobWithExternalCode(job02.id(), "1111"));
        index(createJobWithExternalCode(job03.id(), "3333"));
        index(createJobWithExternalCode(job04.id(), "4444"));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest()
        .setProfessionCodes(new ProfessionCode[]{
                new ProfessionCode(ProfessionCodeType.X28, "1111"),
                new ProfessionCode(ProfessionCodeType.X28, "44")
        });

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job01.name())))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job02.name())));
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
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest()
        .setCantonCodes(new String[]{"BE"});

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job01.name())))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job02.name())))
        ;
    }

    @Test
    public void shouldFilterByWorkingTimeMinMax() throws Exception {
        // GIVEN
        index(createJobWithWorkload(job01.id(), 1, 100));
        index(createJobWithWorkload(job02.id(), 80, 100));
        index(createJobWithWorkload(job03.id(), 50, 50));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest()
        .setWorkloadPercentageMin(60)
        .setWorkloadPercentageMax(80);

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job01.name())))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job02.name())))
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
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest()
        .setCompanyName("goes");

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job02.name())))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job03.name())))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job04.name())));
    }

    @Test
    public void shouldFilterByPermanentContractType() throws Exception {
        // GIVEN
        index(createJob(job01.id()));
        index(createJobWithContractType(job02.id(), true));
        index(createJob(job03.id()));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest()
        .setPermanent(true);

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.*.jobAdvertisement.id").value(job02.name()))
        ;
    }

    @Test
    @WithJobSeeker
    public void shouldFilterByDisplayRestricted() throws Exception {
        // GIVEN
        index(createJob(job01.id()));
        index(createRestrictedJob(job02.id()));
        index(createJob(job03.id()));

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest()
        .setDisplayRestricted(true);

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.*jobAdvertisement.id").value(job02.name()))
        ;
    }

    @Test
    @WithJobSeeker
    public void shouldFilterByPublicationForJobSeeker() throws Exception {
        // GIVEN
        //-------------------------------------------------------------------------------publicDisplay  restrictedDisplay
        index(createJobWithoutPublicDisplayAndWithoutRestrictedDisplay(job01.id()));   //0 0
        index(createJobWithoutPublicDisplayAndWithRestrictedDisplay(job02.id()));      //0 1
        index(createJobWithPublicDisplayAndWithRestrictedDisplay(job03.id()));         //1 1
        index(createJobWithPublicDisplayAndWithoutRestrictedDisplay(job04.id()));      //1 0

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job02.name())))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job03.name())))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job04.name())))
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

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job03.name())))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job04.name())))
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

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.*.jobAdvertisement.id").value(job02.name()));
    }

    @Test
    @WithJobSeeker
    public void shouldShowRestrictedJobsForJobSeekers() throws Exception {
        // GIVEN
        //-----------------------------------------------------------------------------------publicDisplay restrictedDisplay status
        index(createJob(job01.id()));                                                           //1 0 PUBLISHED_PUBLIC
        index(createRestrictedJob(job02.id()));                                                 //1 0 PUBLISHED_RESTRICTED
        index(createRestrictedJobWithoutPublicDisplayAndWithoutRestrictedDisplay(job03.id()));  //0 0 PUBLISHED_RESTRICTED
        index(createRestrictedJobWithoutPublicDisplayAndWithRestrictedDisplay(job04.id()));     //0 1 PUBLISHED_RESTRICTED
        index(createJobWithoutPublicDisplayAndWithoutRestrictedDisplay(job05.id()));            //0 0 PUBLISHED_PUBLIC
        index(createJobWithoutPublicDisplayAndWithRestrictedDisplay(job06.id()));               //0 1 PUBLISHED_PUBLIC

        // WHEN
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "5"))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job01.name())))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job02.name())))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job03.name())))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job04.name())))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(hasItem(job06.name())));
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
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest()
        .setEuresDisplay(true);

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(
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
        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest()
        .setEuresDisplay(false);

        // THEN
        post(searchRequest, API_JOB_ADVERTISEMENTS + "/_search" )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "4"))
                .andExpect(jsonPath("$.[*].jobAdvertisement.id").value(
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
    @WithCompanyUser
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
                .setCompanyId(WithCompanyUser.USER_ID);

        // WHEN
        post(request, API_JOB_ADVERTISEMENTS + "/_search/managed")
                .andExpect(
                        status().isBadRequest());
    }

    @Test
    @WithCompanyUser
    public void shouldSearchManagedJobAdsBeFilteredByPublicationStartDate() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(
                JobAdvertisementFixture.of(job01.id()),
                JobAdvertisementFixture.of(job02.id())
                        .setPublication(testPublication().setStartDate(now().minusDays(20)).build()),
                JobAdvertisementFixture.of(job03.id())
                        .setPublication(testPublication().setStartDate(now().minusDays(15)).build())
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId(WithCompanyUser.USER_COMPANY_ID)
                .setOnlineSinceDays(10);

        // WHEN
        post(request, API_JOB_ADVERTISEMENTS + "/_search/managed")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"));
    }

    @Test
    @WithCompanyUser
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
                .setCompanyId(WithCompanyUser.USER_COMPANY_ID)
                .setOwnerUserId("OwnerUserId");

        // WHEN
        post(request, API_JOB_ADVERTISEMENTS + "/_search/managed")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"));
    }

    @Test
    @WithCompanyUser
    public void shouldSearchManagedJobAdsBeFilteredByOwnerCompanyId() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(
                JobAdvertisementFixture.of(job01.id()),
                JobAdvertisementFixture.of(job02.id())
                        .setOwner(
                                OwnerFixture.of(job02.id()).setCompanyId("XXX").build()
                        )
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId(WithCompanyUser.USER_COMPANY_ID);

        // WHEN
        post(request, API_JOB_ADVERTISEMENTS + "/_search/managed")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"));
    }

    @Test
    @WithCompanyUser
    public void shouldSearchManagedJobAdsBeFilteredByStatus() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(
                JobAdvertisementFixture.of(job01.id()),
                JobAdvertisementFixture.of(job02.id()).setStatus(REJECTED)
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId(WithCompanyUser.USER_COMPANY_ID).setState(REJECTED);

        // WHEN
        post(request, API_JOB_ADVERTISEMENTS + "/_search/managed")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"));
    }

    @Test
    @WithCompanyUser
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
                                        .setJobDescriptions(singletonList(testJobDescription().setTitle("JobDescTitle").build()))
                                        .build()),
                JobAdvertisementFixture.of(job05.id()).setStellennummerAvam("test"),
                JobAdvertisementFixture.of(job06.id())
                        .setStellennummerEgov("StellennummerEgov")
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId(WithCompanyUser.USER_COMPANY_ID)
                .setKeywordsText(String.join(" ",
                        "OwnerUserDisplayName", "Adli", "JobDescT",
                        "test", "StellennummerEgov"));

        post(request, API_JOB_ADVERTISEMENTS + "/_search/managed")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "5"))
                .andExpect(jsonPath("$.[*].owner.userDisplayName").value(hasItem("<em>OwnerUserDisplayName</em>")))
                .andExpect(jsonPath("$.[*].jobContent.location.city").value(hasItem("Adliswil")))
                .andExpect(jsonPath("$.[*].jobContent.jobDescriptions.[*].title").value(hasItem("JobDescTitle")))
                .andExpect(jsonPath("$.[*].stellennummerAvam").value(hasItem("<em>test</em>")))
                .andExpect(jsonPath("$.[*].stellennummerEgov").value(hasItem("<em>StellennummerEgov</em>")));
    }

    @Test
    @WithCompanyUser
    public void shouldSearchManagedJobAdBeSearchedAndSortedByDateAndCreatedTime() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(

                JobAdvertisementFixture.of(job01.id())
                        .setJobContent(JobContentFixture.of(job01.id())
                                .setJobDescriptions(singletonList(
                                        testJobDescription().setTitle("desc1").build()
                                ))
                                .build())
                        .setPublication(testPublication().setStartDate(now()).build()),

                JobAdvertisementFixture.of(job02.id())
                        .setJobContent(JobContentFixture.of(job02.id())
                                .setJobDescriptions(singletonList(
                                        testJobDescription().setTitle("desc2").build()
                                ))
                                .build())
                        .setPublication(testPublication().setStartDate(now()).build()),

                JobAdvertisementFixture.of(job03.id())
                        .setJobContent(JobContentFixture.of(job03.id())
                                .setJobDescriptions(singletonList(
                                        testJobDescription().setTitle("desc3").build()
                                ))
                                .build())
                        .setPublication(testPublication().setStartDate(now().minusDays(10)).build()),
                JobAdvertisementFixture.of(job04.id())
                        .setJobContent(JobContentFixture.of(job04.id())
                                .setJobDescriptions(singletonList(
                                        testJobDescription().setTitle("desc4").build()
                                ))
                                .build())
                        .setPublication(testPublication().setStartDate(now()).build()),

                JobAdvertisementFixture.of(job05.id())
                        .setJobContent(JobContentFixture.of(job05.id())
                                .setJobDescriptions(singletonList(
                                        testJobDescription().setTitle("desc5").build()
                                ))
                                .build())
                        .setPublication(testPublication().setStartDate(now()).build())
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId(WithCompanyUser.USER_COMPANY_ID);

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
    @WithCompanyUser
    public void shouldSearchManagedJobAdBeSearchedAndSortedByTitle() throws Exception {
        // GIVEN
        saveJobAdvertisementDocuments(
                JobAdvertisementFixture.of(job01.id()),
                JobAdvertisementFixture.of(job02.id())
                        .setJobContent(
                                JobContentFixture.of(job02.id())
                                        .setJobDescriptions(singletonList(testJobDescription().setTitle("desc1").build()))
                                        .build()),
                JobAdvertisementFixture.of(job03.id())
                        .setJobContent(
                                JobContentFixture.of(job03.id())
                                        .setJobDescriptions(singletonList(testJobDescription().setTitle("desc3").build()))
                                        .build()),
                JobAdvertisementFixture.of(job04.id())
                        .setJobContent(
                                JobContentFixture.of(job04.id())
                                        .setJobDescriptions(singletonList(testJobDescription().setTitle("desc2").build()))
                                        .build())
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId(WithCompanyUser.USER_COMPANY_ID)
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
                .andExpect(jsonPath("$.[0].jobContent.jobDescriptions[0].title").value(equalTo("desc3")))
                .andExpect(jsonPath("$.[1].jobContent.jobDescriptions[0].title").value(equalTo("desc2")))
                .andExpect(jsonPath("$.[2].jobContent.jobDescriptions[0].title").value(equalTo("desc1")));
    }

    @Test
    @WithCompanyUser
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
                                        .setJobDescriptions(singletonList(testJobDescription().setTitle("desc3").build()))
                                        .build()),
                JobAdvertisementFixture.of(job04.id())
                        .setJobContent(
                                JobContentFixture.of(job04.id())
                                        .setJobDescriptions(singletonList(testJobDescription().setTitle("desc2").build()))
                                        .build())
        );

        ManagedJobAdSearchRequest request = new ManagedJobAdSearchRequest()
                .setCompanyId(WithCompanyUser.USER_COMPANY_ID)
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
                .andExpect(jsonPath("$.[0].jobContent.jobDescriptions[0].title").value(equalTo("desc1"))) // this title is 1st due to multiple descriptions
                .andExpect(jsonPath("$.[1].jobContent.jobDescriptions[0].title").value(equalTo("desc3")))
                .andExpect(jsonPath("$.[2].jobContent.jobDescriptions[0].title").value(equalTo("desc2")));
    }

    @Test
    @WithCompanyUser
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
                .setCompanyId(WithCompanyUser.USER_COMPANY_ID);

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
    @WithCompanyUser
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
                .setCompanyId(WithCompanyUser.USER_COMPANY_ID)
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
                .andExpect(jsonPath("$.[0].jobContent.location.city").value(equalTo("ZurichZ")))
                .andExpect(jsonPath("$.[1].jobContent.location.city").value(equalTo("ZurichB")))
                .andExpect(jsonPath("$.[2].jobContent.location.city").value(equalTo("ZurichA")));
    }

    @Test
    @WithCompanyUser
    public void searchJobAdvertisementWithFavouriteItem() throws Exception {
        JobAdvertisement jobAdvertisement = createJob(job01.id());
        jobAdvertisementJpaRepository.save(jobAdvertisement);
        this.index(jobAdvertisement);
        await().until(() -> jobAdvertisementElasticsearchRepository.findById(job01.id().getValue()).isPresent());

        CreateFavouriteItemDto createFavouriteItemDto = new CreateFavouriteItemDto();
        createFavouriteItemDto.setJobAdvertisementId(job01.id());
        createFavouriteItemDto.setOwnerUserId(WithCompanyUser.USER_ID);
        FavouriteItemDto favouriteItemDto = this.favouriteItemApplicationService.create(createFavouriteItemDto);
        FavouriteItemId favouriteItemId = new FavouriteItemId(favouriteItemDto.getId());
        await().until(() -> favouriteItemElasticsearchRepository.findById(job01.id(), favouriteItemId).isPresent());


        JobAdvertisementSearchRequest searchRequest = new JobAdvertisementSearchRequest();

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(API_JOB_ADVERTISEMENTS + "/_search")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(searchRequest))
        )
                .andExpect(status().isOk());

        // then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.[0].favouriteItem.id").value(equalTo(favouriteItemId.getValue())));
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
