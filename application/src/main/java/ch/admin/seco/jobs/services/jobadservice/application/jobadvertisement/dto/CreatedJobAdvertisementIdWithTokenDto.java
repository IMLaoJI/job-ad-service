package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto;

public class CreatedJobAdvertisementIdWithTokenDto {

    private String jobAdvertisementId;

    private String token;

    public CreatedJobAdvertisementIdWithTokenDto() {
        // For reflection libs
    }

    public String getJobAdvertisementId() {
        return jobAdvertisementId;
    }

    public CreatedJobAdvertisementIdWithTokenDto setJobAdvertisementId(String jobAdvertisementId) {
        this.jobAdvertisementId = jobAdvertisementId;
        return this;
    }

    public String getToken() {
        return token;
    }

    public CreatedJobAdvertisementIdWithTokenDto setToken(String token) {
        this.token = token;
        return this;
    }
}
