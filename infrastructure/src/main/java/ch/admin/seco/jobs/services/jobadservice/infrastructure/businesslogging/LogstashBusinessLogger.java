package ch.admin.seco.jobs.services.jobadservice.infrastructure.businesslogging;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import ch.admin.seco.alv.shared.logger.business.BusinessLogData;
import ch.admin.seco.jobs.services.jobadservice.application.BusinessLogEvent;
import ch.admin.seco.jobs.services.jobadservice.application.BusinessLogger;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUser;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;

@Component
public class LogstashBusinessLogger implements BusinessLogger {

	private final CurrentUserContext currentUserContext;

	private final ch.admin.seco.alv.shared.logger.business.BusinessLogger businessLogger;

	public LogstashBusinessLogger(CurrentUserContext currentUserContext, ch.admin.seco.alv.shared.logger.business.BusinessLogger businessLogger) {
		this.currentUserContext = currentUserContext;
		this.businessLogger = businessLogger;
	}

	@Override
	public void log(BusinessLogEvent businessLogEvent) {
		BusinessLogData businessLogData = new BusinessLogData(businessLogEvent.getEventType().getTypeName())
				.withObjectId(businessLogEvent.getObjectId())
				.withAuthorities(extractAuthorities(currentUserContext.getCurrentUser()))
				.withObjectType(businessLogEvent.getObjectType().getTypeName())
				.withAdditionalData(businessLogEvent.getAdditionalData());
		businessLogger.log(businessLogData);
	}

	private String extractAuthorities(CurrentUser currentUser) {
		return currentUser != null ? String.join(" ", currentUser.getAuthorities()) : Strings.EMPTY;
	}
}
