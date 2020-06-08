package ch.admin.seco.jobs.services.jobadservice.application.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;

public class SearchProfileNotExitsException extends AggregateNotFoundException {

    SearchProfileNotExitsException(SearchProfileId searchProfileId) {
        super(SearchProfile.class, searchProfileId.getValue());
    }
}
