package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface MessageBrokerChannels {
    String CREATE_FROM_EXTERNAL_CONDITION = "headers['action']=='CREATE_FROM_EXTERNAL'";
    String APPROVE_CONDITION = "headers['action']=='APPROVE'";
    String UPDATE_CONDITION = "headers['action']=='UPDATE'";
    String REJECT_CONDITION = "headers['action']=='REJECT'";
    String CANCEL_CONDITION = "headers['action']=='CANCEL'";
    String INACTIVATE_CONDITION = "headers['action']=='INACTIVATE'";
    String REACTIVATE_CONDITION = "headers['action']=='REACTIVATE'";
    String DELETE_CONDITION = "headers['action']=='DELETE'";
    String CREATE_FROM_AVAM_CONDITION = "headers['action']=='CREATE_FROM_AVAM'";

    String JOB_AD_EVENT_CHANNEL = "job-ad-event";

    String JOB_AD_INT_ACTION_CHANNEL = "job-ad-int-action";

    String JOB_AD_INT_EVENT_CHANNEL = "job-ad-int-event";

    String USER_EVENT_CHANNEL = "user-event";

    @Input(USER_EVENT_CHANNEL)
    SubscribableChannel userEventChannel();

    @Input(JOB_AD_INT_ACTION_CHANNEL)
    SubscribableChannel jobAdIntActionChannel();

    @Output(JOB_AD_INT_EVENT_CHANNEL)
    MessageChannel jobAdIntEventChannel();

    @Output(JOB_AD_EVENT_CHANNEL)
    MessageChannel jobAdEventChannel();
}
