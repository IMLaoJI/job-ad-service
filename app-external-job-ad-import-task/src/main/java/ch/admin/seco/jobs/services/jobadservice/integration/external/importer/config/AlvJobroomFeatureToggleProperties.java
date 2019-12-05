package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "alv.feature.toggle")
public class AlvJobroomFeatureToggleProperties {

    private boolean newAvamCodeEnabled;

    public boolean isNewAvamCodeEnabled() {
        return newAvamCodeEnabled;
    }

    public void setNewAvamCodeEnabled(boolean newAvamCodeEnabled) {
        this.newAvamCodeEnabled = newAvamCodeEnabled;
    }
}
