package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Occupation.Builder;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkExperience.LESS_THAN_1_YEAR;

public class OccupationFixture {
    public static Builder testOccupation() {
        return testOccupationEmpty()
        .setAvamOccupationCode("9999")
        .setBfsCode("bfsCode")
        .setLabel("label")
        .setWorkExperience(LESS_THAN_1_YEAR)
        .setEducationCode("educationCode");
    }

    public static Builder testOccupationEmpty() {
        return new Builder();
    }
}
