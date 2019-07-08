package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;

import java.util.Set;

public class ApiSearchRequestDto {

    private Set<JobAdvertisementStatus> status;

    public Set<JobAdvertisementStatus> getStatus() {
        return status;
    }

    public ApiSearchRequestDto setStatus(Set<JobAdvertisementStatus> status) {
        this.status = status;
        return this;
    }
}
