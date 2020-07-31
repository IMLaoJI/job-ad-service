package ch.admin.seco.jobs.services.jobadservice.application.common.logging;

import ch.admin.seco.alv.shared.logger.business.BusinessLogContext;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUser;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Primary
@Component
public class CustomBusinessLogContext implements BusinessLogContext {

    final CurrentUserContext currentUserContext;

    public CustomBusinessLogContext(CurrentUserContext currentUserContext) {
        this.currentUserContext = currentUserContext;
    }

    @Override
    public String getUserId() {
        CurrentUser currentUser = currentUserContext.getCurrentUser();
        return currentUser != null ? currentUser.getUserId() : ANONYMOUS_USER;
    }

    @Override
    public Set<String> getAuthorities() {
        CurrentUser currentUser = currentUserContext.getCurrentUser();
        return currentUser != null ? currentUser.getAuthorities() : Collections.emptySet();
    }
}
