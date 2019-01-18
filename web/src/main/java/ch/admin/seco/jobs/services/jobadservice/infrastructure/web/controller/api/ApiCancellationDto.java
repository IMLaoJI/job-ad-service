package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.CancellationCode;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;

import java.time.LocalDate;

public class ApiCancellationDto {

    private CancellationCode code;

    private final SourceSystem sourceSystem = SourceSystem.API;

    private final LocalDate cancellationDate = TimeMachine.now().toLocalDate();

    public CancellationCode getCode() {
        return code;
    }

    public SourceSystem getSourceSystem() {
        return sourceSystem;
    }

    public LocalDate getCancellationDate() {
        return cancellationDate;
    }

    public ApiCancellationDto setCode(CancellationCode code) {
        this.code = code;
        return this;
    }

}
