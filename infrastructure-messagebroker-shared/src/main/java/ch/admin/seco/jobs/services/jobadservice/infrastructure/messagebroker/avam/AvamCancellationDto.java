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

    @NotBlank
    private String jobDescriptionTitle;

    @NotNull
    private LocalDate cancellationDate;

    @NotNull
    private CancellationCode cancellationCode;

    @NotNull
    private SourceSystem sourceSystem;

    @NotBlank
    private String contactEmail;

    @NotBlank
    private String jobCenterCode;

    public String getStellennummerEgov() {
        return stellennummerEgov;
    }

    public String getStellennummerAvam() {
        return stellennummerAvam;
    }

    public String getJobDescriptionTitle() {
        return jobDescriptionTitle;
    }

    public LocalDate getCancellationDate() {
        return cancellationDate;
    }

    public CancellationCode getCancellationCode() {
        return cancellationCode;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getJobCenterCode() {
        return jobCenterCode;
    }

    public SourceSystem getSourceSystem() {
        return sourceSystem;
    }

    public AvamCancellationDto setStellennummerEgov(String stellennummerEgov) {
        this.stellennummerEgov = stellennummerEgov;
        return this;
    }

    public AvamCancellationDto setStellennummerAvam(String stellennummerAvam) {
        this.stellennummerAvam = stellennummerAvam;
        return this;
    }

    public AvamCancellationDto setJobDescriptionTitle(String jobDescriptionTitle) {
        this.jobDescriptionTitle = jobDescriptionTitle;
        return this;
    }

    public AvamCancellationDto setCancellationDate(LocalDate cancellationDate) {
        this.cancellationDate = cancellationDate;
        return this;
    }

    public AvamCancellationDto setCancellationCode(CancellationCode cancellationCode) {
        this.cancellationCode = cancellationCode;
        return this;
    }

    public AvamCancellationDto setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
        return this;
    }

    public AvamCancellationDto setJobCenterCode(String jobCenterCode) {
        this.jobCenterCode = jobCenterCode;
        return this;
    }

    public AvamCancellationDto setSourceSystem(SourceSystem sourceSystem) {
        this.sourceSystem = sourceSystem;
        return this;
    }

    protected static CancellationDto toDto(AvamCancellationDto avamCancellationDto) {
        return new CancellationDto()
                .setStellennummerEgov(avamCancellationDto.getStellennummerEgov())
                .setStellennummerAvam(avamCancellationDto.getStellennummerAvam())
                .setCode(avamCancellationDto.getCancellationCode())
                .setDate(avamCancellationDto.getCancellationDate())
                .setSourceSystem(avamCancellationDto.getSourceSystem());
    }

}
