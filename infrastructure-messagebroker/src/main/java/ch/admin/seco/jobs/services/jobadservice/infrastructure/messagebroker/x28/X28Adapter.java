package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.x28;

import ch.admin.seco.alv.shared.spring.integration.leader.LeaderAware;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.x28.X28CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.function.Predicate;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.CREATE_FROM_X28_CONDITION;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.MessageBrokerChannels.JOB_AD_INT_ACTION_CHANNEL;

@Service
public class X28Adapter {

	private static final Logger LOG = LoggerFactory.getLogger(X28Adapter.class);

	private final JobAdvertisementApplicationService jobAdvertisementApplicationService;

	private final JobAdvertisementRepository jobAdvertisementRepository;

	private final TransactionTemplate transactionTemplate;

	private final X28MessageLogRepository x28MessageLogRepository;

	public X28Adapter(JobAdvertisementApplicationService jobAdvertisementApplicationService,
			JobAdvertisementRepository jobAdvertisementRepository,
			TransactionTemplate transactionTemplate,
			X28MessageLogRepository x28MessageLogRepository) {
		this.jobAdvertisementApplicationService = jobAdvertisementApplicationService;
		this.jobAdvertisementRepository = jobAdvertisementRepository;
		this.transactionTemplate = transactionTemplate;
		this.x28MessageLogRepository = x28MessageLogRepository;
	}

	@Scheduled(cron = "${jobAdvertisement.checkExternalJobAdExpiration.cron}")
	@LeaderAware("scheduler")
	public void scheduledArchiveExternalJobAds() {
		LOG.info("Starting scheduledArchiveExternalJobAds");

		if (isX28MessageReceived(today())) {
			Long archivedJobCount = this.transactionTemplate.execute(status -> archiveExternalJobAds());

			LOG.info("Archived external jobs: {}", archivedJobCount);
		} else {
			LOG.info("Archiving skipped because no x28 message was received. Please check the x28 import job!");
		}
	}

	@StreamListener(target = JOB_AD_INT_ACTION_CHANNEL, condition = CREATE_FROM_X28_CONDITION)
	public void handleCreateFromX28Action(X28CreateJobAdvertisementDto createFromX28) {
		logLastX28MessageDate(createFromX28.getFingerprint());

		final JobAdvertisementId jobAdvertisementId = determineJobAdvertisementId(createFromX28.getStellennummerEgov(), createFromX28.getStellennummerAvam());

		if (jobAdvertisementId != null) {
			jobAdvertisementApplicationService.enrichFromExtern(jobAdvertisementId, createFromX28.getFingerprint(), createFromX28.getProfessionCodes());
		} else {
			final JobAdvertisementId externalJobAdvertisementId = findJobAdvertisementIdByFingerprint(createFromX28.getFingerprint());

			if (externalJobAdvertisementId != null) {
				jobAdvertisementApplicationService.updateFromExtern(externalJobAdvertisementId, createFromX28);
			} else {
				jobAdvertisementApplicationService.createFromExtern(createFromX28);
			}
		}
	}

	@Nullable
	private JobAdvertisementId determineJobAdvertisementId(String stellennummerEgov, String stellennummerAvam) {

		if (stellennummerEgov != null) {
			JobAdvertisementId jobAdvertisementId = transactionTemplate.execute(status ->
					jobAdvertisementRepository.findByStellennummerEgov(stellennummerEgov)
							.map(JobAdvertisement::getId)
							.orElse(null)
			);

			if (jobAdvertisementId != null) {
				return jobAdvertisementId;
			}
		}

		if (stellennummerAvam != null) {

			return transactionTemplate.execute(status ->
					jobAdvertisementRepository.findByStellennummerAvam(stellennummerAvam)
							.map(JobAdvertisement::getId)
							.orElse(null)
			);
		}

		return null;
	}

	@Nullable
	private JobAdvertisementId findJobAdvertisementIdByFingerprint(String fingerprint) {
		return transactionTemplate.execute(status -> jobAdvertisementRepository.findByFingerprint(fingerprint)
				.map(JobAdvertisement::getId)
				.orElse(null)
		);
	}

	private long archiveExternalJobAds() {
		final LocalDate today = today();

		Predicate<JobAdvertisement> shouldArchive = jobAdvertisement ->
				this.x28MessageLogRepository.findById(jobAdvertisement.getFingerprint())
						.map(X28MessageLog::getLastMessageDate)
						.map(lastMessageDate -> lastMessageDate.isBefore(today))
						.orElse(Boolean.TRUE);

		return this.jobAdvertisementRepository.findAllPublishedExtern()
				.filter(shouldArchive)
				.map(jobAdvertisement -> {
					jobAdvertisement.expirePublication();
					return jobAdvertisement.getId();
				})
				.count();
	}

	private void logLastX28MessageDate(String fingerprint) {
		X28MessageLog x28MessageLog = new X28MessageLog(fingerprint, today());
		x28MessageLogRepository.save(x28MessageLog);
	}

	private boolean isX28MessageReceived(LocalDate date) {
		return x28MessageLogRepository.countByLastMessageDateEquals(date) > 0;
	}

	private LocalDate today() {
		return TimeMachine.now().toLocalDate();
	}
}
