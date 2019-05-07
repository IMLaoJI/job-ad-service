package ch.admin.seco.jobs.services.jobadservice.application.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileResultDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.create.CreateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.CantonFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.SearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.update.UpdateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventMockUtils;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.GeoPointFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileEvents;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.CantonFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.LocalityFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.OccupationFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.OccupationFilterType;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.RadiusSearchFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.SearchFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.Sort;
import com.google.common.collect.ImmutableSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class SearchProfileApplicationServiceTest {

    // TODO
    // Use fixtures

    private DomainEventMockUtils domainEventMockUtils;

    @Autowired
    private SearchProfileRepository searchProfileRepository;

    @Autowired
    private SearchProfileApplicationService searchProfileApplicationService;

    @Before
    public void setUp() {
        this.domainEventMockUtils = new DomainEventMockUtils();
    }

    @After
    public void tearDown() {
        this.searchProfileRepository.deleteAll();
        this.domainEventMockUtils.clearEvents();
    }

    @Test
    public void testGetSearchProfile() {
        // given
        SearchProfileDto createdSearchProfileDto = createSearchProfileDto();

        // when
        SearchProfileId searchProfileId = new SearchProfileId(createdSearchProfileDto.getId());

        // then
        assertThat(this.searchProfileApplicationService.getSearchProfile(searchProfileId).getName()).isEqualTo(createdSearchProfileDto.getName());
        domainEventMockUtils.assertSingleDomainEventPublished(SearchProfileEvents.SEARCH_PROFILE_CREATED.getDomainEventType());
    }

    @Test
    public void testGetSearchProfiles() {
        // given
        final String ownerUserId="User 1";

        CreateSearchProfileDto createSearchProfileDto1 = new CreateSearchProfileDto();
        createSearchProfileDto1.setName("SearchProfile 1");
        createSearchProfileDto1.setOwnerUserId(ownerUserId);
        createSearchProfileDto1.setSearchFilter(SearchFilterDto.toDto(prepareSearchFilter()));

        CreateSearchProfileDto createSearchProfileDto2 = new CreateSearchProfileDto();
        createSearchProfileDto2.setName("SearchProfile 2");
        createSearchProfileDto2.setOwnerUserId(ownerUserId);
        createSearchProfileDto2.setSearchFilter(SearchFilterDto.toDto(prepareSearchFilter()));

        CreateSearchProfileDto createSearchProfileDto3 = new CreateSearchProfileDto();
        createSearchProfileDto3.setName("SearchProfile 3");
        createSearchProfileDto3.setOwnerUserId(ownerUserId);
        createSearchProfileDto3.setSearchFilter(SearchFilterDto.toDto(prepareSearchFilter()));

        // when
        SearchProfileDto createdSearchProfileDto1 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto1);
        SearchProfileDto createdSearchProfileDto2 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto2);
        SearchProfileDto createdSearchProfileDto3 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto3);


        // then
        domainEventMockUtils.assertMultipleDomainEventPublished(3,SearchProfileEvents.SEARCH_PROFILE_CREATED.getDomainEventType());
        Page<SearchProfileResultDto> searchProfileResultDtos = searchProfileApplicationService.getSearchProfiles(ownerUserId,0,100);
        assertThat(searchProfileResultDtos).hasSize(3);
        assertThat(searchProfileResultDtos.getContent().get(0).getId()).isEqualTo(createdSearchProfileDto1.getId());
        assertThat(searchProfileResultDtos.getContent().get(1).getId()).isEqualTo(createdSearchProfileDto2.getId());
        assertThat(searchProfileResultDtos.getContent().get(2).getId()).isEqualTo(createdSearchProfileDto3.getId());
    }

    @Test
    public void testCreate() {
        // given
        CreateSearchProfileDto createSearchProfileDto = new CreateSearchProfileDto();
        createSearchProfileDto.setName("SearchProfile 1");
        createSearchProfileDto.setOwnerUserId("User 1");
        createSearchProfileDto.setSearchFilter(SearchFilterDto.toDto(prepareSearchFilter()));

        // when
        SearchProfileDto createdSearchProfileDto = searchProfileApplicationService.createSearchProfile(createSearchProfileDto);

        // then
        Optional<SearchProfile> createdSearchProfile = searchProfileRepository.findById(new SearchProfileId(createdSearchProfileDto.getId()));
        assertThat(createdSearchProfile).isPresent();
        assertThat(createdSearchProfile.get().getName()).isEqualTo(createSearchProfileDto.getName());
        domainEventMockUtils.assertSingleDomainEventPublished(SearchProfileEvents.SEARCH_PROFILE_CREATED.getDomainEventType());
    }

    @Test
    public void testCreateWithExistingName() {
        // given
        CreateSearchProfileDto createSearchProfileDto1 = new CreateSearchProfileDto();
        createSearchProfileDto1.setName("SearchProfile 1");
        createSearchProfileDto1.setOwnerUserId("User 1");
        createSearchProfileDto1.setSearchFilter(SearchFilterDto.toDto(prepareSearchFilter()));

        CreateSearchProfileDto createSearchProfileDto2 = new CreateSearchProfileDto();
        createSearchProfileDto2.setName("SearchProfile 1");
        createSearchProfileDto2.setOwnerUserId("User 1");
        createSearchProfileDto2.setSearchFilter(SearchFilterDto.toDto(prepareSearchFilter()));

        // when
        searchProfileApplicationService.createSearchProfile(createSearchProfileDto1);

        // then
        assertThatThrownBy(() -> this.searchProfileApplicationService.createSearchProfile(createSearchProfileDto2))
                .isInstanceOf(SearchProfileNameAlreadyExistsException.class)
                .hasMessageContaining("SearchProfile with name " + "SearchProfile 1" + "already exists. " +
                        "Please give rename your new SearchProfile for ownerUserId=" + "User 1" + ".");
    }


    @Test
    public void testUpdateSearchProfile() {
        // given
        CreateSearchProfileDto createSearchProfileDto = new CreateSearchProfileDto();
        createSearchProfileDto.setName("SearchProfile 1");
        createSearchProfileDto.setOwnerUserId("User 1");
        SearchFilterDto searchFilterDto = SearchFilterDto.toDto(prepareSearchFilter());
        createSearchProfileDto.setSearchFilter(searchFilterDto);
        SearchProfileDto createdSearchProfileDto = searchProfileApplicationService.createSearchProfile(createSearchProfileDto);
        domainEventMockUtils.assertSingleDomainEventPublished(SearchProfileEvents.SEARCH_PROFILE_CREATED.getDomainEventType());

        // when
        searchFilterDto.getCantonFilters().clear();
        List<CantonFilterDto> cantonFilterList = Collections.singletonList(
                new CantonFilterDto().setCode("ZH").setName("Zürich"));
        searchFilterDto.setCantonFilters(cantonFilterList);
        UpdateSearchProfileDto updateSearchProfileDto = new UpdateSearchProfileDto(new SearchProfileId(createdSearchProfileDto.getId()), "Another Name", searchFilterDto);
        searchProfileApplicationService.updateSearchProfile(updateSearchProfileDto);

        // then
        Optional<SearchProfile> updatedSearchProfile = searchProfileRepository.findById(new SearchProfileId(createdSearchProfileDto.getId()));
        assertThat(updatedSearchProfile).isPresent();
        assertThat(updatedSearchProfile.get().getName()).isEqualTo(updateSearchProfileDto.getName());
        assertThat(updatedSearchProfile.get().getSearchFilter().getCantonFilters()).contains(new CantonFilter("Zürich", "ZH"));
    }

    @Test
    public void testDeleteSearchProfile() {
        // given
        SearchProfileDto createdSearchProfileDto = createSearchProfileDto();

        // when
        SearchProfileId searchProfileId = new SearchProfileId(createdSearchProfileDto.getId());
        assertThat(this.searchProfileApplicationService.getSearchProfile(searchProfileId).getName()).isEqualTo(createdSearchProfileDto.getName());
        searchProfileApplicationService.deleteSearchProfile(searchProfileId);

        // then
        assertThatThrownBy(() -> this.searchProfileApplicationService.getSearchProfile(searchProfileId))
                .isInstanceOf(SearchProfileNotExitsException.class)
                .hasMessageContaining("Aggregate with ID " + searchProfileId.getValue() + " not found");
    }

    private SearchProfileDto createSearchProfileDto() {
        CreateSearchProfileDto createSearchProfileDto = new CreateSearchProfileDto();
        createSearchProfileDto.setName("SearchProfile 1");
        createSearchProfileDto.setOwnerUserId("User 1");
        SearchFilterDto searchFilterDto = SearchFilterDto.toDto(prepareSearchFilter());
        createSearchProfileDto.setSearchFilter(searchFilterDto);
        return searchProfileApplicationService.createSearchProfile(createSearchProfileDto);
    }

    private SearchFilter prepareSearchFilter() {
        return SearchFilter.builder()
                .setSort(Sort.SCORE)
                .setKeywords(ImmutableSet.of("Keyword-1", "Keyword-2", "Keyword-3", "Keyword-4"))
                .setOccupationFilters(Arrays.asList(
                        new OccupationFilter("Label-1", OccupationFilterType.OCCUPATION),
                        new OccupationFilter("Label-2", OccupationFilterType.OCCUPATION),
                        new OccupationFilter("Label-3", OccupationFilterType.OCCUPATION),
                        new OccupationFilter("Label-4", OccupationFilterType.OCCUPATION),
                        new OccupationFilter("Label-5", OccupationFilterType.CLASSIFICATION)
                ))
                .setLocalityFilters(Arrays.asList(
                        new LocalityFilter("Label-1"),
                        new LocalityFilter("Label-2"),
                        new LocalityFilter("Label-3")
                ))
                .setCantonFilters(Collections.singletonList(
                        new CantonFilter("Bern", "BE")
                ))
                .setRadiusSearchFilter(
                        new RadiusSearchFilter(GeoPointFixture.testGeoPoint(), 20)
                )
                .build();
    }
}