package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.eures;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

public class EuresMessagingService {

    private final Logger LOG = LoggerFactory.getLogger(EuresMessagingService.class);

    private final JobAdvertisementApplicationService jobAdvertisementApplicationService;

    private final MessageChannel jobAdEventChannel;

    public EuresMessagingService(JobAdvertisementApplicationService jobAdvertisementApplicationService, MessageChannel jobAdEventChannel) {
        this.jobAdvertisementApplicationService = jobAdvertisementApplicationService;
        this.jobAdEventChannel = jobAdEventChannel;
    }

    public void sendEuresEventToMessageBroker(String jobAdvertisementId) {
        JobAdvertisementDto dto = jobAdvertisementApplicationService.getById(new JobAdvertisementId(jobAdvertisementId));
        LOG.debug("Send an Eures event through the message broker with JobAdvertisementStatus: '{}', JobAdvertisementId: '{}'", dto.getStatus(), dto.getId());
        jobAdEventChannel.send(MessageBuilder.withPayload(EuresEvent.from(dto.getId(), dto.getStatus())).build());
    }
}
