package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.CancellationCode;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;

import java.time.LocalDate;

public class CancellationDto {

    private String stellennummerEgov;

    private String stellennummerAvam;

    //FIXME refactor to cancellationdate
    private LocalDate date;

    //FIXME refactor to cancellationcode
    private CancellationCode code;

    private SourceSystem sourceSystem;


    public String getStellennummerEgov() {
        return stellennummerEgov;
    }

    public String getStellennummerAvam() {
        return stellennummerAvam;
    }

    public LocalDate getDate() {
        return date;
    }

    public CancellationCode getCode() {
        return code;
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

    public CancellationDto setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public CancellationDto setCode(CancellationCode code) {
        this.code = code;
        return this;
    }

    public CancellationDto setSourceSystem(SourceSystem sourceSystem) {
        this.sourceSystem = sourceSystem;
        return this;
    }
}
