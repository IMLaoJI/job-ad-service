package web.controller;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.SearchProfileApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.CreateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.ResolvedSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.UpdateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.SearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileFixture;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.TestUtil;
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

import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileIdFixture.search_profile_01;
import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileIdFixture.search_profile_02;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class SearchProfileRestControllerIntTest {

    private static final String URL = "/api/searchProfiles";

    @Autowired
    private SearchProfileRepository searchProfileRepository;

    @Autowired
    private SearchProfileApplicationService searchProfileApplicationService;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.searchProfileRepository.deleteAll();
    }

    @Test
    @WithJobSeeker
    public void testCreateSearchProfile() throws Exception {
        // given
        SearchProfile searchProfile = SearchProfileFixture.testSearchProfile(search_profile_01.id());
        CreateSearchProfileDto createSearchProfileDto = new CreateSearchProfileDto(
                searchProfile.getName(), searchProfile.getOwnerUserId(), SearchFilterDto.toDto(searchProfile.getSearchFilter())
        );

        // when
        ResultActions post = post(createSearchProfileDto, URL);

        // then
        post.andExpect(status().isCreated());
    }

    @Test
    @WithJobSeeker
    public void testUpdateSearchProfile() throws Exception {
        // given
        ResolvedSearchProfileDto resolvedSearchProfileDto = createSearchProfile();

        String adjustedName = "Adjusted Name";
        UpdateSearchProfileDto updateSearchProfileDto = new UpdateSearchProfileDto();
        updateSearchProfileDto.setName(adjustedName);
        updateSearchProfileDto.setSearchFilter(SearchFilterDto.toDto(SearchProfileFixture.prepareSearchFilter()));

        // when
        ResultActions put = this.mockMvc.perform(
                MockMvcRequestBuilders.put(URL + "/" + resolvedSearchProfileDto.getId())
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(updateSearchProfileDto)));
        put.andExpect(status().isOk());

        // then
        Optional<SearchProfile> searchProfile = this.searchProfileRepository.findById(new SearchProfileId(resolvedSearchProfileDto.getId()));
        assertThat(searchProfile).isPresent();
        assertThat(searchProfile.get().getName()).isEqualTo(adjustedName);
    }

    @Test
    @WithJobSeeker
    public void testDeleteSearchProfile() throws Exception {
        // given
        ResolvedSearchProfileDto resolvedSearchProfileDto = createSearchProfile();

        // when
        ResultActions delete = this.mockMvc.perform(
                MockMvcRequestBuilders.delete(URL + "/" + resolvedSearchProfileDto.getId())
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));
        delete.andExpect(status().isNoContent());

        // then
        assertThat(this.searchProfileRepository.findById(new SearchProfileId(resolvedSearchProfileDto.getId()))).isNotPresent();
    }

    @Test
    @WithJobSeeker
    public void testFindById() throws Exception {
        // given
        ResolvedSearchProfileDto resolvedSearchProfileDto = createSearchProfile();
        SearchProfileId id = new SearchProfileId(resolvedSearchProfileDto.getId());

        // when
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/" + resolvedSearchProfileDto.getId())
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));
        // then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(resolvedSearchProfileDto.getId()));
    }


    @Test
    @WithJobSeeker
    public void testFindByOwnerUserId() throws Exception {
        // given
        ResolvedSearchProfileDto resolvedSearchProfileDto = createSearchProfile();
        SearchProfile searchProfile = SearchProfileFixture.testSearchProfile(search_profile_02.id());
        CreateSearchProfileDto createSearchProfileDto = new CreateSearchProfileDto(
                searchProfile.getName()
                , searchProfile.getOwnerUserId()
                , SearchFilterDto.toDto(searchProfile.getSearchFilter())
        );
        ResolvedSearchProfileDto resolvedSearchProfileDto2 = searchProfileApplicationService.createSearchProfile(createSearchProfileDto);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/_search")
                        .param("ownerUserId", WithJobSeeker.USER_ID)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.[0].id").value(resolvedSearchProfileDto2.getId()))
                .andExpect(jsonPath("$.[1].id").value(resolvedSearchProfileDto.getId()));
    }

    private ResolvedSearchProfileDto createSearchProfile() {
        SearchProfile searchProfile = SearchProfileFixture.testSearchProfile(search_profile_01.id());
        CreateSearchProfileDto createSearchProfileDto = new CreateSearchProfileDto(
                searchProfile.getName()
                , searchProfile.getOwnerUserId()
                , SearchFilterDto.toDto(searchProfile.getSearchFilter())
        );
        return searchProfileApplicationService.createSearchProfile(createSearchProfileDto);
    }

    private ResultActions post(Object request, String urlTemplate) throws Exception {
        return this.mockMvc.perform(
                MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
        );
    }
}
