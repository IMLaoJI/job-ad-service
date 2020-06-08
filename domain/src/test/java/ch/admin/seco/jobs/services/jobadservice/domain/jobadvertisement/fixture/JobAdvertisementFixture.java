package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement.Builder;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.CancellationCode.OCCUPIED_JOBCENTER;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.CREATED;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem.JOBROOM;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.ContactFixture.testContact;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobContentFixture.testJobContent;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.OwnerFixture.testOwner;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.PublicationFixture.testPublication;
import static java.time.LocalDate.now;

public class JobAdvertisementFixture {

    private static Builder testJobAdvertisementEmpty() {
        return new Builder();
    }

    public static Builder testJobAdvertisement() {
        return testJobAdvertisementEmpty()
                .setId(job01.id())
                .setStatus(CREATED)
                .setSourceSystem(JOBROOM)
                .setExternalReference("externalReference")
                .setStellennummerEgov(job01.name())
                .setStellennummerAvam("12345678")
                .setFingerprint("fingerprint")
                .setReportingObligationEndDate(now().plusWeeks(4))
                .setJobCenterCode("12345")
                .setJobCenterUserId("14711")
                .setApprovalDate(now().plusWeeks(3))
                .setRejectionDate(now().plusWeeks(2))
                .setRejectionCode("1234")
                .setRejectionReason("rejectionReason")
                .setCancellationDate(now().plusWeeks(1))
                .setCancellationCode(OCCUPIED_JOBCENTER)
                .setJobContent(testJobContent().build())
                .setOwner(testOwner().build())
                .setContact(testContact().build())
                .setPublication(testPublication().build());
    }

    public static JobAdvertisement.Builder of(JobAdvertisementId jobAdvertisementId) {
        return testJobAdvertisement()
                .setId(jobAdvertisementId)
                .setOwner(OwnerFixture.of(jobAdvertisementId).build())
                .setJobContent(JobContentFixture.of(jobAdvertisementId).build())
                .setStellennummerEgov(jobAdvertisementId.getValue())
                .setContact(ContactFixture.of(jobAdvertisementId).build());
    }
}
