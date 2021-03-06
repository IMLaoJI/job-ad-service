package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.application.security.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockCustomUser(
        userId = WithJobSeeker.USER_ID,
        userExtId = WithJobSeeker.USER_EXT_ID,
        roles = Role.JOBSEEKER_CLIENT
)
@WithSecurityContext(factory = WithJobSeekerSecurityContextFactory.class)
public @interface WithJobSeeker {
    String USER_ID = "job-seeker-1";
    String USER_EXT_ID = "ext-1";
}
