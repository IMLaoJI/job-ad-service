package ch.admin.seco.jobs.services.jobadservice.infrastructure.businesslogging;

import ch.admin.seco.jobs.services.jobadservice.application.BusinessLogData;
import ch.admin.seco.jobs.services.jobadservice.application.BusinessLogger;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUser;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static net.logstash.logback.argument.StructuredArguments.entries;

@Component
public class LogstashBusinessLogger implements BusinessLogger {

    private static final String AUTHORITIES_KEY = "authorities";

    private static final String EVENT_TYPE_KEY = "eventType";

    private static final String OBJECT_TYPE_KEY = "objectType";

    private static final String OBJECT_ID_KEY = "objectId";

    private final Logger log;

    private final Marker businessLogMarker;

    private final CurrentUserContext currentUserContext;

    public LogstashBusinessLogger(CurrentUserContext currentUserContext) {
        this.currentUserContext = currentUserContext;
        this.businessLogMarker = MarkerFactory.getMarker("BUSINESS_LOG");
        this.log = LoggerFactory.getLogger(LogstashBusinessLogger.class);
    }

    @Override
    public void log(BusinessLogData businessLogData) {
        final HashMap<String, Object> businessLogEntry = new HashMap<>();

        businessLogEntry.put(AUTHORITIES_KEY, extractAuthorities(currentUserContext.getCurrentUser()));
        businessLogEntry.put(EVENT_TYPE_KEY, businessLogData.getEventType());
        businessLogEntry.put(OBJECT_TYPE_KEY, businessLogData.getObjectType());
        businessLogEntry.put(OBJECT_ID_KEY, businessLogData.getObjectId());
        businessLogEntry.putAll(businessLogData.getAdditionalData());

        log.info(this.businessLogMarker, "BusinessLogEntry: {}", entries(businessLogEntry));
    }

    private String extractAuthorities(CurrentUser currentUser) {
        return currentUser != null ? currentUser.getAuthorities().toString() : null;
    }
}
