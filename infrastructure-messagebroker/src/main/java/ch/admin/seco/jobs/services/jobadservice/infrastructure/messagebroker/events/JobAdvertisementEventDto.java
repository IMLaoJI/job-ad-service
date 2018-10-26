package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.events;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;

public class JobAdvertisementEventDto {

    private String aggregateId;

    private String eventType;

    private JobAdvertisementStatus status;

    static JobAdvertisementEventDto from(String id, String eventType, JobAdvertisementStatus status) {
        return new JobAdvertisementEventDto()
                .setAggregateId(id)
                .setEventType(eventType)
                .setStatus(status);
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public JobAdvertisementEventDto setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
        return this;
    }

    public String getEventType() {
        return eventType;
    }

    public JobAdvertisementEventDto setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public JobAdvertisementStatus getStatus() {
        return status;
    }

    public JobAdvertisementEventDto setStatus(JobAdvertisementStatus status) {
        this.status = status;
        return this;
    }
}
