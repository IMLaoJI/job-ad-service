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
	
	private static final String ANONYMOUS_USER = "anonymousUser";

	private static final String USER_LOGIN_ID_KEY = "userLoginId";

	private final CurrentUserContext currentUserContext;

	private final ch.admin.seco.alv.shared.logger.business.BusinessLogger businessLogger;

	public LogstashBusinessLogger(CurrentUserContext currentUserContext, ch.admin.seco.alv.shared.logger.business.BusinessLogger businessLogger) {
		this.currentUserContext = currentUserContext;
		this.businessLogger = businessLogger;
	}

	@Override
	public void log(BusinessLogEvent businessLogEvent) {
		CurrentUser currentUser = currentUserContext.getCurrentUser();
		BusinessLogData businessLogData = new BusinessLogData(businessLogEvent.getEventType().getTypeName())
				.withObjectId(businessLogEvent.getObjectId())
				.withAuthorities(extractAuthorities(currentUser))
				.withObjectType(businessLogEvent.getObjectType().getTypeName())
				.withAdditionalData(businessLogEvent.getAdditionalData())
				.withAdditionalData(USER_LOGIN_ID_KEY, getCurrentUserId(currentUser));
		businessLogger.log(businessLogData);
	}
	
	private String getCurrentUserId(CurrentUser currentUser) {
		return currentUser != null ? currentUser.getUserId() :  ANONYMOUS_USER;
	}
	
	private String extractAuthorities(CurrentUser currentUser) {
		return currentUser != null ? String.join(" ", currentUser.getAuthorities()) : Strings.EMPTY;
	}
}
