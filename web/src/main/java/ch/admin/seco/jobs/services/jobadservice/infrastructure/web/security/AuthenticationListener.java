package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.security;

import ch.admin.seco.jobs.services.jobadservice.domain.apiuser.ApiUser;
import ch.admin.seco.jobs.services.jobadservice.domain.apiuser.ApiUserId;
import ch.admin.seco.jobs.services.jobadservice.domain.apiuser.ApiUserRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.config.JobAdServiceSecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class AuthenticationListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private static Logger LOG = LoggerFactory.getLogger(AuthenticationListener.class);

    private final ApiUserRepository apiUserRepository;

    private final JobAdServiceSecurityProperties jobAdServiceSecurityProperties;

    public AuthenticationListener(ApiUserRepository apiUserRepository, JobAdServiceSecurityProperties jobAdServiceSecurityProperties) {
        this.apiUserRepository = apiUserRepository;
        this.jobAdServiceSecurityProperties = jobAdServiceSecurityProperties;
    }

    @Override
    @Transactional
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            onAuthenticationFailureBadCredentialsEvent((AuthenticationFailureBadCredentialsEvent) event);
        } else if (event instanceof AuthenticationSuccessEvent) {
            onAuthenticationSuccessEvent((AuthenticationSuccessEvent) event);
        }
    }

    private void onAuthenticationFailureBadCredentialsEvent(AuthenticationFailureBadCredentialsEvent event) {
        extractApiUser(event)
                .ifPresent(apiUser -> apiUser.invalidLoginAttempt(jobAdServiceSecurityProperties.getApiUserMaxLoginAttempts()));
    }

    private void onAuthenticationSuccessEvent(AuthenticationSuccessEvent event) {
        extractApiUser(event)
                .ifPresent(ApiUser::resetCountLoginFailure);
    }

    private Optional<ApiUser> extractApiUser(AbstractAuthenticationEvent event) {
        Authentication authentication = event.getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsToCurrentUserAdapter) {
            String userId = ((UserDetailsToCurrentUserAdapter) authentication.getPrincipal())
                    .getCurrentUser().getUserId();
            return apiUserRepository.findById(new ApiUserId(userId));
        }
        return Optional.empty();
    }
}
