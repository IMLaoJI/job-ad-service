package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;

public class SearchProfileNameAlreadyExistsException  extends RuntimeException {
    SearchProfileNameAlreadyExistsException(SearchProfileId favouriteItemId, JobAdvertisementId jobAdvertisementId, String userId) {
        super("SearchProfile couldn't be created. User: '" + userId + "' already has a SearchProfile: '" + favouriteItemId.getValue() + "' for JobAdvertismentId: '" + jobAdvertisementId.getValue() + "'");
    }
}
