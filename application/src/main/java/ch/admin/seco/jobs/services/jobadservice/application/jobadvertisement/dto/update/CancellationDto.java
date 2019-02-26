package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.CancellationCode;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;

import java.time.LocalDate;

public class CancellationDto {

    private String stellennummerEgov;

    private String stellennummerAvam;

    private LocalDate cancellationDate;

    private CancellationCode cancellationCode;

    private SourceSystem cancelledBy;

    public String getStellennummerEgov() {
        return stellennummerEgov;
    }

    public CancellationDto setStellennummerEgov(String stellennummerEgov) {
        this.stellennummerEgov = stellennummerEgov;
        return this;
    }

    public String getStellennummerAvam() {
        return stellennummerAvam;
    }

    public CancellationDto setStellennummerAvam(String stellennummerAvam) {
        this.stellennummerAvam = stellennummerAvam;
        return this;
    }

    public LocalDate getCancellationDate() {
        return cancellationDate;
    }

    public CancellationDto setCancellationDate(LocalDate cancellationDate) {
        this.cancellationDate = cancellationDate;
        return this;
    }

    public CancellationCode getCancellationCode() {
        return cancellationCode;
    }

    public CancellationDto setCancellationCode(CancellationCode cancellationCode) {
        this.cancellationCode = cancellationCode;
        return this;
    }

    public SourceSystem getCancelledBy() {
        return cancelledBy;
    }

    public CancellationDto setCancelledBy(SourceSystem cancelledBy) {
        this.cancelledBy = cancelledBy;
        return this;
    }
}
