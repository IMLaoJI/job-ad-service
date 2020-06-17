package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.RejectionDto;

import java.time.LocalDate;

import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationServiceForAvamTest.STELLENNUMMER_AVAM;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;

public class RejectionDtoTestFixture {
    public static RejectionDto testRejectionDto() {
        return new RejectionDto()
                .setStellennummerEgov(job01.id().getValue())
                .setStellennummerAvam(STELLENNUMMER_AVAM)
                .setDate(LocalDate.of(2018, 1, 1))
                .setCode("code")
                .setReason("reason")
                .setJobCenterCode("14711")
                .setJobCenterUserId("jobcenterid");
    }
}
