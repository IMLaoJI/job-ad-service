package ch.admin.seco.jobs.services.jobadservice.infrastructure.scheduler.actuator.health;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskHealth {

	String value();

}
