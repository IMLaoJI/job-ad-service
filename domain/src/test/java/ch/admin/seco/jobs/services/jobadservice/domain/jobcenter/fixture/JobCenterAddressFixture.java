package ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenterAddress;

public class JobCenterAddressFixture {
    public static JobCenterAddress testJobCenterAddress(String name) {
        return JobCenterAddress.builder()
                .setName(name)
                .setCity("jobCenter-city")
                .setStreet("jobCenter-street")
                .setHouseNumber("100")
                .setZipCode("3000")
                .build();

    }
}
