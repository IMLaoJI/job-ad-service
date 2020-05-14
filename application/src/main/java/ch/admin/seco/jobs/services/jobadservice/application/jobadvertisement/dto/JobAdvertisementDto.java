package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JobAdvertisementDto {

    private String id;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

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

    private String jobCenterUserId;

    private LocalDate approvalDate;

    private LocalDate rejectionDate;

    private String rejectionCode;

    private String rejectionReason;

    private LocalDate cancellationDate;

    private CancellationCode cancellationCode;

    @Valid
    private JobContentDto jobContent;

    @Valid
    private PublicationDto publication;

    private OwnerDto owner;

    private  boolean reportAdvertisementLinkVisible;

    public JobAdvertisementDto() {
        // For reflection libs
    }

    public String getId() {
        return id;
    }

    public JobAdvertisementDto setId(String id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public JobAdvertisementDto setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public JobAdvertisementDto setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    public JobAdvertisementStatus getStatus() {
        return status;
    }

    public JobAdvertisementDto setStatus(JobAdvertisementStatus status) {
        this.status = status;
        return this;
    }

    public SourceSystem getSourceSystem() {
        return sourceSystem;
    }

    public JobAdvertisementDto setSourceSystem(SourceSystem sourceSystem) {
        this.sourceSystem = sourceSystem;
        return this;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public JobAdvertisementDto setExternalReference(String externalReference) {
        this.externalReference = externalReference;
        return this;
    }

    public String getStellennummerEgov() {
        return stellennummerEgov;
    }

    public JobAdvertisementDto setStellennummerEgov(String stellennummerEgov) {
        this.stellennummerEgov = stellennummerEgov;
        return this;
    }

    public String getStellennummerAvam() {
        return stellennummerAvam;
    }

    public JobAdvertisementDto setStellennummerAvam(String stellennummerAvam) {
        this.stellennummerAvam = stellennummerAvam;
        return this;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public JobAdvertisementDto setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
        return this;
    }

    public boolean isReportingObligation() {
        return reportingObligation;
    }

    public JobAdvertisementDto setReportingObligation(boolean reportingObligation) {
        this.reportingObligation = reportingObligation;
        return this;
    }

    public LocalDate getReportingObligationEndDate() {
        return reportingObligationEndDate;
    }

    public JobAdvertisementDto setReportingObligationEndDate(LocalDate reportingObligationEndDate) {
        this.reportingObligationEndDate = reportingObligationEndDate;
        return this;
    }

    public boolean isReportToAvam() {
        return reportToAvam;
    }

    public JobAdvertisementDto setReportToAvam(boolean reportToAvam) {
        this.reportToAvam = reportToAvam;
        return this;
    }

    public String getJobCenterCode() {
        return jobCenterCode;
    }

    public JobAdvertisementDto setJobCenterCode(String jobCenterCode) {
        this.jobCenterCode = jobCenterCode;
        return this;
    }

    public JobAdvertisementDto setJobCenterUserId(String jobCenterUserId) {
        this.jobCenterUserId = jobCenterUserId;
        return this;
    }

    public String getJobCenterUserId() {
        return jobCenterUserId;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public JobAdvertisementDto setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
        return this;
    }

    public LocalDate getRejectionDate() {
        return rejectionDate;
    }

    public JobAdvertisementDto setRejectionDate(LocalDate rejectionDate) {
        this.rejectionDate = rejectionDate;
        return this;
    }

    public String getRejectionCode() {
        return rejectionCode;
    }

    public JobAdvertisementDto setRejectionCode(String rejectionCode) {
        this.rejectionCode = rejectionCode;
        return this;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public JobAdvertisementDto setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
        return this;
    }

    public LocalDate getCancellationDate() {
        return cancellationDate;
    }

    public JobAdvertisementDto setCancellationDate(LocalDate cancellationDate) {
        this.cancellationDate = cancellationDate;
        return this;
    }

    public CancellationCode getCancellationCode() {
        return cancellationCode;
    }

    public JobAdvertisementDto setCancellationCode(CancellationCode cancellationCode) {
        this.cancellationCode = cancellationCode;
        return this;
    }

    public JobContentDto getJobContent() {
        return jobContent;
    }

    public JobAdvertisementDto setJobContent(JobContentDto jobContent) {
        this.jobContent = jobContent;
        return this;
    }

    public PublicationDto getPublication() {
        return publication;
    }

    public JobAdvertisementDto setPublication(PublicationDto publication) {
        this.publication = publication;
        return this;
    }

    public OwnerDto getOwner() {
        return owner;
    }

    public JobAdvertisementDto setOwner(OwnerDto owner) {
        this.owner = owner;
        return this;
    }

    public static JobAdvertisementDto toDto(JobAdvertisement jobAdvertisement) {
        JobAdvertisementDto jobAdvertisementDto = new JobAdvertisementDto()
                .setId(jobAdvertisement.getId().getValue())
                .setCreatedTime(jobAdvertisement.getCreatedTime())
                .setUpdatedTime(jobAdvertisement.getUpdatedTime())
                .setStatus(jobAdvertisement.getStatus())
                .setSourceSystem(jobAdvertisement.getSourceSystem())
                .setExternalReference(jobAdvertisement.getExternalReference())
                .setStellennummerEgov(jobAdvertisement.getStellennummerEgov())
                .setStellennummerAvam(jobAdvertisement.getStellennummerAvam())
                .setFingerprint(jobAdvertisement.getFingerprint())
                .setReportingObligation(jobAdvertisement.isReportingObligation())
                .setReportingObligationEndDate(jobAdvertisement.getReportingObligationEndDate())
                .setReportToAvam(jobAdvertisement.isReportToAvam())
                .setJobCenterCode(jobAdvertisement.getJobCenterCode())
                .setJobCenterUserId(jobAdvertisement.getJobCenterUserId())
                .setApprovalDate(jobAdvertisement.getApprovalDate())
                .setRejectionDate(jobAdvertisement.getRejectionDate())
                .setRejectionCode(jobAdvertisement.getRejectionCode())
                .setRejectionReason(jobAdvertisement.getRejectionReason())
                .setCancellationDate(jobAdvertisement.getCancellationDate())
                .setCancellationCode(jobAdvertisement.getCancellationCode())
                .setJobContent(JobContentDto.toDto(jobAdvertisement.getJobContent()))
                .setPublication(PublicationDto.toDto(jobAdvertisement.getPublication()));

        // Eager load data from ElementCollection
        Set<WorkForm> workForms = jobAdvertisement.getJobContent().getEmployment().getWorkForms() != null
                ? jobAdvertisement.getJobContent().getEmployment().getWorkForms()
                : Collections.emptySet();

        jobAdvertisementDto.getJobContent().getEmployment().setWorkForms(new HashSet<>(workForms));
        return jobAdvertisementDto;
    }

    public static JobAdvertisementDto toDtoWithOwner(JobAdvertisement jobAdvertisement) {
        return toDto(jobAdvertisement)
                .setOwner(OwnerDto.toDto(jobAdvertisement.getOwner()));
    }

    public boolean isReportAdvertisementLinkVisible() {
        return reportAdvertisementLinkVisible;
    }

    public void setReportAdvertisementLinkVisible(boolean reportAdvertisementLinkVisible) {
        this.reportAdvertisementLinkVisible = reportAdvertisementLinkVisible;
    }
}
