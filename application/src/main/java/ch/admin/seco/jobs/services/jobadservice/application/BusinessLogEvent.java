package ch.admin.seco.jobs.services.jobadservice.application;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class BusinessLogEvent {

	private String authorities;

	private BusinessLogEventType eventType;

	private BusinessLogObjectType objectType;

	private String objectId;

	private Map<String, Object> additionalData = new HashMap<>();

	public String getAuthorities() {
		return authorities;
	}

	public BusinessLogEvent withAuthorities(String authorities) {
		this.authorities = authorities;
		return this;
	}

	public BusinessLogEvent withEventType(BusinessLogEventType eventType) {
		this.eventType = Condition.notNull(eventType, "Business log event type must not be empty!");
		return this;
	}

	public BusinessLogEventType getEventType() {
		return eventType;
	}

	public BusinessLogObjectType getObjectType() {
		return objectType;
	}

	public BusinessLogEvent withObjectType(BusinessLogObjectType objectType) {
		this.objectType = Condition.notNull(objectType, "Business log object type must not be empty!");
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
