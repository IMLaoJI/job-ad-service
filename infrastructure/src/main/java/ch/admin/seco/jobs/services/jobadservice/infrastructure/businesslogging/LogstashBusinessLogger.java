package ch.admin.seco.jobs.services.jobadservice.infrastructure.businesslogging;

import org.springframework.stereotype.Component;

import ch.admin.seco.alv.shared.logger.business.BusinessLogData;
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
	public void log(BusinessLogData businessLogData) {
		BusinessLogData businessLogEvent = new BusinessLogData(businessLogData.getEventType())
				.withObjectId(businessLogData.getObjectId())
				.withAuthorities(extractAuthorities(currentUserContext.getCurrentUser()))
				.withObjectType(businessLogData.getObjectType())
				.withAdditionalData(businessLogData.getAdditionalData());
		businessLogger.log(businessLogEvent);
	}

	private String extractAuthorities(CurrentUser currentUser) {
		return currentUser != null ? currentUser.getAuthorities().toString() : null;
	}
}
