package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkForm;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.Set;

public class ApiEmploymentDto {

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean shortEmployment;

    private boolean immediately;

    private boolean permanent;

    @Min(10)
    @Max(100)
    private int workloadPercentageMin;

    @Min(10)
    @Max(100)
    private int workloadPercentageMax;

    private Set<WorkForm> workForms;

    public LocalDate getStartDate() {
        return startDate;
    }

    public ApiEmploymentDto setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ApiEmploymentDto setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public boolean isShortEmployment() {
        return shortEmployment;
    }

    public ApiEmploymentDto setShortEmployment(boolean shortEmployment) {
        this.shortEmployment = shortEmployment;
        return this;
    }

    public boolean isImmediately() {
        return immediately;
    }

    public ApiEmploymentDto setImmediately(boolean immediately) {
        this.immediately = immediately;
        return this;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public ApiEmploymentDto setPermanent(boolean permanent) {
        this.permanent = permanent;
        return this;
    }

    public int getWorkloadPercentageMin() {
        return workloadPercentageMin;
    }

    public ApiEmploymentDto setWorkloadPercentageMin(int workloadPercentageMin) {
        this.workloadPercentageMin = workloadPercentageMin;
        return this;
    }

    public int getWorkloadPercentageMax() {
        return workloadPercentageMax;
    }

    public ApiEmploymentDto setWorkloadPercentageMax(int workloadPercentageMax) {
        this.workloadPercentageMax = workloadPercentageMax;
        return this;
    }

    public Set<WorkForm> getWorkForms() {
        return workForms;
    }

    public ApiEmploymentDto setWorkForms(Set<WorkForm> workForms) {
        this.workForms = workForms;
        return this;
    }
}
