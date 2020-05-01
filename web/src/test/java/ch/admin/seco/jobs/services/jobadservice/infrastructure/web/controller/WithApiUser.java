package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.application.security.Role;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockCustomUser(
        userId = WithApiUser.USER_ID,
        userExtId = WithApiUser.USER_EXT_ID,
        roles = Role.API
)
public @interface WithApiUser {
    String USER_ID = "api-user-id";
    String USER_EXT_ID = "api-user-extId";
}
