package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementAlreadyExistsException;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.AvamCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.ApprovalDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.CancellationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.RejectionDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.util.StopWatch;

import static ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition.notNull;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.APPROVE_CONDITION;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.CANCEL_CONDITION;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.CREATE_FROM_AVAM_CONDITION;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.JOB_AD_INT_ACTION_CHANNEL;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.REJECT_CONDITION;

public class AvamEventReceiverGateway {

    private final Logger LOG = LoggerFactory.getLogger(AvamEventReceiverGateway.class);

    private final JobCenterService jobCenterService;

    private final JobAdvertisementApplicationService jobAdvertisementApplicationService;

    private final AvamMailSender avamMailSender;

    public AvamEventReceiverGateway(JobAdvertisementApplicationService jobAdvertisementApplicationService,
                                    JobCenterService jobCenterService,
                                    AvamMailSender avamMailSender) {
        this.jobAdvertisementApplicationService = jobAdvertisementApplicationService;
        this.jobCenterService = jobCenterService;
        this.avamMailSender = avamMailSender;
    }

    @StreamListener(target = JOB_AD_INT_ACTION_CHANNEL, condition = APPROVE_CONDITION)
    public void handleApprovedAction(ApprovalDto approvalDto) {
        StopWatch stopWatch = new StopWatch();
        LOG.trace(".start handleApprovedAction [stellennummerEgov = {}, stellennummerAvam = {}]", approvalDto.getStellennummerEgov(), approvalDto.getStellennummerEgov());

        startTask("..", "jobAdvertisementApplicationService.getByStellennummerEgovOrAvam", stopWatch);
        JobAdvertisementDto jobAdvertisementDto = jobAdvertisementApplicationService.getByStellennummerEgovOrAvam(approvalDto.getStellennummerEgov(), approvalDto.getStellennummerAvam());
        stopTask(stopWatch);

        notNull(jobAdvertisementDto, "Couldn't find the jobAdvertisement to approve for stellennummerEgov %s nor stellennummerAvam %s", approvalDto.getStellennummerEgov(), approvalDto.getStellennummerAvam());

        startTask("...", "jobAdvertisementApplicationService.approve", stopWatch);
        jobAdvertisementApplicationService.approve(approvalDto);
        stopTask(stopWatch);

        LOG.trace("...finished handleApprovedAction [stellennummerEgov = {}, stellennummerAvam = {}] in {} ms",
                approvalDto.getStellennummerEgov(), approvalDto.getStellennummerEgov(), stopWatch.getTotalTimeMillis());
    }

    @StreamListener(target = JOB_AD_INT_ACTION_CHANNEL, condition = REJECT_CONDITION)
    public void handleRejectAction(RejectionDto rejectionDto) {
        StopWatch stopWatch = new StopWatch();
        LOG.trace(".start handleRejectAction [stellennummerEgov = {}]", rejectionDto.getStellennummerEgov());

        startTask("..", "jobAdvertisementApplicationService.reject", stopWatch);
        jobAdvertisementApplicationService.reject(rejectionDto);
        stopTask(stopWatch);

        LOG.trace("..finished handleRejectAction [stellennummerEgov = {}] in {} ms",
                rejectionDto.getStellennummerEgov(), stopWatch.getTotalTimeMillis());
    }

    @StreamListener(target = JOB_AD_INT_ACTION_CHANNEL, condition = CREATE_FROM_AVAM_CONDITION)
    public void handleCreateAction(AvamCreateJobAdvertisementDto createJobAdvertisementFromAvamDto) {
        StopWatch stopWatch = new StopWatch();
        LOG.trace(".start handleCreateAction [stellennummerAvam = {}]", createJobAdvertisementFromAvamDto.getStellennummerAvam());

        try {
            startTask("..", "jobAdvertisementApplicationService.createFromAvam", stopWatch);
            jobAdvertisementApplicationService.createFromAvam(createJobAdvertisementFromAvamDto);
            stopTask(stopWatch);
        } catch (JobAdvertisementAlreadyExistsException e) {
            LOG.debug(e.getMessage());
        }

        LOG.trace("..finished handleCreateAction [stellennummerAvam = {}] in {} ms",
                createJobAdvertisementFromAvamDto.getStellennummerAvam(), stopWatch.getTotalTimeMillis());
    }

    @StreamListener(target = JOB_AD_INT_ACTION_CHANNEL, condition = CANCEL_CONDITION)
    public void handleCancelAction(AvamCancellationDto avamCancellationDto) {
        StopWatch stopWatch = new StopWatch();

        LOG.trace(".start handleCancelAction [stellennummerEgov = {}, stellennummerAvam = {}]", avamCancellationDto.getStellennummerEgov(), avamCancellationDto.getStellennummerEgov());

        startTask("..", "jobAdvertisementApplicationService.findByStellennummerEgovOrAvam", stopWatch);
        JobAdvertisementDto jobAdvertisementDto = jobAdvertisementApplicationService.findByStellennummerEgovOrAvam(avamCancellationDto.getStellennummerEgov(), avamCancellationDto.getStellennummerAvam());
        stopTask(stopWatch);

        if ((jobAdvertisementDto == null) && (avamCancellationDto.getSourceSystem() == SourceSystem.RAV)) {

            LOG.trace("...");

            if (avamCancellationDto.getContactEmail() != null) {

                LOG.info("Cancellation of an unknown jobAdvertisement from AVAM with stellennummerAvam {}", avamCancellationDto.getStellennummerAvam());

                startTask("....", "jobCenterService.findJobCenterByCode", stopWatch);
                final JobCenter jobCenter = jobCenterService.findJobCenterByCode(avamCancellationDto.getJobCenterCode());
                stopTask(stopWatch);

                startTask(".....", "jobCenterService.findJobCenterByCode", stopWatch);
                avamMailSender.sendCancellation(avamCancellationDto, jobCenter);
                stopTask(stopWatch);

                return;
            } else {
                LOG.info("No cancellation mail sent of unknown JobAdvertisement from AVAM with stellennummerAvam {}", avamCancellationDto.getStellennummerAvam());
                return;
            }
        }

        notNull(jobAdvertisementDto, "Couldn't find the jobAdvertisement to cancel for stellennummerEgov %s nor stellennummerAvam %s", avamCancellationDto.getStellennummerEgov(), avamCancellationDto.getStellennummerAvam());

        CancellationDto cancellationDto = AvamCancellationDto.toDto(avamCancellationDto);

        startTask("........", "jobAdvertisementApplicationService.cancel", stopWatch);
        jobAdvertisementApplicationService.cancel(
                new JobAdvertisementId(jobAdvertisementDto.getId()),
                cancellationDto,
                null
        );
        stopTask(stopWatch);

        LOG.trace(".........finished handleCancelAction [stellennummerEgov = {}, stellennummerAvam = {}] in {} ms",
                avamCancellationDto.getStellennummerEgov(), avamCancellationDto.getStellennummerEgov(), stopWatch.getTotalTimeMillis());
    }

    private void startTask(String prefix, String task, StopWatch stopWatch) {
        LOG.trace(prefix + " start: {}", task);
        stopWatch.start(task);
    }

    private void stopTask(StopWatch stopWatch) {
        stopWatch.stop();
        LOG.trace("finished: {} in {} ms", stopWatch.getLastTaskName(), stopWatch.getLastTaskTimeMillis());
    }

}
