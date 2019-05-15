package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.GeoPointFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.CantonFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.RadiusSearchFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.SearchFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.Sort;
import com.google.common.collect.ImmutableSet;

import java.util.Collections;

public class SearchProfileFixture {

    public static SearchProfile testSearchProfile(SearchProfileId id) {
        return SearchProfile.builder()
                .setId(id)
                .setName("name-" + id)
                .setOwnerUserId("job-seeker-1")
                .setSearchFilter(prepareSearchFilter())
                .build();
    }

    private static SearchFilter prepareSearchFilter() {
        return SearchFilter.builder()
                .setSort(Sort.RELEVANCE_DESC)
                .setKeywords(ImmutableSet.of("java", "angular"))
                .setOccupationFilters(Collections.emptyList())
                .setLocalityFilters(Collections.emptyList())
                .setCantonFilters(Collections.singletonList(
                        new CantonFilter("Bern", "BE")
                ))
                .setRadiusSearchFilter(
                        new RadiusSearchFilter(GeoPointFixture.testGeoPoint(),20)
                )
                .build();
    }
}
