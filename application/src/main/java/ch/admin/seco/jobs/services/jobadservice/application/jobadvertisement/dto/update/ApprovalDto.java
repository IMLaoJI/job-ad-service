package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class ApprovalDto {

    @NotNull
    private String stellennummerEgov;

    @NotNull
    private String stellennummerAvam;

    @NotNull
    private LocalDate date;

    private boolean reportingObligation;

    private LocalDate reportingObligationEndDate;

    private UpdateJobAdvertisementFromAvamDto updateJobAdvertisement;

    private String jobCenterCode;

    private String jobCenterUserId;

    public String getStellennummerEgov() {
        return stellennummerEgov;
    }

    public ApprovalDto setStellennummerEgov(String stellennummerEgov) {
        this.stellennummerEgov = stellennummerEgov;
        return this;
    }

    public String getStellennummerAvam() {
        return stellennummerAvam;
    }

    public ApprovalDto setStellennummerAvam(String stellennummerAvam) {
        this.stellennummerAvam = stellennummerAvam;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public ApprovalDto setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public boolean isReportingObligation() {
        return reportingObligation;
    }

    public ApprovalDto setReportingObligation(boolean reportingObligation) {
        this.reportingObligation = reportingObligation;
        return this;
    }

    public LocalDate getReportingObligationEndDate() {
        return reportingObligationEndDate;
    }

    public ApprovalDto setReportingObligationEndDate(LocalDate reportingObligationEndDate) {
        this.reportingObligationEndDate = reportingObligationEndDate;
        return this;
    }

    public UpdateJobAdvertisementFromAvamDto getUpdateJobAdvertisement() {
        return updateJobAdvertisement;
    }

    public ApprovalDto setUpdateJobAdvertisement(UpdateJobAdvertisementFromAvamDto updateJobAdvertisement) {
        this.updateJobAdvertisement = updateJobAdvertisement;
        return this;
    }

    public String getJobCenterCode() {
        return jobCenterCode;
    }

    public ApprovalDto setJobCenterCode(String jobCenterCode) {
        this.jobCenterCode = jobCenterCode;
        return this;
    }

    public String getJobCenterUserId() {
        return jobCenterUserId;
    }

    public ApprovalDto setJobCenterUserId(String jobCenterUserId) {
        this.jobCenterUserId = jobCenterUserId;
        return this;
    }
}
