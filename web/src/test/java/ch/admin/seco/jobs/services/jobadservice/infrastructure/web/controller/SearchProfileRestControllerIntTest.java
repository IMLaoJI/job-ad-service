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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        SearchProfileIdFixture searchProfileId = SearchProfileIdFixture.search_profile_01;
        SearchProfile searchProfile = SearchProfileFixture.testSearchProfile(searchProfileId.id());
        createSearchProfileResource.name = searchProfile.getName();
        createSearchProfileResource.userOwnerId = searchProfile.getOwnerUserId();
        createSearchProfileResource.searchFilter = searchProfile.getSearchFilter();

        // when
        ResultActions post = post(createSearchProfileResource, URL);
        post.andExpect(status().isCreated());

        String contentAsString = post.andReturn().getResponse().getContentAsString();
        JSONArray ja = new JSONArray("[" + contentAsString + "]");
        String id = ja.getJSONObject(0).getString("id");

        // then
        assertThat(this.searchProfileRepository.findById(new SearchProfileId(id))).isPresent();
    }


    @Test
    @WithJobSeeker
    public void testUpdateSearchProfile() throws Exception {
        // given
        SearchProfileRestController.CreateSearchProfileResource createSearchProfileResource = new SearchProfileRestController.CreateSearchProfileResource();
        SearchProfileIdFixture searchProfileId = SearchProfileIdFixture.search_profile_01;
        SearchProfile testSearchProfile = SearchProfileFixture.testSearchProfile(searchProfileId.id());
        createSearchProfileResource.name = testSearchProfile.getName();
        createSearchProfileResource.userOwnerId = testSearchProfile.getOwnerUserId();
        createSearchProfileResource.searchFilter = testSearchProfile.getSearchFilter();
        ResultActions post = post(createSearchProfileResource, URL);
        post.andExpect(status().isCreated());

        SearchProfileRestController.UpdateSearchProfileResource updateSearchProfileResource = new SearchProfileRestController.UpdateSearchProfileResource();
        String adjustedName = "Adjusted Name";
        updateSearchProfileResource.name = adjustedName;
        updateSearchProfileResource.searchFilter = testSearchProfile.getSearchFilter();

        // when
        ResultActions put = this.mockMvc.perform(
                MockMvcRequestBuilders.put(URL + "/" + searchProfileId.id().getValue())
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(updateSearchProfileResource)));
        put.andExpect(status().isOk());


        String contentAsString = post.andReturn().getResponse().getContentAsString();
        JSONArray ja = new JSONArray("[" + contentAsString + "]");
        String id = ja.getJSONObject(0).getString("id");

        // then
        Optional<SearchProfile> searchProfile = this.searchProfileRepository.findById(testSearchProfile.getId());
        assertThat(searchProfile).isPresent();
        assertThat(searchProfile.get().getName()).isEqualTo(adjustedName);
    }

    private ResultActions post(Object request, String urlTemplate) throws Exception {
        return this.mockMvc.perform(
                MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(request))
        );
    }
}
