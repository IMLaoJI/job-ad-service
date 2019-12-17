package ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.ContactDisplayStyle;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenterUser;

import java.util.Optional;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.fixture.JobCenterAddressFixture.testJobCenterAddress;

public class JobCenterTestFixture {

    public static final String JOB_CENTER_CODE = "jobCenterCode";

    public static JobCenter testJobCenter(String name) {
        return new JobCenter(
                "jobCenterId",
                JOB_CENTER_CODE,
                "email",
                "phone",
                "jobCenterFax",
                ContactDisplayStyle.DETAILED,
                testJobCenterAddress(name)
        );
    }

    public static JobCenter testJobCenterWithUserDetail(String name) {
        return new JobCenter(
                "jobCenterId",
                JOB_CENTER_CODE,
                "email",
                "phone",
                "jobCenterFax",
                ContactDisplayStyle.JOB_CENTER_USER_CONTACT_DATA,
                testJobCenterAddress(name)
        );
    }

    public static Optional<JobCenterUser> createJobCenterUser() {
        return Optional.of(new JobCenterUser()
                .setAddress(testJobCenterAddress("Markus Meier"))
                .setCode("code")
                .setEmail("email")
                .setExternalId("14711")
                .setFirstName("Markus")
                .setLastName("Meier")
                .setPhone("phone"));
    }

}
