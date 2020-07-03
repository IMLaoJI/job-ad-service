package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class RejectionDto {

    @NotNull
    private String stellennummerEgov;

    private String stellennummerAvam;

    @NotNull
    private LocalDate date;

    @NotNull
    private String code;

    private String reason;

    private String jobCenterCode;

    private String jobCenterUserId;

    public String getStellennummerEgov() {
        return stellennummerEgov;
    }

    public RejectionDto setStellennummerEgov(String stellennummerEgov) {
        this.stellennummerEgov = stellennummerEgov;
        return this;
    }

    public String getStellennummerAvam() {
        return stellennummerAvam;
    }

    public RejectionDto setStellennummerAvam(String stellennummerAvam) {
        this.stellennummerAvam = stellennummerAvam;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public RejectionDto setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public String getCode() {
        return code;
    }

    public RejectionDto setCode(String code) {
        this.code = code;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public RejectionDto setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public String getJobCenterCode() {
        return jobCenterCode;
    }

    public RejectionDto setJobCenterCode(String jobCenterCode) {
        this.jobCenterCode = jobCenterCode;
        return this;
    }

    public String getJobCenterUserId() {
        return jobCenterUserId;
    }

    public RejectionDto setJobCenterUserId(String jobCenterUserId) {
        this.jobCenterUserId = jobCenterUserId;
        return this;
    }
}
