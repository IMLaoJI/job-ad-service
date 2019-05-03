package ch.admin.seco.jobs.services.jobadservice.application.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;

public class SearchProfileNameAlreadyExistsException extends RuntimeException {

    SearchProfileNameAlreadyExistsException(SearchProfileId searchProfileId, String name, String ownerUserId) {
        super("SearchProfile with name " + name + "already exists. " +
                "Please give another name for SearchProfile{id="+searchProfileId.getValue()+", ownerUserId="+ownerUserId+"}.");
    }
}
