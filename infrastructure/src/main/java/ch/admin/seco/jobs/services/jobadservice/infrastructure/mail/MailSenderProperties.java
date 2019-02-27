package ch.admin.seco.jobs.services.jobadservice.infrastructure.mail;

import java.util.Collections;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "mail.sender")
public class MailSenderProperties {

    @NotEmpty
    private String fromAddress;

    @NotEmpty
    private Set<String> bccAddress = Collections.emptySet();

    private String baseUrl;

    private String linkToJobAdDetailPage;

    @NotNull
    private String templatesPath;

    @Min(0)
    private int mailQueueThreshold = 0;

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public Set<String> getBccAddress() {
        return bccAddress;
    }

    public void setBccAddress(Set<String> bccAddress) {
        this.bccAddress = bccAddress;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getLinkToJobAdDetailPage() {
        return linkToJobAdDetailPage;
    }

    public void setLinkToJobAdDetailPage(String linkToJobAdDetailPage) {
        this.linkToJobAdDetailPage = linkToJobAdDetailPage;
    }

    public String getTemplatesPath() {
        return templatesPath;
    }

    public void setTemplatesPath(String templatesPath) {
        this.templatesPath = templatesPath;
    }

    public int getMailQueueThreshold() {
        return mailQueueThreshold;
    }

    public void setMailQueueThreshold(int mailQueueThreshold) {
        this.mailQueueThreshold = mailQueueThreshold;
    }
}
