package ch.admin.seco.jobs.services.jobadservice.infrastructure.scheduler;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import ch.admin.seco.alv.shared.spring.integration.leader.LeaderAware;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;

@Configuration
public class ScheduledTasks {

	private final JobAdvertisementApplicationService jobAdvertisementApplicationService;

	public ScheduledTasks(JobAdvertisementApplicationService jobAdvertisementApplicationService) {
		this.jobAdvertisementApplicationService = jobAdvertisementApplicationService;
	}

	@Scheduled(cron = "${jobAdvertisement.checkPublicationStarts.cron}")
	@LeaderAware("scheduler")
	public void scheduledCheckPublicationStarts() {
		this.jobAdvertisementApplicationService.checkPublicationStarts();
	}

	@Scheduled(cron = "${jobAdvertisement.checkBlackoutPolicyExpiration.cron}")
	@LeaderAware("scheduler")
	public void scheduledCheckBlackoutPolicyExpiration() {
		this.jobAdvertisementApplicationService.checkBlackoutPolicyExpiration();
	}

	@Scheduled(cron = "${jobAdvertisement.checkPublicationExpiration.cron}")
	@LeaderAware("scheduler")
	public void scheduledCheckPublicationExpiration() {
		this.jobAdvertisementApplicationService.checkBlackoutPolicyExpiration();
	}

}
