package ch.admin.seco.jobs.services.jobadservice.application;

import static org.springframework.util.Assert.hasText;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class BusinessLogEvent {

	private String authorities;

	private String eventType;

	private String objectType;

	private String objectId;

	private Map<String, Object> additionalData = new HashMap<>();

	public BusinessLogEvent(String eventType) {
		hasText(eventType, "Event type must not be empty!");
		this.eventType = eventType;
	}

	public String getAuthorities() {
		return authorities;
	}

	public BusinessLogEvent withAuthorities(String authorities) {
		this.authorities = authorities;
		return this;
	}

	public String getEventType() {
		return eventType;
	}

	public String getObjectType() {
		return objectType;
	}

	public BusinessLogEvent withObjectType(String objectType) {
		this.objectType = objectType;
		return this;
	}

	public BusinessLogEvent withObjectId(String objectId) {
		this.objectId = objectId;
		return this;
	}

	public String getObjectId() {
		return this.objectId;
	}

	public BusinessLogEvent withAdditionalData(String key, Object value) {
		if (StringUtils.isNotEmpty(key) && value != null) {
			this.additionalData.put(key, value);
		}
		return this;
	}

	public BusinessLogEvent withAdditionalData(Map<String, Object> additionalData) {
		additionalData.forEach(this::withAdditionalData);
		return this;
	}

	public Map<String, Object> getAdditionalData() {
		return additionalData;
	}

	@Override
	public String toString() {
		return "BusinessLogEvent{" +
				"eventType='" + eventType + '\'' +
				", objectType='" + objectType + '\'' +
				", objectId='" + objectId + '\'' +
				", additionalData=" + additionalData +
				'}';
	}

}
