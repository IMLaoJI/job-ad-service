package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.GeoPointFixture;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchProfileRepositoryTest {

    @Autowired
    private SearchProfileRepository searchProfileRepository;

    @Test
    public void testSaveSearchProfile() {
        // given
        SearchProfile searchProfile = new SearchProfile.Builder()
                .setId(new SearchProfileId("SP-1"))
                .setName("My-Name")
                .setOwnerUserId("User-01")
                .setSearchFilter(prepareSearchFilter())
                .build();

        //when
        SearchProfile savedSearchProfile = this.searchProfileRepository.save(searchProfile);

        // then
        assertThat(savedSearchProfile.getSearchFilter().getKeywords())
                .containsExactlyElementsOf(searchProfile.getSearchFilter().getKeywords());

        assertThat(savedSearchProfile.getSearchFilter().getOccupationFilters())
                .containsExactlyElementsOf(searchProfile.getSearchFilter().getOccupationFilters());
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
                .setCantonFilters(Arrays.asList(
                        new CantonFilter("Bern", "BE")
                ))
                .setRadiusSearchFilters(Arrays.asList(
                        new RadiusSearchFilter(GeoPointFixture.testGeoPoint(),20)
                ))
                .build();
    }

    @SpringBootApplication
    static class TestConfig {

    }
}