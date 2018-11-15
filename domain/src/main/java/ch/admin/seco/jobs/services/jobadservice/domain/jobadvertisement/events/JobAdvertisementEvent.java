package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;

public class JobAdvertisementEvent extends DomainEvent<JobAdvertisementId> {

    protected JobAdvertisementId jobAdvertisementId;

    protected JobAdvertisementStatus jobAdvertisementStatus;

    public JobAdvertisementEvent(JobAdvertisementEvents jobAdvertisementEventType, JobAdvertisement jobAdvertisement) {
        super(jobAdvertisementEventType.getDomainEventType(), JobAdvertisement.class.getSimpleName());
        this.setJobAdvertisementId(jobAdvertisement.getId());
        this.setJobAdvertisementStatus(jobAdvertisement.getStatus());
    }

    @Override
    public JobAdvertisementId getAggregateId() {
        return this.jobAdvertisementId;
    }


    public JobAdvertisementStatus getJobAdvertisementStatus() {
        return this.jobAdvertisementStatus;
    }

    private void setJobAdvertisementId(JobAdvertisementId jobAdvertisementId) {
        this.jobAdvertisementId = jobAdvertisementId;
        additionalAttributes.put("jobAdvertisementId", jobAdvertisementId.getValue());
    }

    private void setJobAdvertisementStatus(JobAdvertisementStatus jobAdvertisementStatus) {
        this.jobAdvertisementStatus = jobAdvertisementStatus;
        additionalAttributes.put("jobAdvertisementStatus", jobAdvertisementStatus.name());
    }
}
