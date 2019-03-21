package ch.admin.seco.jobs.services.jobadservice.application.complaint;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("jobad.complaint")
@Validated
public class ComplaintProperties {

    private String receiverEmailAddress;

    private String linkToJobAdSearch;

    public String getReceiverEmailAddress() {
        return receiverEmailAddress;
    }

    public void setReceiverEmailAddress(String receiverEmailAddress) {
        this.receiverEmailAddress = receiverEmailAddress;
    }

    public String getLinkToJobAdSearch() {
        return linkToJobAdSearch;
    }

    public void setLinkToJobAdSearch(String linkToJobAdSearch) {
        this.linkToJobAdSearch = linkToJobAdSearch;
    }
}
