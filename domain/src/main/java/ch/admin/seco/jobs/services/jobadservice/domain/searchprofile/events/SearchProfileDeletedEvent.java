package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;

import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileEvents.SEARCH_PROFILE_DELETED;

public class SearchProfileDeletedEvent extends SearchProfileEvent {

    public SearchProfileDeletedEvent(SearchProfile searchProfile) {
        super(SEARCH_PROFILE_DELETED, searchProfile);
    }
}
