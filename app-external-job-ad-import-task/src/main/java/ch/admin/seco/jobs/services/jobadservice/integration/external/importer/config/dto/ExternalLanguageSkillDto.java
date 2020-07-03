package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config.dto;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LanguageSkillDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.LanguageLevel;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.SupportedLanguageIsoCode;

import javax.validation.constraints.NotBlank;

public class ExternalLanguageSkillDto {

    @NotBlank
    @SupportedLanguageIsoCode
    private String languageIsoCode;

    private LanguageLevel spokenLevel;

    private LanguageLevel writtenLevel;

    public ExternalLanguageSkillDto() {
        // For reflection libs
    }

    LanguageSkillDto toLanguageSkillDto() {
        return new LanguageSkillDto()
                .setLanguageIsoCode(this.languageIsoCode)
                .setSpokenLevel(this.spokenLevel)
                .setWrittenLevel(this.writtenLevel);
    }

    public String getLanguageIsoCode() {
        return languageIsoCode;
    }

    public ExternalLanguageSkillDto setLanguageIsoCode(String languageIsoCode) {
        this.languageIsoCode = languageIsoCode;
        return this;
    }

    public LanguageLevel getSpokenLevel() {
        return spokenLevel;
    }

    public ExternalLanguageSkillDto setSpokenLevel(LanguageLevel spokenLevel) {
        this.spokenLevel = spokenLevel;
        return this;
    }

    public LanguageLevel getWrittenLevel() {
        return writtenLevel;
    }

    public ExternalLanguageSkillDto setWrittenLevel(LanguageLevel writtenLevel) {
        this.writtenLevel = writtenLevel;
        return this;
    }
}
