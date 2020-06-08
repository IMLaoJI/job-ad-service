package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.config;

import ch.admin.seco.alv.shared.jwt.JWTFilterConfigurer;
import ch.admin.seco.alv.shared.jwt.TokenToAuthenticationConverter;
import ch.admin.seco.jobs.services.jobadservice.application.security.Role;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@Configuration
@Import(SecurityProblemSupport.class)
@Order(SecurityProperties.BASIC_AUTH_ORDER)
@EnableWebSecurity
public class MicroserviceSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProblemSupport problemSupport;

    private final TokenToAuthenticationConverter tokenToAuthenticationConverter;

    public MicroserviceSecurityConfig(SecurityProblemSupport problemSupport, TokenToAuthenticationConverter tokenToAuthenticationConverter) {
        this.problemSupport = problemSupport;
        this.tokenToAuthenticationConverter = tokenToAuthenticationConverter;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/swagger-ui.html");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
                .and()
                .headers()
                .frameOptions()
                .disable()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .anonymous()
                .and()
                .authorizeRequests()
                .antMatchers("/api/complaint").permitAll()
                .antMatchers("/api/searchProfiles/jobalert/_action/unsubscribe/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/jobAdvertisements/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/jobAdvertisements", "/api/jobAdvertisements/_search", "/api/jobAdvertisements/_count").permitAll()
                .antMatchers(HttpMethod.PATCH, "/api/jobAdvertisements/{id}/cancel").permitAll()
                .antMatchers("/api/apiUsers/**").hasAuthority(Role.SYSADMIN.getValue())
                .antMatchers("/api/**").authenticated()
                .antMatchers("/management/info").permitAll()
                .antMatchers("/management/health").permitAll()
                .antMatchers("/management/**").hasAuthority(Role.ADMIN.getValue())
                .antMatchers("/swagger-ui.html").permitAll()
                .and()
                .apply(jwtFilterConfigurer());
        // @formatter:on
    }

    private JWTFilterConfigurer jwtFilterConfigurer() {
        return new JWTFilterConfigurer(this.tokenToAuthenticationConverter);
    }
}
