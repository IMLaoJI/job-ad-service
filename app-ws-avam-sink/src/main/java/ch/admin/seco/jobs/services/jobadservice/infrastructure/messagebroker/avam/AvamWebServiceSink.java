package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;


import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;

@EnableBinding(Sink.class)
public class AvamWebServiceSink {

    private final AvamWebServiceClient avamWebServiceClient;

    public AvamWebServiceSink(AvamWebServiceClient avamWebServiceClient) {
        this.avamWebServiceClient = avamWebServiceClient;
    }

    @StreamListener(target = Sink.INPUT, condition = "headers[event]=='JOB_ADVERTISEMENT_INSPECTING'")
    public void register(JobAdvertisement jobAdvertisement) {
        avamWebServiceClient.register(jobAdvertisement);
    }

    @StreamListener(target = Sink.INPUT, condition = "headers[event]=='JOB_ADVERTISEMENT_CANCELLED'")
    public void deregister(JobAdvertisement jobAdvertisement) {
        if (mustSendDeregistratonNotification(jobAdvertisement)) {
            avamWebServiceClient.deregister(jobAdvertisement);
        }
    }

    private boolean mustSendDeregistratonNotification(JobAdvertisement jobAdvertisement) {
        return jobAdvertisement.getStellennummerAvam() != null;
    }
}
