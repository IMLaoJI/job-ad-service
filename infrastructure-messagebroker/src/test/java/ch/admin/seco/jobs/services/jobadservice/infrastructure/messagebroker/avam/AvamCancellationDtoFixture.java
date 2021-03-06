package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.CancellationCode;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;

import java.time.LocalDate;


public class AvamCancellationDtoFixture {

    public static AvamCancellationDto testAvamCancellationDto() {
        return new AvamCancellationDto()
                .setStellennummerAvam("stellennummeravam")
                .setStellennummerEgov("stellennummeregov")
                .setJobDescriptionTitle("title")
                .setCancellationDate(LocalDate.of(2018,01,17))
                .setCancellationCode(CancellationCode.OCCUPIED_OTHER)
                .setContactEmail("test@example.com")
                .setSourceSystem(SourceSystem.RAV)
                .setJobCenterCode("AG10");
    }
}
