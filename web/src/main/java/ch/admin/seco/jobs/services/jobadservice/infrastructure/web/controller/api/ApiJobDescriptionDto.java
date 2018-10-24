package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.LanguageIsoCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ApiJobDescriptionDto {

    @NotBlank
    @LanguageIsoCode
    private String languageIsoCode;

    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 12000)
    private String description;

    public String getLanguageIsoCode() {
        return languageIsoCode;
    }

    public ApiJobDescriptionDto setLanguageIsoCode(String languageIsoCode) {
        this.languageIsoCode = languageIsoCode;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ApiJobDescriptionDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ApiJobDescriptionDto setDescription(String description) {
        this.description = description;
        return this;
    }
}
