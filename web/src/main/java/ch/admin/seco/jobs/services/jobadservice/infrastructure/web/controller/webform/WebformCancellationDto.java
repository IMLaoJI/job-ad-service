package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.webform;

import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.CancellationCode;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;

import java.time.LocalDate;

public class WebformCancellationDto {

    private CancellationCode code;

    private final SourceSystem sourceSystem = SourceSystem.JOBROOM;

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

    public WebformCancellationDto setCode(CancellationCode code) {
        this.code = code;
        return this;
    }

}
