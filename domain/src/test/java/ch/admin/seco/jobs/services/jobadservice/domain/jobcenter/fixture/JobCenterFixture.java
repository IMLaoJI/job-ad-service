package ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.ContactDisplayStyle;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.fixture.JobCenterAddressFixture.testJobCenterAddress;

public class JobCenterFixture {
    public static JobCenter testJobCenter() {
        return JobCenter.builder()
                .setCode("jobCenter-code")
                .setEmail("jobCenter-email")
                .setPhone("jobCenter-phone")
                .setFax("jobCenter-fax")
                .setContactDisplayStyle(ContactDisplayStyle.POSTAL_ADDRESS_ONLY)
                .setAddress(testJobCenterAddress("jobCenter-name"))
                .build();
    }
}
