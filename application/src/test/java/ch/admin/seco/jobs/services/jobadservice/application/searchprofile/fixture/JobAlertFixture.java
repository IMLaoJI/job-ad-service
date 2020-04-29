package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.fixture;

import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.Interval;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.JobAlert;

import java.util.LinkedHashSet;

public class JobAlertFixture {

    public static JobAlert testJobAlert() {
        return JobAlert.builder()
                .setCreatedAt(TimeMachine.now())
                .setLastUpdatedAt(TimeMachine.now())
                .setNextReleaseAt(TimeMachine.now().plusHours(10))
                .setQuery("query")
                .setMatchedJobAdvertisementIds(new LinkedHashSet<>())
                .setInterval(Interval.INT_1DAY)
                .build();
    }

    static JobAlert testJobAlertToBeReleased() {
        return JobAlert.builder()
                .setCreatedAt(TimeMachine.now())
                .setLastUpdatedAt(TimeMachine.now())
                .setNextReleaseAt(TimeMachine.now().minusDays(1))
                .setQuery("query")
                .setMatchedJobAdvertisementIds(new LinkedHashSet<>())
                .setInterval(Interval.INT_1DAY)
                .build();
    }

}
