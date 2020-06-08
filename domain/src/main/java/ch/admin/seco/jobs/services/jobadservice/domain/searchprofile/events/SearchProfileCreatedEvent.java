package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;

import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileEvents.SEARCH_PROFILE_CREATED;

public class SearchProfileCreatedEvent extends SearchProfileEvent {

	public SearchProfileCreatedEvent(SearchProfile searchProfile) {
		super(SEARCH_PROFILE_CREATED, searchProfile);
	}

}
