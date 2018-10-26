package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.events;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventType;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ConfigurationProperties("domainEventGateway")
@Validated
public class DomainEventGatewayProperties {

    private static Set<DomainEventType> DEFAULT_RELEVANT_EVENT_TYPES = new HashSet<>(
            Arrays.asList(
                    JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_PUBLIC.getDomainEventType(),
                    JobAdvertisementEvents.JOB_ADVERTISEMENT_UPDATED.getDomainEventType(),
                    JobAdvertisementEvents.JOB_ADVERTISEMENT_CANCELLED.getDomainEventType(),
                    JobAdvertisementEvents.JOB_ADVERTISEMENT_ARCHIVED.getDomainEventType()
            )
    );

    @NotNull
    private Set<DomainEventType> relevantEventTypes = DEFAULT_RELEVANT_EVENT_TYPES;

    public Set<DomainEventType> getRelevantEventTypes() {
        return relevantEventTypes;
    }

    public DomainEventGatewayProperties setRelevantEventTypes(Set<DomainEventType> relevantEventTypes) {
        this.relevantEventTypes = relevantEventTypes;
        return this;
    }
}
