package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.CancellationCode;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;

import javax.validation.Valid;
import java.time.LocalDate;

public class ApiJobAdvertisementDto {

    private String id;

    private JobAdvertisementStatus status;

    private SourceSystem sourceSystem;

    private String externalReference;

    private String stellennummerEgov;

    private String stellennummerAvam;

    private String fingerprint;

    private boolean reportingObligation;

    private LocalDate reportingObligationEndDate;

    private boolean reportToAvam;

    private String jobCenterCode;

    private LocalDate approvalDate;

    private LocalDate rejectionDate;

    private String rejectionCode;

    private String rejectionReason;

    private LocalDate cancellationDate;

    private CancellationCode cancellationCode;

    @Valid
    private ApiJobContentDto jobContent;

    @Valid
    private ApiPublicationDto publication;

    public String getId() {
        return id;
    }

    public ApiJobAdvertisementDto setId(String id) {
        this.id = id;
        return this;
    }

    public JobAdvertisementStatus getStatus() {
        return status;
    }

    public ApiJobAdvertisementDto setStatus(JobAdvertisementStatus status) {
        this.status = status;
        return this;
    }

    public SourceSystem getSourceSystem() {
        return sourceSystem;
    }

    public ApiJobAdvertisementDto setSourceSystem(SourceSystem sourceSystem) {
        this.sourceSystem = sourceSystem;
        return this;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public ApiJobAdvertisementDto setExternalReference(String externalReference) {
        this.externalReference = externalReference;
        return this;
    }

    public String getStellennummerEgov() {
        return stellennummerEgov;
    }

    public ApiJobAdvertisementDto setStellennummerEgov(String stellennummerEgov) {
        this.stellennummerEgov = stellennummerEgov;
        return this;
    }

    public String getStellennummerAvam() {
        return stellennummerAvam;
    }

    public ApiJobAdvertisementDto setStellennummerAvam(String stellennummerAvam) {
        this.stellennummerAvam = stellennummerAvam;
        return this;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public ApiJobAdvertisementDto setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
        return this;
    }

    public boolean isReportingObligation() {
        return reportingObligation;
    }

    public ApiJobAdvertisementDto setReportingObligation(boolean reportingObligation) {
        this.reportingObligation = reportingObligation;
        return this;
    }

    public LocalDate getReportingObligationEndDate() {
        return reportingObligationEndDate;
    }

    public ApiJobAdvertisementDto setReportingObligationEndDate(LocalDate reportingObligationEndDate) {
        this.reportingObligationEndDate = reportingObligationEndDate;
        return this;
    }

    public boolean isReportToAvam() {
        return reportToAvam;
    }

    public ApiJobAdvertisementDto setReportToAvam(boolean reportToAvam) {
        this.reportToAvam = reportToAvam;
        return this;
    }

    public String getJobCenterCode() {
        return jobCenterCode;
    }

    public ApiJobAdvertisementDto setJobCenterCode(String jobCenterCode) {
        this.jobCenterCode = jobCenterCode;
        return this;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public ApiJobAdvertisementDto setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
        return this;
    }

    public LocalDate getRejectionDate() {
        return rejectionDate;
    }

    public ApiJobAdvertisementDto setRejectionDate(LocalDate rejectionDate) {
        this.rejectionDate = rejectionDate;
        return this;
    }

    public String getRejectionCode() {
        return rejectionCode;
    }

    public ApiJobAdvertisementDto setRejectionCode(String rejectionCode) {
        this.rejectionCode = rejectionCode;
        return this;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public ApiJobAdvertisementDto setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
        return this;
    }

    public LocalDate getCancellationDate() {
        return cancellationDate;
    }

    public ApiJobAdvertisementDto setCancellationDate(LocalDate cancellationDate) {
        this.cancellationDate = cancellationDate;
        return this;
    }

    public CancellationCode getCancellationCode() {
        return cancellationCode;
    }

    public ApiJobAdvertisementDto setCancellationCode(CancellationCode cancellationCode) {
        this.cancellationCode = cancellationCode;
        return this;
    }

    public ApiJobContentDto getJobContent() {
        return jobContent;
    }

    public ApiJobAdvertisementDto setJobContent(ApiJobContentDto jobContent) {
        this.jobContent = jobContent;
        return this;
    }

    public ApiPublicationDto getPublication() {
        return publication;
    }

    public ApiJobAdvertisementDto setPublication(ApiPublicationDto publication) {
        this.publication = publication;
        return this;
    }
}
