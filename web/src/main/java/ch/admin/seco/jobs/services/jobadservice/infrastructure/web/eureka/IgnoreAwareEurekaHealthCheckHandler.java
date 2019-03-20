package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.eureka;

import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.netflix.eureka.EurekaHealthCheckHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class IgnoreAwareEurekaHealthCheckHandler extends EurekaHealthCheckHandler {

    private final List<String> ignoredHealthIndicators = new ArrayList<>();

    IgnoreAwareEurekaHealthCheckHandler(HealthAggregator healthAggregator, List<String> ignoredHealthIndicators) {
        super(healthAggregator);
        this.ignoredHealthIndicators.addAll(
                ignoredHealthIndicators.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Map<String, HealthIndicator> healthIndicators = getHealthIndicatorMap(super.getHealthIndicator());
        healthIndicators.keySet()
                .removeIf(healthIndicator -> ignoredHealthIndicators.contains(healthIndicator.toLowerCase()));
    }

    private Map<String, HealthIndicator> getHealthIndicatorMap(CompositeHealthIndicator healthIndicator) {
        return new HashMap<>(healthIndicator.getRegistry().getAll());
    }
}
