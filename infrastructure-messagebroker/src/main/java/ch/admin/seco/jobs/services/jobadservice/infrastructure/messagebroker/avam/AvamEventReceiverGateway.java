package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementAlreadyExistsException;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.ApprovalDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.CancellationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.RejectionDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;

import static ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition.notNull;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.*;

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
		JobAdvertisementDto jobAdvertisementDto = jobAdvertisementApplicationService.getByStellennummerEgovOrAvam(approvalDto.getStellennummerEgov(), approvalDto.getStellennummerAvam());
		notNull(jobAdvertisementDto, "Couldn't find the jobAdvertisement to approve for stellennummerEgov %s nor stellennummerAvam %s", approvalDto.getStellennummerEgov(), approvalDto.getStellennummerAvam());
		jobAdvertisementApplicationService.approve(approvalDto);
	}

	@StreamListener(target = JOB_AD_INT_ACTION_CHANNEL, condition = REJECT_CONDITION)
	public void handleRejectAction(RejectionDto rejectionDto) {
		jobAdvertisementApplicationService.reject(rejectionDto);
	}

	@StreamListener(target = JOB_AD_INT_ACTION_CHANNEL, condition = CREATE_FROM_AVAM_CONDITION)
	public void handleCreateAction(AvamCreateJobAdvertisementDto avamCreateJobAdvertisementDto) {
		try {
			jobAdvertisementApplicationService.createFromAvam(AvamCreateJobAdvertisementDto.toDto(avamCreateJobAdvertisementDto));
		} catch (JobAdvertisementAlreadyExistsException e) {
			LOG.debug(e.getMessage());
		}
	}

	@StreamListener(target = JOB_AD_INT_ACTION_CHANNEL, condition = CANCEL_CONDITION)
	public void handleCancelAction(AvamCancellationDto avamCancellationDto) {
		JobAdvertisementDto jobAdvertisementDto = jobAdvertisementApplicationService.findByStellennummerEgovOrAvam(avamCancellationDto.getStellennummerEgov(), avamCancellationDto.getStellennummerAvam());

		if ((jobAdvertisementDto == null) && (avamCancellationDto.getSourceSystem() == SourceSystem.RAV)) {
            if (avamCancellationDto.getContactEmail() != null) {
                LOG.info("Cancellation of an unknown jobAdvertisement from AVAM with stellennummerAvam {}", avamCancellationDto.getStellennummerAvam());
                final JobCenter jobCenter = jobCenterService.findJobCenterByCode(avamCancellationDto.getJobCenterCode());
                avamMailSender.sendCancellation(avamCancellationDto, jobCenter);
                return;
            } else {
                LOG.info("No cancellation mail sent of unknown JobAdvertisement from AVAM with stellennummerAvam {}", avamCancellationDto.getStellennummerAvam());
                return;
            }
		}

		notNull(jobAdvertisementDto, "Couldn't find the jobAdvertisement to cancel for stellennummerEgov %s nor stellennummerAvam %s", avamCancellationDto.getStellennummerEgov(), avamCancellationDto.getStellennummerAvam());

		CancellationDto cancellationDto = AvamCancellationDto.toDto(avamCancellationDto);
		jobAdvertisementApplicationService.cancel(
				new JobAdvertisementId(jobAdvertisementDto.getId()),
				cancellationDto,
				null
		);
	}

}
