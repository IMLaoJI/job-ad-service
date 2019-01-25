package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.CancellationCode;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;

import java.time.LocalDate;

public class CancellationDto {

    private String stellennummerEgov;

    private String stellennummerAvam;

    private LocalDate cancellationDate;

    private CancellationCode cancellationCode;

    private SourceSystem sourceSystem;


    public String getStellennummerEgov() {
        return stellennummerEgov;
    }

    public String getStellennummerAvam() {
        return stellennummerAvam;
    }

    public LocalDate getCancellationDate() {
        return cancellationDate;
    }

    public CancellationCode getCancellationCode() {
        return cancellationCode;
    }

    public SourceSystem getSourceSystem() {
        return sourceSystem;
    }

    public CancellationDto setStellennummerEgov(String stellennummerEgov) {
        this.stellennummerEgov = stellennummerEgov;
        return this;
    }

    public CancellationDto setStellennummerAvam(String stellennummerAvam) {
        this.stellennummerAvam = stellennummerAvam;
        return this;
    }

    public CancellationDto setCancellationDate(LocalDate date) {
        this.cancellationDate = date;
        return this;
    }

    public CancellationDto setCancellationCode(CancellationCode cancellationCode) {
        this.cancellationCode = cancellationCode;
        return this;
    }

    public CancellationDto setSourceSystem(SourceSystem sourceSystem) {
        this.sourceSystem = sourceSystem;
        return this;
    }
}
