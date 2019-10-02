package ch.admin.seco.jobs.services.jobadservice.infrastructure.scheduler;

import ch.admin.seco.alv.shared.spring.integration.leader.LeaderAware;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.scheduler.actuator.endpoint.ManualTaskTrigger;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.scheduler.actuator.health.TaskHealth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class ScheduledTasks {

    private final JobAdvertisementApplicationService jobAdvertisementApplicationService;

    public ScheduledTasks(JobAdvertisementApplicationService jobAdvertisementApplicationService) {
        this.jobAdvertisementApplicationService = jobAdvertisementApplicationService;
    }

    @Bean
    ManualTaskTrigger checkPublicationStarts() {
        return new ManualTaskTrigger() {
            @Override
            @Scheduled(cron = "${jobAdvertisement.checkPublicationStarts.cron}")
            @LeaderAware("scheduler")
            @TaskHealth("checkPublicationStarts")
            public void invoke() {
                jobAdvertisementApplicationService.checkPublicationStarts();
            }
        };
    }

    @Bean
    ManualTaskTrigger checkBlackoutPolicyExpiration() {
        return new ManualTaskTrigger() {
            @Override
            @Scheduled(cron = "${jobAdvertisement.checkBlackoutPolicyExpiration.cron}")
            @LeaderAware("scheduler")
            @TaskHealth("checkBlackoutPolicyExpiration")
            public void invoke() {
                jobAdvertisementApplicationService.checkBlackoutPolicyExpiration();
            }
        };
    }

    @Bean
    ManualTaskTrigger checkPublicationExpiration() {
        return new ManualTaskTrigger() {
            @Override
            @Scheduled(cron = "${jobAdvertisement.checkPublicationExpiration.cron}")
            @LeaderAware("scheduler")
            @TaskHealth("checkPublicationExpiration")
            public void invoke() {
                jobAdvertisementApplicationService.checkPublicationExpiration();
            }
        };
    }

    @Bean
    ManualTaskTrigger archiveExternalJobAdvertisements() {
        return new ManualTaskTrigger() {
            @Override
            @Scheduled(cron = "${jobAdvertisement.checkExternalJobAdExpiration.cron}")
            @LeaderAware("scheduler")
            @TaskHealth("archiveExternalJobAdvertisements")
            public void invoke() {
                jobAdvertisementApplicationService.archiveExternalJobAdvertisements();
            }
        };
    }
}
