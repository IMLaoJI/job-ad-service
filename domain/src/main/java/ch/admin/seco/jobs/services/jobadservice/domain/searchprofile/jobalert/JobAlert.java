package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert;

import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import com.google.common.collect.Iterables;

import javax.persistence.*;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;


@Embeddable
@Access(AccessType.FIELD)
public class JobAlert {


	@Column(name = "JOB_ALERT_INTERVAL")
	@Enumerated(EnumType.STRING)
	private Interval interval;

	@Column(name = "JOB_ALERT_QUERY")
	private String query;

	@ElementCollection
	@CollectionTable(name = "SEARCH_PROFILE_MATCHED_ID", joinColumns = @JoinColumn(name = "SEARCH_PROFILE_ID"))
	@AttributeOverride(name = "value", column = @Column(name = "job_advertisement_id"))
	@Valid
	private Set<JobAdvertisementId> matchedJobAdvertisementIds = new LinkedHashSet<>();

	@Column(name = "JOB_ALERT_CREATED_AT")
	private LocalDateTime createdAt;

	@Column(name = "JOB_ALERT_UPDATED_AT")
	private LocalDateTime updatedAt;

	@Column(name = "JOB_ALERT_NEXT_RELEASE_AT")
	private LocalDateTime nextReleaseAt;

	@Column(name = "JOB_ALERT_EMAIL")
	private String email;

	@Column(name = "JOB_ALERT_ACCESS_TOKEN")
	private String accessToken;

	@Column(name = "JOB_ALERT_CONTACT_LANGUAGE_ISO_CODE")
	private String language;

	private JobAlert() {
		// FOR REFLECTION
	}

	public Interval getInterval() {
		return interval;
	}

	public String getQuery() {
		return query;
	}

	public Set<JobAdvertisementId> getMatchedJobAdvertisementIds() {
		return matchedJobAdvertisementIds;
	}

	public Set<JobAdvertisementId> getMatchedJobAdvertisementIdsForRelease() {
		if (!this.getMatchedJobAdvertisementIds().isEmpty() && this.getMatchedJobAdvertisementIds().size() >= MAX_ENTRIES_TO_BE_RELEASED) {
			removeEldestEntriesForRelease();
		}
		return this.getMatchedJobAdvertisementIds();
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public LocalDateTime getNextReleaseAt() {
		return nextReleaseAt;
	}

	public String getEmail() {
		return email;
	}

	public String getLanguage() {
		return language;
	}

	public String getAccessToken() {
		return accessToken;
	}

	private static final int MAX_ENTRIES = 13; // Only 10 are released, 3 are reserved.

	private static final int MAX_ENTRIES_TO_BE_RELEASED = 10;

	private static final int POSITION_OF_ELDEST_ENTRY = 0;

	public void add(JobAdvertisementId jobAdvertisementId) {
		if (!matchedJobAdvertisementIds.isEmpty() && matchedJobAdvertisementIds.size() >= MAX_ENTRIES) {
			matchedJobAdvertisementIds.remove(Iterables.get(matchedJobAdvertisementIds, POSITION_OF_ELDEST_ENTRY));
		}
		this.matchedJobAdvertisementIds.add(jobAdvertisementId);
		touch();
	}

	public void addQuery(String query) {
		this.query = query;
	}

	public static LocalDateTime calculateReleaseTime(Interval interval) {
		switch (interval) {
			case INT_1DAY:
				return TimeMachine.now().plusDays(1);
			case INT_3DAY:
				return TimeMachine.now().plusDays(3);
			case INT_5DAY:
				return TimeMachine.now().plusDays(5);
			default:
				throw new UnsupportedOperationException("Invalid Interval: " + interval);
		}
	}

	public void clearMatchedIdsAndCalculateNextRelease() {
		touch();
		this.matchedJobAdvertisementIds.clear();
		this.nextReleaseAt = calculateReleaseTime(this.interval);
	}

	private void touch() {
		this.updatedAt = TimeMachine.now();
	}

	public static JobAlert.Builder builder() {
		return new JobAlert.Builder();
	}

	private JobAlert(JobAlert.Builder builder) {
		this.interval = builder.interval;
		this.query = builder.query;
		this.matchedJobAdvertisementIds = builder.matchedJobAdvertisementIds;
		this.createdAt = builder.createdAt;
		this.updatedAt = builder.lastUpdatedAt;
		this.nextReleaseAt = builder.nextReleaseAt;
		this.email = builder.email;
		this.language = builder.language;
		this.accessToken = builder.accessToken;
	}

	public static final class Builder {

		private Interval interval;

		private String query;

		private LinkedHashSet<JobAdvertisementId> matchedJobAdvertisementIds = new LinkedHashSet<>();

		private LocalDateTime createdAt;

		private LocalDateTime lastUpdatedAt;

		private LocalDateTime nextReleaseAt;

		private String email;

		private String language;

		private String accessToken;

		public Builder() {
		}

		public JobAlert build() {
			return new JobAlert(this);
		}

		public Builder setInterval(Interval interval) {
			this.interval = interval;
			return this;
		}

		public Builder setQuery(String query) {
			this.query = query;
			return this;
		}

		public Builder setMatchedJobAdvertisementIds(LinkedHashSet<JobAdvertisementId> matchedJobAdvertisementIds) {
			this.matchedJobAdvertisementIds = matchedJobAdvertisementIds;
			return this;
		}

		public Builder setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public Builder setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
			this.lastUpdatedAt = lastUpdatedAt;
			return this;
		}

		public Builder setNextReleaseAt(LocalDateTime nextReleaseAt) {
			this.nextReleaseAt = nextReleaseAt;
			return this;
		}

		public Builder setEmail(String email) {
			this.email = email;
			return this;
		}

		public Builder setLanguage(String language) {
			this.language = language;
			return this;
		}

		public Builder setAccessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}
	}

	private void removeEldestEntriesForRelease() {
		if (matchedJobAdvertisementIds.size() == MAX_ENTRIES_TO_BE_RELEASED + 1) {
			this.matchedJobAdvertisementIds.remove(Iterables.get(matchedJobAdvertisementIds, POSITION_OF_ELDEST_ENTRY));
		}

		else if (this.getMatchedJobAdvertisementIds().size() == MAX_ENTRIES_TO_BE_RELEASED + 2) {
			this.matchedJobAdvertisementIds.remove(Iterables.get(matchedJobAdvertisementIds, POSITION_OF_ELDEST_ENTRY));
			this.matchedJobAdvertisementIds.remove(Iterables.get(matchedJobAdvertisementIds, POSITION_OF_ELDEST_ENTRY));
		}

		else if (this.getMatchedJobAdvertisementIds().size() == MAX_ENTRIES_TO_BE_RELEASED + 3) {
			this.matchedJobAdvertisementIds.remove(Iterables.get(matchedJobAdvertisementIds, POSITION_OF_ELDEST_ENTRY));
			this.matchedJobAdvertisementIds.remove(Iterables.get(matchedJobAdvertisementIds, POSITION_OF_ELDEST_ENTRY));
			this.matchedJobAdvertisementIds.remove(Iterables.get(matchedJobAdvertisementIds, POSITION_OF_ELDEST_ENTRY));
		}
	}

	@Override
	public String toString() {
		return "JobAlert{" +
				"interval=" + interval +
				", query='" + query + '\'' +
				", matchedJobAdvertisementIds=" + matchedJobAdvertisementIds +
				", createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				", nextReleaseAt=" + nextReleaseAt +
				", email='" + email + '\'' +
				", accessToken='" + accessToken + '\'' +
				", language=" + language +
				'}';
	}
}
