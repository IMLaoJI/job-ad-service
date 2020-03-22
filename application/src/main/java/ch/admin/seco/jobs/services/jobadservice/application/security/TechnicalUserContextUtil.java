package ch.admin.seco.jobs.services.jobadservice.application.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

final class TechnicalUserContextUtil {

	private static final String DEFAULT_ROLE = Role.SYSADMIN.getValue();

	private static final String TECH_USER_ID = "tech-user-id";

	private static final String TECH_USER_NAME = "Tech-User";

	private static final String TECH_USER_EMAIL = "techuser@example.com";

	private static final String TECH_USER_EXT_ID = "tech-user-ext-id";

	private TechnicalUserContextUtil() {
	}

	static void initContext() {
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(DEFAULT_ROLE);
		securityContext.setAuthentication(prepareAuthentication(authorities));
		SecurityContextHolder.setContext(securityContext);
	}

	static void clearContext() {
		SecurityContextHolder.clearContext();
	}

	private static UsernamePasswordAuthenticationToken prepareAuthentication(List<GrantedAuthority> authorities) {
		return new UsernamePasswordAuthenticationToken(prepareUser(authorities), null, authorities);
	}

	private static UserDetailsToCurrentUserAdapter prepareUser(List<GrantedAuthority> authorities) {
		CurrentUser currentUser = new CurrentUser(
				TECH_USER_ID,
				TECH_USER_EXT_ID,
				null,
				TECH_USER_NAME,
				TECH_USER_NAME,
				TECH_USER_EMAIL,
				extractAuthorities(authorities)
		);
		return new UserDetailsToCurrentUserAdapter(
				TECH_USER_NAME,
				"N/A",
				currentUser,
				authorities
		);
	}

	private static Set<String> extractAuthorities(List<GrantedAuthority> authorities) {
		return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
	}

}
