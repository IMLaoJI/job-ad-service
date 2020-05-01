package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;

public enum SearchProfileIdFixture {
    search_profile_01, search_profile_02, search_profile_03, search_profile_04, search_profile_05,
    search_profile_06, search_profile_07, search_profile_08, search_profile_09, search_profile_10;

    private final SearchProfileId id;

    SearchProfileIdFixture() {
        this.id = new SearchProfileId(name());
    }

    public SearchProfileId id() {
        return id;
    }
}
