package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture;

import java.util.Arrays;
import java.util.Collections;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.CantonFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.SearchFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.Sort;

public class SearchProfileFixture {

    public static SearchProfile testSearchProfile(SearchProfileId id) {
        return SearchProfile.builder()
                .setId(id)
                .setName("name-" + id)
                .setOwnerUserId("job-seeker-1")
                .setSearchFilter(prepareSearchFilter())
                .build();
    }

    // plain search filter
    public static SearchFilter prepareSearchFilter() {
        return SearchFilter.builder()
                .setSort(Sort.RELEVANCE_DESC)
                .setKeywords(Arrays.asList("java", "angular"))
                .setOccupationFilters(Collections.emptyList())
                .setLocalityFilters(Collections.emptyList())
                .setCantonFilters(Collections.singletonList(
                        new CantonFilter("Bern", "BE")
                ))
                .build();
    }
}
