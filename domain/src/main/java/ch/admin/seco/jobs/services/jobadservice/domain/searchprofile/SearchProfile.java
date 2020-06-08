package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.Aggregate;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventPublisher;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.AccessTokenGenerator;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.JobAlertReleasedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.JobAlertSubscribedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.JobAlertUnsubscribedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileUpdatedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.Interval;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.JobAlert;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.SearchFilter;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.JobAlert.calculateReleaseTime;

@Entity
public class SearchProfile implements Aggregate<SearchProfile, SearchProfileId> {

	@EmbeddedId
	@AttributeOverride(name = "value", column = @Column(name = "ID"))
	@Valid
	private SearchProfileId id;

	@NotNull
	private LocalDateTime createdTime;

	@NotNull
	private LocalDateTime updatedTime;

	@NotBlank
	@Size(max = 50)
	private String name;

	@NotBlank
	private String ownerUserId;

	@Embedded
	@Valid
	@NotNull
	private SearchFilter searchFilter;

	@Embedded
	@Valid
	private JobAlert jobAlert;


	private SearchProfile() {
		// FOR REFLECTION
	}

	public static Builder builder() {
		return new Builder();
	}

	private SearchProfile(Builder builder) {
		this.id = Condition.notNull(builder.id);
		this.createdTime = TimeMachine.now();
		this.updatedTime = this.createdTime;
		this.name = Condition.notNull(builder.name);
		this.ownerUserId = Condition.notBlank(builder.ownerUserId);
		this.searchFilter = Condition.notNull(builder.searchFilter);
		this.jobAlert = builder.jobAlert;
	}

	public SearchProfileId getId() {
		return id;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public LocalDateTime getUpdatedTime() {
		return updatedTime;
	}

	public String getName() {
		return name;
	}

	public String getOwnerUserId() {
		return ownerUserId;
	}

	public SearchFilter getSearchFilter() {
		return searchFilter;
	}

	public JobAlert getJobAlert() {
		return jobAlert;
	}

	public void update(String name, SearchFilter searchFilter) {
		this.name = name;
		this.searchFilter = searchFilter;
		this.updatedTime = TimeMachine.now();
		DomainEventPublisher.publish(new SearchProfileUpdatedEvent(this));
	}


	public void subscribeToJobAlert(Interval interval, String email, String language) {
		AccessTokenGenerator accessTokenGenerator = new AccessTokenGenerator();
		this.jobAlert = JobAlert.builder()
				.setMatchedJobAdvertisementIds(new LinkedHashSet<>())
				.setLastUpdatedAt(TimeMachine.now())
				.setInterval(interval)
				.setCreatedAt(TimeMachine.now())
				.setNextReleaseAt(calculateReleaseTime(interval))
				.setEmail(email)
				.setLanguage(language)
				.setAccessToken(accessTokenGenerator.generateToken())
				.build();
		DomainEventPublisher.publish(new JobAlertSubscribedEvent(this));
	}

	public void unsubscribeFromJobAlert() {
		this.jobAlert = JobAlert.builder()
				.setMatchedJobAdvertisementIds(new LinkedHashSet<>())
				.setLastUpdatedAt(null)
				.setInterval(null)
				.setCreatedAt(null)
				.setNextReleaseAt(null)
				.setEmail(null)
				.setLanguage(null)
				.setAccessToken(null)
				.build();
		DomainEventPublisher.publish(new JobAlertUnsubscribedEvent(this));
	}

	public void release() {
		List<JobAdvertisementId> matchedIds = new ArrayList<>(this.jobAlert.getMatchedJobAdvertisementIdsForRelease());
		this.jobAlert.clearMatchedIdsAndCalculateNextRelease();
		DomainEventPublisher.publish(new JobAlertReleasedEvent(this, matchedIds));
	}

	public static final class Builder {

		private SearchProfileId id;

		private String name;

		private String ownerUserId;

		private SearchFilter searchFilter;

		private JobAlert jobAlert;

		public Builder() {
		}

		public SearchProfile build() {
			return new SearchProfile(this);
		}

		public Builder setId(SearchProfileId id) {
			this.id = id;
			return this;
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setOwnerUserId(String ownerUserId) {
			this.ownerUserId = ownerUserId;
			return this;
		}

		public Builder setSearchFilter(SearchFilter searchFilter) {
			this.searchFilter = searchFilter;
			return this;
		}

		public Builder setJobAlert(JobAlert jobAlert) {
			this.jobAlert = jobAlert;
			return this;
		}
	}

	@Override
	public String toString() {
		return "SearchProfile{" +
				" id=" + id.getValue() + '\'' +
				",name=" + name + '\'' +
				",ownerUserId=" + ownerUserId + '\'' +
				"}";
	}
}
