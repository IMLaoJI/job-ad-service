package ch.admin.seco.jobs.services.jobadservice.infrastructure.businesslogging;

import ch.admin.seco.alv.shared.logger.business.BusinessLogData;
import ch.admin.seco.jobs.services.jobadservice.application.BusinessLogger;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUser;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

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
		businessLogData.withAuthorities(extractAuthorities(currentUserContext.getCurrentUser()));
		businessLogger.log(businessLogData);
	}

	private String extractAuthorities(CurrentUser currentUser) {
		return currentUser != null ? String.join(", ", currentUser.getAuthorities()) : Strings.EMPTY;
	}
}
