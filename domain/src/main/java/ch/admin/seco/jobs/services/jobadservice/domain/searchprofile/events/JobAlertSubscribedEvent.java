package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;

import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileEvents.JOBALERT_SUBSCRIBED_EVENT;

public class JobAlertSubscribedEvent extends SearchProfileEvent {

	private SearchProfile searchProfile;

	public JobAlertSubscribedEvent(SearchProfile searchProfile) {
		super(JOBALERT_SUBSCRIBED_EVENT, searchProfile);
		this.searchProfile = searchProfile;
	}


	public SearchProfile getSearchProfile() {
		return searchProfile;
	}
}

