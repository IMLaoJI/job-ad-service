package ch.admin.seco.jobs.services.jobadservice.application.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileResultDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.create.CreateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.CantonFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.SearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.update.UpdateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventMockUtils;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileEvents;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.CantonFilter;
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
import java.util.List;
import java.util.Optional;

import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileIdFixture.search_profile_01;
import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileIdFixture.search_profile_02;
import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileIdFixture.search_profile_03;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class SearchProfileApplicationServiceTest {

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
        SearchProfileDto createdSearchProfileDto = searchProfileApplicationService.createSearchProfile(getCreateSearchProfileDto(search_profile_01.id()));

        // when
        SearchProfileId searchProfileId = new SearchProfileId(createdSearchProfileDto.getId());

        // then
        assertThat(this.searchProfileApplicationService.getSearchProfile(searchProfileId).getName()).isEqualTo(createdSearchProfileDto.getName());
        domainEventMockUtils.assertSingleDomainEventPublished(SearchProfileEvents.SEARCH_PROFILE_CREATED.getDomainEventType());
    }

    @Test
    public void testGetSearchProfiles() {
        // given
        CreateSearchProfileDto createSearchProfileDto1 = getCreateSearchProfileDto(search_profile_01.id());
        CreateSearchProfileDto createSearchProfileDto2 = getCreateSearchProfileDto(search_profile_02.id());
        CreateSearchProfileDto createSearchProfileDto3 = getCreateSearchProfileDto(search_profile_03.id());

        // when
        SearchProfileDto createdSearchProfileDto1 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto1);
        SearchProfileDto createdSearchProfileDto2 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto2);
        SearchProfileDto createdSearchProfileDto3 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto3);

        // then
        domainEventMockUtils.assertMultipleDomainEventPublished(3,SearchProfileEvents.SEARCH_PROFILE_CREATED.getDomainEventType());
        Page<SearchProfileResultDto> searchProfileResultDtos = searchProfileApplicationService.getSearchProfiles(createSearchProfileDto1.getOwnerUserId(), 0, 100);
        assertThat(searchProfileResultDtos).hasSize(3);
        assertThat(searchProfileResultDtos.getContent().get(0).getId()).isEqualTo(createdSearchProfileDto1.getId());
        assertThat(searchProfileResultDtos.getContent().get(1).getId()).isEqualTo(createdSearchProfileDto2.getId());
        assertThat(searchProfileResultDtos.getContent().get(2).getId()).isEqualTo(createdSearchProfileDto3.getId());
    }

    @Test
    public void testCreate() {
        // given
        CreateSearchProfileDto createSearchProfileDto = getCreateSearchProfileDto(search_profile_01.id());

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
        CreateSearchProfileDto createSearchProfileDto1 = getCreateSearchProfileDto(search_profile_01.id());
        CreateSearchProfileDto createSearchProfileDto2 = getCreateSearchProfileDto(search_profile_01.id());

        // when
        searchProfileApplicationService.createSearchProfile(createSearchProfileDto1);

        // then
        assertThatThrownBy(() -> this.searchProfileApplicationService.createSearchProfile(createSearchProfileDto2))
                .isInstanceOf(SearchProfileNameAlreadyExistsException.class)
                .hasMessageContaining("SearchProfile with name " + createSearchProfileDto2.getName() + "already exists. " +
                        "Please give rename your new SearchProfile for ownerUserId=" + createSearchProfileDto2.getOwnerUserId() + ".");
    }


    @Test
    public void testUpdateSearchProfile() {
        // given
        CreateSearchProfileDto createSearchProfileDto = getCreateSearchProfileDto(search_profile_01.id());
        SearchProfileDto createdSearchProfileDto = searchProfileApplicationService.createSearchProfile(createSearchProfileDto);
        domainEventMockUtils.assertSingleDomainEventPublished(SearchProfileEvents.SEARCH_PROFILE_CREATED.getDomainEventType());

        // when
        SearchFilterDto searchFilterDto = createdSearchProfileDto.getSearchFilter();
        List<CantonFilterDto> cantonFilterList = Arrays.asList(
                new CantonFilterDto().setCode("ZH").setName("Zürich"));
        searchFilterDto.setCantonFilters(cantonFilterList);
        UpdateSearchProfileDto updateSearchProfileDto = new UpdateSearchProfileDto(new SearchProfileId(createdSearchProfileDto.getId()), "Another Name", searchFilterDto, getCreateSearchProfileDto(search_profile_01.id()).getOwnerUserId());
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
        SearchProfileDto createdSearchProfileDto = searchProfileApplicationService.createSearchProfile(getCreateSearchProfileDto(search_profile_01.id()));
        SearchProfileId searchProfileId = new SearchProfileId(createdSearchProfileDto.getId());

        // when
        assertThat(this.searchProfileApplicationService.getSearchProfile(searchProfileId).getName()).isEqualTo(createdSearchProfileDto.getName());
        searchProfileApplicationService.deleteSearchProfile(searchProfileId);

        // then
        assertThatThrownBy(() -> this.searchProfileApplicationService.getSearchProfile(searchProfileId))
                .isInstanceOf(SearchProfileNotExitsException.class)
                .hasMessageContaining("Aggregate with ID " + searchProfileId.getValue() + " not found");
    }

    private CreateSearchProfileDto getCreateSearchProfileDto(SearchProfileId searchProfileId) {
        SearchProfile searchProfile = SearchProfileFixture.testSearchProfile(searchProfileId);
        return new CreateSearchProfileDto(
                searchProfile.getName()
                , searchProfile.getOwnerUserId()
                , SearchFilterDto.toDto(searchProfile.getSearchFilter()));

    }
}