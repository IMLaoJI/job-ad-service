package ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenterAddress;

public class JobCenterAddressFixture {
    public static JobCenterAddress testJobCenterAddress(String name) {
        return new JobCenterAddress(
                name,
                "jobCenter-city",
                "jobCenter-street",
                "100",
                "3000"
        );
    }
}
