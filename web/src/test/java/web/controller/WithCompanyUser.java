package web.controller;

import ch.admin.seco.jobs.services.jobadservice.application.security.Role;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockCustomUser(
        userId = WithCompanyUser.USER_ID,
        userExtId = WithCompanyUser.USER_EXT_ID,
        companyId = WithCompanyUser.USER_COMPANY_ID,
        roles = Role.COMPANY
)
public @interface WithCompanyUser {
    String USER_ID = "company-user-id";
    String USER_EXT_ID = "company-user-extId";
    String USER_COMPANY_ID = "companyId";
}
