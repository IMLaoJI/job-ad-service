package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.springframework.util.StringUtils.hasText;

public class AtLeastOneApplyChannelRequiredValidator implements ConstraintValidator<AtLeastOneApplyChannelRequired, ApiApplyChannelDto> {

    @Override
    public void initialize(AtLeastOneApplyChannelRequired constraintAnnotation) {

    }

    @Override
    public boolean isValid(ApiApplyChannelDto apiApplyChannelDto, ConstraintValidatorContext context) {
        return  (apiApplyChannelDto.getMailAddress() != null)
                || hasText(apiApplyChannelDto.getEmailAddress())
                || hasText(apiApplyChannelDto.getPhoneNumber())
                || hasText(apiApplyChannelDto.getFormUrl());
    }
}
