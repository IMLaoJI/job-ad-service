package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.LanguageLevel;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.SupportedLanguageIsoCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ApiLanguageSkillDto {

    @NotBlank
    @SupportedLanguageIsoCode
    private String languageIsoCode;

    @NotNull
    private LanguageLevel spokenLevel;

    @NotNull
    private LanguageLevel writtenLevel;

    public String getLanguageIsoCode() {
        return languageIsoCode;
    }

    public ApiLanguageSkillDto setLanguageIsoCode(String languageIsoCode) {
        this.languageIsoCode = languageIsoCode;
        return this;
    }

    public LanguageLevel getSpokenLevel() {
        return spokenLevel;
    }

    public ApiLanguageSkillDto setSpokenLevel(LanguageLevel spokenLevel) {
        this.spokenLevel = spokenLevel;
        return this;
    }

    public LanguageLevel getWrittenLevel() {
        return writtenLevel;
    }

    public ApiLanguageSkillDto setWrittenLevel(LanguageLevel writtenLevel) {
        this.writtenLevel = writtenLevel;
        return this;
    }
}
