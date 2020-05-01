package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto;

public class JobAlertMaxAmountReachedException extends RuntimeException {

    public JobAlertMaxAmountReachedException() {
        super("Maximum Amount of JobAlerts reached!");
    }
}
