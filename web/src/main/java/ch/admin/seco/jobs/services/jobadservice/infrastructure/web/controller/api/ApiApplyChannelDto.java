package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.ApplyChannel;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.PhoneNumber;

import javax.validation.constraints.Size;

public class ApiApplyChannelDto {

    @Size(max=255)
    private String mailAddress;

    @Size(max=50)
    private String emailAddress;

    @PhoneNumber
    private String phoneNumber;

    @Size(max=255)
    private String formUrl;

    @Size(max=255)
    private String additionalInfo;

    public String getMailAddress() {
        return mailAddress;
    }

    public ApiApplyChannelDto setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
        return this;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public ApiApplyChannelDto setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ApiApplyChannelDto setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getFormUrl() {
        return formUrl;
    }

    public ApiApplyChannelDto setFormUrl(String formUrl) {
        this.formUrl = formUrl;
        return this;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public ApiApplyChannelDto setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }
}
