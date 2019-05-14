package ch.admin.seco.jobs.services.jobadservice.application.searchprofile;

public class SearchProfileNameAlreadyExistsException extends RuntimeException {

    SearchProfileNameAlreadyExistsException(String name, String ownerUserId) {
        super("SearchProfile with name " + name + "already exists. " +
                "Please rename your new SearchProfile for ownerUserId=" + ownerUserId + ".");
    }
}
