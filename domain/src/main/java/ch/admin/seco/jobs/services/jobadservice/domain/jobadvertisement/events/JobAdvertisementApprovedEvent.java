package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.changes.ChangeLog;

/**
 * AVAM has approved the JobAdvertisement
 */
public class JobAdvertisementApprovedEvent extends JobAdvertisementEvent {

    public JobAdvertisementApprovedEvent(JobAdvertisement jobAdvertisement) {
        super(JobAdvertisementEvents.JOB_ADVERTISEMENT_APPROVED, jobAdvertisement);
    }

    public JobAdvertisementApprovedEvent(JobAdvertisement jobAdvertisement, ChangeLog changeLog) {
        super(JobAdvertisementEvents.JOB_ADVERTISEMENT_APPROVED, jobAdvertisement);
        additionalAttributes.put("changeLog", changeLog);
    }

}
