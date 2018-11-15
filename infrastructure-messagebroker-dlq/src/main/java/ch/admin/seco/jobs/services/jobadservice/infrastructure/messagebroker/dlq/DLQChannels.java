package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.dlq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface DLQChannels {

    String JOB_AD_INT_EVENT_DLQ_CHANNEL = "job-ad-int-event-dlq";

    String JOB_AD_INT_ACTION_DLQ_CHANNEL = "job-ad-int-action-dlq";

    @Input(JOB_AD_INT_EVENT_DLQ_CHANNEL)
    SubscribableChannel jobAdEventDLQChannel();

    @Input(JOB_AD_INT_ACTION_DLQ_CHANNEL)
    SubscribableChannel jobAdActionDLQChannel();
}
