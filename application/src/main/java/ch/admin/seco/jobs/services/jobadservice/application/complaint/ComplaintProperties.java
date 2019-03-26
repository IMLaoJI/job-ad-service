package ch.admin.seco.jobs.services.jobadservice.application.complaint;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("jobad.complaint")
@Validated
public class ComplaintProperties {

    private String receiverEmailAddress;

    public String getReceiverEmailAddress() {
        return receiverEmailAddress;
    }

    public void setReceiverEmailAddress(String receiverEmailAddress) {
        this.receiverEmailAddress = receiverEmailAddress;
    }
}
