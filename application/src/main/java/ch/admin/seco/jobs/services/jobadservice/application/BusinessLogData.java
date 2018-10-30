package ch.admin.seco.jobs.services.jobadservice.application;

import org.springframework.util.Assert;

import java.util.Map;

public class BusinessLogData {

    private final String eventType;

    private final String objectType;

    private final String objectId;

    private final Map<String, Object> additionalData;

    public BusinessLogData(String eventType, String objectType, String objectId, Map<String, Object> additionalData) {
        Assert.hasText(eventType, "Event type must not be empty");
        this.eventType = eventType;
        this.objectType = objectType;
        this.objectId = objectId;
        this.additionalData = additionalData;
    }

    public String getEventType() {
        return eventType;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getObjectId() {
        return objectId;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    @Override
    public String toString() {
        return "BusinessLogData{" +
                "eventType='" + eventType + '\'' +
                ", objectType='" + objectType + '\'' +
                ", objectId='" + objectId + '\'' +
                ", additionalData=" + additionalData +
                '}';
    }
}
