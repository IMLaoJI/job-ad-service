package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.config;

import ch.admin.seco.alv.shared.jwt.JWTUser;
import ch.admin.seco.alv.shared.jwt.JWTUserDetailsProvider;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUser;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.security.UserDetailsToCurrentUserAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class JWTConfiguration {

    @Bean
    JWTUserDetailsProvider jwtUserDetailsProvider() {
        return new JWTUserDetailsProvider() {
            @Override
            public UserDetails provide(JWTUser jwtUser) {
                List<GrantedAuthority> authorities = jwtUser.getAuthorities();
                CurrentUser currentUser = new CurrentUser(
                        jwtUser.getUserId(),
                        jwtUser.getUserExternalId(),
                        jwtUser.getCompanyId(),
                        jwtUser.getFirstName(),
                        jwtUser.getLastName(),
                        jwtUser.getEmail(),
                        extractAuthorities(authorities));
                return new UserDetailsToCurrentUserAdapter(
                        jwtUser.getUsername(),
                        "",
                        currentUser,
                        authorities
                );
            }
        };
    }

    private Set<String> extractAuthorities(List<GrantedAuthority> authorities) {
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

}
