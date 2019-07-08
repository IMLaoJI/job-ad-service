package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

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
