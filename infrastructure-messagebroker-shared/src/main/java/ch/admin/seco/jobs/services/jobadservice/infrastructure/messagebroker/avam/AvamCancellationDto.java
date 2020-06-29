package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.CancellationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.CancellationCode;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class AvamCancellationDto {

    private String stellennummerEgov;

    @NotBlank
    private String stellennummerAvam;

    @NotNull
    private LocalDate cancellationDate;

    @NotNull
    private CancellationCode cancellationCode;

    @NotNull
    private SourceSystem cancelledBy;

    @NotBlank
    private String jobDescriptionTitle;

    @NotNull
    private SourceSystem sourceSystem;

    @NotBlank
    private String contactEmail;

    @NotBlank
    private String jobCenterCode;

    private String jobCenterUserId;

    public String getStellennummerEgov() {
        return stellennummerEgov;
    }

    public AvamCancellationDto setStellennummerEgov(String stellennummerEgov) {
        this.stellennummerEgov = stellennummerEgov;
        return this;
    }

    public String getStellennummerAvam() {
        return stellennummerAvam;
    }

    public AvamCancellationDto setStellennummerAvam(String stellennummerAvam) {
        this.stellennummerAvam = stellennummerAvam;
        return this;
    }

    public LocalDate getCancellationDate() {
        return cancellationDate;
    }

    public AvamCancellationDto setCancellationDate(LocalDate cancellationDate) {
        this.cancellationDate = cancellationDate;
        return this;
    }

    public CancellationCode getCancellationCode() {
        return cancellationCode;
    }

    public AvamCancellationDto setCancellationCode(CancellationCode cancellationCode) {
        this.cancellationCode = cancellationCode;
        return this;
    }

    public SourceSystem getCancelledBy() {
        return cancelledBy;
    }

    public AvamCancellationDto setCancelledBy(SourceSystem cancelledBy) {
        this.cancelledBy = cancelledBy;
        return this;
    }

    public String getJobDescriptionTitle() {
        return jobDescriptionTitle;
    }

    public AvamCancellationDto setJobDescriptionTitle(String jobDescriptionTitle) {
        this.jobDescriptionTitle = jobDescriptionTitle;
        return this;
    }

    public SourceSystem getSourceSystem() {
        return sourceSystem;
    }

    public AvamCancellationDto setSourceSystem(SourceSystem sourceSystem) {
        this.sourceSystem = sourceSystem;
        return this;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public AvamCancellationDto setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
        return this;
    }

    public String getJobCenterCode() {
        return jobCenterCode;
    }

    public AvamCancellationDto setJobCenterCode(String jobCenterCode) {
        this.jobCenterCode = jobCenterCode;
        return this;
    }

    public String getJobCenterUserId() {
        return jobCenterUserId;
    }

    public AvamCancellationDto setJobCenterUserId(String jobCenterUserId) {
        this.jobCenterUserId = jobCenterUserId;
        return this;
    }

    protected static CancellationDto toDto(AvamCancellationDto avamCancellationDto) {
        return new CancellationDto()
                .setStellennummerEgov(avamCancellationDto.getStellennummerEgov())
                .setStellennummerAvam(avamCancellationDto.getStellennummerAvam())
                .setCancellationCode(avamCancellationDto.getCancellationCode())
                .setCancellationDate(avamCancellationDto.getCancellationDate())
                .setCancelledBy(avamCancellationDto.getCancelledBy());
    }

}
