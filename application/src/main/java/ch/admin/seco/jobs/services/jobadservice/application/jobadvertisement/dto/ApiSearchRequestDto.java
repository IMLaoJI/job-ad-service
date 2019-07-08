package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;

public class ApiSearchRequestDto {

    private JobAdvertisementStatus[] status;

    public JobAdvertisementStatus[] getStatus() {
        return status;
    }

    public ApiSearchRequestDto setStatus(JobAdvertisementStatus[] status) {
        this.status = status;
        return this;
    }
}
