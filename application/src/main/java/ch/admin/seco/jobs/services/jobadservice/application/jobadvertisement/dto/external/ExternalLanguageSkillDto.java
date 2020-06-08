package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external;

import javax.validation.constraints.NotBlank;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LanguageSkillDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.LanguageLevel;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.SupportedLanguageIsoCode;

public class ExternalLanguageSkillDto {

    @NotBlank
    @SupportedLanguageIsoCode
    private String languageIsoCode;

    private LanguageLevel spokenLevel;

    private LanguageLevel writtenLevel;

    protected ExternalLanguageSkillDto() {
        // For reflection libs
    }

    public ExternalLanguageSkillDto(String languageIsoCode, LanguageLevel spokenLevel, LanguageLevel writtenLevel) {
        this.languageIsoCode = languageIsoCode;
        this.spokenLevel = spokenLevel;
        this.writtenLevel = writtenLevel;
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

    public void setLanguageIsoCode(String languageIsoCode) {
        this.languageIsoCode = languageIsoCode;
    }

    public LanguageLevel getSpokenLevel() {
        return spokenLevel;
    }

    public void setSpokenLevel(LanguageLevel spokenLevel) {
        this.spokenLevel = spokenLevel;
    }

    public LanguageLevel getWrittenLevel() {
        return writtenLevel;
    }

    public void setWrittenLevel(LanguageLevel writtenLevel) {
        this.writtenLevel = writtenLevel;
    }
}
