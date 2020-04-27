package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;

import java.util.List;

import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileEvents.JOBALERT_RELEASED_EVENT;


public class JobAlertReleasedEvent extends SearchProfileEvent {

	private SearchProfile searchProfile;

	private List<JobAdvertisementId> matchedIds;

	public JobAlertReleasedEvent(SearchProfile searchProfile, List<JobAdvertisementId> matchedIds) {
		super(JOBALERT_RELEASED_EVENT, searchProfile);
		this.searchProfile = searchProfile;
		this.matchedIds = matchedIds;
	}

	public SearchProfile getSearchProfile() {
		return searchProfile;
	}

	public List<JobAdvertisementId> getMatchedIds() {
		return matchedIds;
	}
}
