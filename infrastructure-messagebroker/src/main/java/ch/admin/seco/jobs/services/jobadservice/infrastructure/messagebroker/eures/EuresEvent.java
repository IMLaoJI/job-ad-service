package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.eures;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;

class EuresEvent {

    static EuresEvent from(String id, JobAdvertisementStatus status) {
        EuresEvent event = new EuresEvent();
        event.setJobAdvertisementId(id);
        event.setStatus(status);
        return event;
    }

    private String jobAdvertisementId;

    private JobAdvertisementStatus status;

    public String getJobAdvertisementId() {
        return jobAdvertisementId;
    }

    public void setJobAdvertisementId(String jobAdvertisementId) {
        this.jobAdvertisementId = jobAdvertisementId;
    }

    public JobAdvertisementStatus getStatus() {
        return status;
    }

    public void setStatus(JobAdvertisementStatus status) {
        this.status = status;
    }
}
