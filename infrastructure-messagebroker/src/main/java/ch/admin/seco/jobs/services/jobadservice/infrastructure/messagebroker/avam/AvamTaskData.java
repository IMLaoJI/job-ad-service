package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;

class AvamTaskData {

     private JobAdvertisementId jobAdvertisementId;

     private AvamTaskType type;

     private AvamTaskData() {
        // FOR REFLECTION
    }

     AvamTaskData(JobAdvertisementId jobAdvertisementId, AvamTaskType type) {
        this.jobAdvertisementId = Condition.notNull(jobAdvertisementId);
        this.type = Condition.notNull(type);
    }

     JobAdvertisementId getJobAdvertisementId() {
        return jobAdvertisementId;
    }

     AvamTaskType getType() {
        return type;
    }

}