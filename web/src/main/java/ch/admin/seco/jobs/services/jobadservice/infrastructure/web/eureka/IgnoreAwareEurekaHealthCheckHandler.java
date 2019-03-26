package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.eureka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.netflix.eureka.EurekaHealthCheckHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter out certain {@link org.springframework.boot.actuate.health.HealthIndicator} that are not relevant for eureka
 * Why? We don't wan't the Registry to indicate that this service is down due to the ignored {@link org.springframework.boot.actuate.health.HealthIndicator}
 * For Example: if the {@link org.springframework.boot.actuate.mail.MailHealthIndicator} indicates 'DOWN' this won't be
 * a problem for our JobAdService since the Mails are saved to the Database until the {@link org.springframework.boot.actuate.mail.MailHealthIndicator} is 'UP'
 */
class IgnoreAwareEurekaHealthCheckHandler extends EurekaHealthCheckHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(IgnoreAwareEurekaHealthCheckHandler.class);

    private final List<String> ignoredHealthIndicators = new ArrayList<>();

    IgnoreAwareEurekaHealthCheckHandler(HealthAggregator healthAggregator, List<String> ignoredHealthIndicators) {
        super(healthAggregator);
        this.ignoredHealthIndicators.addAll(ignoredHealthIndicators);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        for (String ignoredHealthIndicatorName : ignoredHealthIndicators) {
            HealthIndicator unregisteredHealthIndicator = super.getHealthIndicator().getRegistry().unregister(ignoredHealthIndicatorName);
            if (unregisteredHealthIndicator != null) {
                LOGGER.info("Ignored the HealthIndicator: {} for the Eureka-Registry", ignoredHealthIndicatorName);
            } else {
                LOGGER.warn("Can not ignore the HealthIndicator: {} since it was not found", ignoredHealthIndicatorName);
            }
        }
    }
}
