package ch.admin.seco.jobs.services.jobadservice.infrastructure.scheduler.actuator.health;

import org.springframework.boot.actuate.health.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TaskHealthConfiguration {

    private final TaskHealthRepository taskHealthRepository;

    TaskHealthConfiguration(TaskHealthRepository taskHealthRepository) {
        this.taskHealthRepository = taskHealthRepository;
    }

    @Bean
    public HealthIndicator taskHealthIndicator(HealthAggregator healthAggregator) {
        return new TaskHealthIndicator(this.taskHealthRepository, healthAggregator);
    }

    static class TaskHealthIndicator implements HealthIndicator {

        private final TaskHealthRepository taskHealthRepository;

        private final HealthAggregator healthAggregator;

        TaskHealthIndicator(TaskHealthRepository taskHealthRepository, HealthAggregator healthAggregator) {
            this.taskHealthRepository = taskHealthRepository;
            this.healthAggregator = healthAggregator;
        }

        @Override
        public Health health() {
            final CompositeHealthIndicator compositeHealthIndicator = new CompositeHealthIndicator(healthAggregator, new DefaultHealthIndicatorRegistry());
            this.taskHealthRepository.findAll()
                    .forEach((id, taskHealthInformation) -> compositeHealthIndicator.getRegistry().register(id, new AbstractHealthIndicator() {
                        @Override
                        protected void doHealthCheck(Health.Builder builder) {
                            if (taskHealthInformation == null) {
                                builder.unknown();
                                return;
                            }

                            builder
                                    .withDetail("startedAt", taskHealthInformation.getStartedAt())
                                    .withDetail("endedAt", defaultIfNull(taskHealthInformation.getEndedAt(), "not finished"))
                                    .withDetail("duration", defaultIfNull(taskHealthInformation.getDuration(), "unknown"));
                            if (taskHealthInformation.getFailureException() == null) {
                                builder.up();
                            } else {
                                builder.down()
                                        .withDetail("error: ", taskHealthInformation.getFailureException().getMessage());
                            }
                        }
                    }));
            return compositeHealthIndicator.health();
        }

        private static Object defaultIfNull(Object value, String defaultValue) {
            return value != null ? value : defaultValue;
        }
    }

}
