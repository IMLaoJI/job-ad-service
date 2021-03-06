package ch.admin.seco.jobs.services.jobadservice.application;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole(T(ch.admin.seco.jobs.services.jobadservice.application.security.Role).SYSADMIN.value)")
public @interface IsSysAdmin {
}
