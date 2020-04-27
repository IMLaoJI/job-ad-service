package web.controller;

import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUser;
import ch.admin.seco.jobs.services.jobadservice.application.security.Role;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.security.UserDetailsToCurrentUserAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;
import java.util.List;

public class WithJobSeekerSecurityContextFactory implements WithSecurityContextFactory<WithJobSeeker> {

    @Override
    public SecurityContext createSecurityContext(WithJobSeeker jobSeeker) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        CurrentUser currentUser = getCurrentUser();
        String[] roles = {Role.JOBSEEKER_CLIENT.getValue()};
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(roles);
        UserDetailsToCurrentUserAdapter principal = new UserDetailsToCurrentUserAdapter(
                currentUser.getUserId(),
                "N/A",
                currentUser,
                authorityList
        );
        Authentication auth =
                new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }

    private CurrentUser getCurrentUser() {
        return new CurrentUser(
                WithJobSeeker.USER_ID,
                WithJobSeeker.USER_EXT_ID,
                "",
                "Junit",
                "Junit",
                "junit@example.com",
                Collections.singleton(Role.JOBSEEKER_CLIENT.getValue())
        );
    }

}
