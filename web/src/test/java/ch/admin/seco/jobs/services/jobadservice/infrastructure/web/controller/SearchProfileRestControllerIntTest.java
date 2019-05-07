package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileIdFixture;
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

import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileIdFixture.search_profile_01;
import static com.google.common.base.Predicates.equalTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.searchProfileRepository.deleteAll();
    }

    @Test
    @WithJobSeeker
    public void testCreateSearchProfile() throws Exception {
        // given
        SearchProfileRestController.CreateSearchProfileResource createSearchProfileResource = new SearchProfileRestController.CreateSearchProfileResource();
        SearchProfileIdFixture searchProfileId = search_profile_01;
        SearchProfile searchProfile = SearchProfileFixture.testSearchProfile(searchProfileId.id());
        createSearchProfileResource.name = searchProfile.getName();
        createSearchProfileResource.userOwnerId = searchProfile.getOwnerUserId();
        createSearchProfileResource.searchFilter = searchProfile.getSearchFilter();

        // when
        ResultActions post = post(createSearchProfileResource, URL);

        // then
        post.andExpect(status().isCreated());
    }

    @Test
    @WithJobSeeker
    public void testUpdateSearchProfile() throws Exception {
        // given
        SearchProfile testSearchProfile = createSearchProfile();

        SearchProfileRestController.UpdateSearchProfileResource updateSearchProfileResource = new SearchProfileRestController.UpdateSearchProfileResource();
        String adjustedName = "Adjusted Name";
        updateSearchProfileResource.name = adjustedName;
        updateSearchProfileResource.searchFilter = testSearchProfile.getSearchFilter();

        // when
        ResultActions put = this.mockMvc.perform(
                MockMvcRequestBuilders.put(URL + "/" + search_profile_01.id().getValue())
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(updateSearchProfileResource)));
        put.andExpect(status().isOk());

        // then
        Optional<SearchProfile> searchProfile = this.searchProfileRepository.findById(testSearchProfile.getId());
        assertThat(searchProfile).isPresent();
        assertThat(searchProfile.get().getName()).isEqualTo(adjustedName);
    }

    @Test
    @WithJobSeeker
    public void testDeleteSearchProfile() throws Exception {
        // given
        createSearchProfile();

        // when
        ResultActions delete = this.mockMvc.perform(
                MockMvcRequestBuilders.delete(URL + "/" + search_profile_01.id().getValue())
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));
        delete.andExpect(status().isNoContent());

        // then
        assertThat(this.searchProfileRepository.findById(search_profile_01.id())).isNotPresent();
    }


    @Test
    @WithJobSeeker
    public void testFindById() throws Exception {
        // given
        SearchProfileRestController.CreateSearchProfileResource createSearchProfileResource = new SearchProfileRestController.CreateSearchProfileResource();
        SearchProfileIdFixture searchProfileId = search_profile_01;
        SearchProfile testSearchProfile = SearchProfileFixture.testSearchProfile(searchProfileId.id());
        createSearchProfileResource.name = testSearchProfile.getName();
        createSearchProfileResource.userOwnerId = testSearchProfile.getOwnerUserId();
        createSearchProfileResource.searchFilter = testSearchProfile.getSearchFilter();

        // when
        ResultActions resultActions = this.mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/" + search_profile_01.id().getValue())
                        .contentType(TestUtil.APPLICATION_JSON_UTF8));
        // then
        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(equalTo(search_profile_01.id().getValue())));
    }


    @Test
    @WithJobSeeker
    public void testFindByOwnerUserId() throws Exception {
        // given

        // when

        // then
    }

    private SearchProfile createSearchProfile() throws Exception {
        SearchProfileRestController.CreateSearchProfileResource createSearchProfileResource = new SearchProfileRestController.CreateSearchProfileResource();
        SearchProfileIdFixture searchProfileId = search_profile_01;
        SearchProfile testSearchProfile = SearchProfileFixture.testSearchProfile(searchProfileId.id());
        createSearchProfileResource.name = testSearchProfile.getName();
        createSearchProfileResource.userOwnerId = testSearchProfile.getOwnerUserId();
        createSearchProfileResource.searchFilter = testSearchProfile.getSearchFilter();
        ResultActions post = post(createSearchProfileResource, URL);
        post.andExpect(status().isCreated());

        return testSearchProfile;
    }

    private ResultActions post(Object request, String urlTemplate) throws Exception {
        return this.mockMvc.perform(
                MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
        );
    }
}
