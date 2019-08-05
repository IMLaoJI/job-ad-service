package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneApplyChannelRequiredValidator.class)
@Documented
public @interface AtLeastOneApplyChannelRequired {
    String message() default "Either mail, email, form url or phone must be entered.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
