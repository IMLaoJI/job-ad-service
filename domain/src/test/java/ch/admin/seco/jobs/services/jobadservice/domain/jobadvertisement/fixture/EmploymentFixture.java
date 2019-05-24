package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Employment;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkForm;

import static com.google.common.collect.Sets.immutableEnumSet;
import static java.time.LocalDate.now;

public class EmploymentFixture {

    public static Employment.Builder testEmployment() {
        return new Employment.Builder()
                .setStartDate(now())
                .setEndDate(now().plusDays(31))
                .setWorkloadPercentageMin(80)
                .setWorkloadPercentageMax(100)
                .setWorkForms(immutableEnumSet(WorkForm.SHIFT_WORK, WorkForm.SUNDAY_AND_HOLIDAYS));
    }
}
