package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;

public class JobAdvertisementAdjournedPublicationEvent extends JobAdvertisementEvent {
    public JobAdvertisementAdjournedPublicationEvent(JobAdvertisement jobAdvertisement) {
        super(JobAdvertisementEvents.JOB_ADVERTISEMENT_ADJOURNED_PUBLICATION, jobAdvertisement);
    }
}
