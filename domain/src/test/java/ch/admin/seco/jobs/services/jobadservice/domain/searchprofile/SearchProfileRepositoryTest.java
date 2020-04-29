package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileFixture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.SearchProfileIdFixture.search_profile_01;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchProfileRepositoryTest {

    @Autowired
    private SearchProfileRepository searchProfileRepository;

    @Test
    public void testSaveSearchProfile() {
        // given
        SearchProfile searchProfile = SearchProfileFixture.testSearchProfile(search_profile_01.id());

        //when
        SearchProfile savedSearchProfile = this.searchProfileRepository.save(searchProfile);

        // then
        assertThat(savedSearchProfile.getSearchFilter().getKeywords())
                .containsExactlyElementsOf(searchProfile.getSearchFilter().getKeywords());

        assertThat(savedSearchProfile.getSearchFilter().getOccupationFilters())
                .containsExactlyElementsOf(searchProfile.getSearchFilter().getOccupationFilters());
    }

    @SpringBootApplication
    static class TestConfig {

    }
}
