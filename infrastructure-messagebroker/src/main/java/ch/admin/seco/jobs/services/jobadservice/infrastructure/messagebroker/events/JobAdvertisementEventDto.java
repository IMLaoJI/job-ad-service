package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.events;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;

public class JobAdvertisementEventDto {

    private String id;

    private String eventType;

    private JobAdvertisementStatus status;

    static JobAdvertisementEventDto from(String id, String eventType, JobAdvertisementStatus status) {
        return new JobAdvertisementEventDto()
                .setId(id)
                .setEventType(eventType)
                .setStatus(status);
    }

    public String getId() {
        return id;
    }

    public JobAdvertisementEventDto setId(String id) {
        this.id = id;
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
