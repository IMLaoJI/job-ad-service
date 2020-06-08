package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;

import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileEvents.JOBALERT_UNSUBSCRIBED_EVENT;

public class JobAlertUnsubscribedEvent extends SearchProfileEvent {

	private SearchProfile searchProfile;

	public JobAlertUnsubscribedEvent(SearchProfile searchProfile) {
		super(JOBALERT_UNSUBSCRIBED_EVENT, searchProfile);
		this.searchProfile = searchProfile;
	}


	public SearchProfile getSearchProfile() {
		return searchProfile;
	}
}

