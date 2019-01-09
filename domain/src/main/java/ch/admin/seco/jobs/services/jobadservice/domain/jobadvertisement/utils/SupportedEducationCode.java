package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SupportedEducationCodeValidator.class)
public @interface SupportedEducationCode {

    String message() default "{education.code.unsupported.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
