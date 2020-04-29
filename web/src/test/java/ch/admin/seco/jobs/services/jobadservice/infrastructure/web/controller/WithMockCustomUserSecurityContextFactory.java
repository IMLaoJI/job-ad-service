package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        CurrentUser currentUser = this.getCurrentUser(customUser.userId(), customUser.userExtId(),customUser.companyId());
        String[] roles = Arrays.stream(customUser.roles())
                .map(Role::getValue)
                .toArray(size -> new String[customUser.roles().length]);
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

    private CurrentUser getCurrentUser(String userId, String userExtId, String companyId) {
        return new CurrentUser(
                userId,
                userExtId,
                companyId,
                "Junit",
                "Junit",
                "junit@example.com",
                Collections.emptyList()
        );
    }

}
