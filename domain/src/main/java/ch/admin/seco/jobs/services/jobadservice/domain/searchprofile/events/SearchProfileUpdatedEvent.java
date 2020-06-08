package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;

import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileEvents.SEARCH_PROFILE_UPDATED;

public class SearchProfileUpdatedEvent extends SearchProfileEvent {

    public SearchProfileUpdatedEvent(SearchProfile searchProfile) {
        super(SEARCH_PROFILE_UPDATED, searchProfile);
    }
}
