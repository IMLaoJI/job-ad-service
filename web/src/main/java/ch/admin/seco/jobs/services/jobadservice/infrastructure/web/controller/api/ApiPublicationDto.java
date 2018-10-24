package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class ApiPublicationDto {

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    private boolean euresDisplay;

    private boolean euresAnonymous;

    private boolean publicDisplay;

    private boolean restrictedDisplay;

    private boolean companyAnonymous;

    public LocalDate getStartDate() {
        return startDate;
    }

    public ApiPublicationDto setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ApiPublicationDto setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public boolean isEuresDisplay() {
        return euresDisplay;
    }

    public ApiPublicationDto setEuresDisplay(boolean euresDisplay) {
        this.euresDisplay = euresDisplay;
        return this;
    }

    public boolean isEuresAnonymous() {
        return euresAnonymous;
    }

    public ApiPublicationDto setEuresAnonymous(boolean euresAnonymous) {
        this.euresAnonymous = euresAnonymous;
        return this;
    }

    public boolean isPublicDisplay() {
        return publicDisplay;
    }

    public ApiPublicationDto setPublicDisplay(boolean publicDisplay) {
        this.publicDisplay = publicDisplay;
        return this;
    }

    public boolean isRestrictedDisplay() {
        return restrictedDisplay;
    }

    public ApiPublicationDto setRestrictedDisplay(boolean restrictedDisplay) {
        this.restrictedDisplay = restrictedDisplay;
        return this;
    }

    public boolean isCompanyAnonymous() {
        return companyAnonymous;
    }

    public ApiPublicationDto setCompanyAnonymous(boolean companyAnonymous) {
        this.companyAnonymous = companyAnonymous;
        return this;
    }
}
