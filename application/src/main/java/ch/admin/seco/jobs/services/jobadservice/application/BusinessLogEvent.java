package ch.admin.seco.jobs.services.jobadservice.application;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.Assert.hasText;

public class BusinessLogEvent {
	public static final String JOB_ADVERTISEMENT_FAVORITE_EVENT = "JOB_ADVERTISEMENT_FAVORITE";
	public static final String JOB_ADVERTISEMENT_ACCESS = "JOB_ADVERTISEMENT_ACCESS";

	public static final String JOB_ADVERTISEMENT = "JobAdvertisement";
	public static final String OBJECT_TYPE_STATUS = "objectTypeStatus";

	private String authorities;

	private String eventType;

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
		return JOB_ADVERTISEMENT;
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

	public Map<String, Object> getAdditionalData() {
		return additionalData;
	}

	@Override
	public String toString() {
		return "BusinessLogEvent{" +
				"eventType='" + eventType + '\'' +
				", objectType='" + getObjectType() + '\'' +
				", objectId='" + objectId + '\'' +
				", additionalData=" + additionalData +
				'}';
	}

}
