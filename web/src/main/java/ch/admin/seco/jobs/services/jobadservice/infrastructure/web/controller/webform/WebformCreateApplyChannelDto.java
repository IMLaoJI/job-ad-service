package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.webform;

public class WebformCreateApplyChannelDto {

    private WebformCreateAddressDto postAddress;

    private String emailAddress;

    private String phoneNumber;

    private String formUrl;

    private String additionalInfo;

    public WebformCreateAddressDto getPostAddress() {
        return postAddress;
    }

    public WebformCreateApplyChannelDto setPostAddress(WebformCreateAddressDto postAddress) {
        this.postAddress = postAddress;
        return this;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public WebformCreateApplyChannelDto setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public WebformCreateApplyChannelDto setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getFormUrl() {
        return formUrl;
    }

    public WebformCreateApplyChannelDto setFormUrl(String formUrl) {
        this.formUrl = formUrl;
        return this;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public WebformCreateApplyChannelDto setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }
}
