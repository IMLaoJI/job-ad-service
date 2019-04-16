package ch.admin.seco.jobs.services.jobadservice.infrastructure.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "mail.sender")
public class MailSenderProperties {

    private String baseUrl;

    private String linkToJobAdDetailPage;

    @NotNull
    private String templatesPath;

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
}
