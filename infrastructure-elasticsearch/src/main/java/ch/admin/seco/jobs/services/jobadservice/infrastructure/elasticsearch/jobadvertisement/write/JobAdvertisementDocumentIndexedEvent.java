package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write;

import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateId;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvent;

public class JobAdvertisementDocumentIndexedEvent extends DomainEvent<JobAdvertisementId> {

	private final JobAdvertisementEvent originalEvent;

	public JobAdvertisementDocumentIndexedEvent(JobAdvertisementEvent event) {
		super(event.getDomainEventType(), JobAdvertisementDocument.class.getSimpleName());
		this.originalEvent = event;
	}

	public JobAdvertisementEvent getOriginalEvent() {
		return originalEvent;
	}

	@Override
	public AggregateId<JobAdvertisementId> getAggregateId() {
		return this.originalEvent.getAggregateId();
	}
}
