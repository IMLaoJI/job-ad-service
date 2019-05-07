package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.GeoPointFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.*;
import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.Collections;

import static java.lang.String.format;

public class SearchProfileFixture {

    public static SearchProfile testSearchProfile(SearchProfileId id) {
        return SearchProfile.builder()
                .setId(id)
                .setName(format("name-%s", id))
                .setOwnerUserId(format("job-seeker-1", id))
                .setSearchFilter(prepareSearchFilter())
                .build();
    }

    private static SearchFilter prepareSearchFilter() {
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
                        new RadiusSearchFilter(GeoPointFixture.testGeoPoint(),20)
                )
                .build();
    }
}
