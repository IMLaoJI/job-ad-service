package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update;

import javax.validation.constraints.NotBlank;

public class UpdateJobAdvertisementFromExternalDto {

    @NotBlank
    private String jobAdvertisementId;

    @NotBlank
    private String fingerprint;

    private String externalOccupationCodes;

    protected UpdateJobAdvertisementFromExternalDto() {
        // For reflection libs
    }

    public UpdateJobAdvertisementFromExternalDto(String jobAdvertisementId, String fingerprint, String externalOccupationCodes) {
        this.jobAdvertisementId = jobAdvertisementId;
        this.fingerprint = fingerprint;
        this.externalOccupationCodes = externalOccupationCodes;
    }

    public String getJobAdvertisementId() {
        return jobAdvertisementId;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getExternalOccupationCodes() {
        return externalOccupationCodes;
    }
}
