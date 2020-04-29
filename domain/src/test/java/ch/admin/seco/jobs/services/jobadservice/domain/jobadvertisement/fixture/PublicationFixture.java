package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Publication.Builder;

import static java.time.LocalDate.now;

public class PublicationFixture {

    public static Builder testPublication(){
        return testPublicationEmpty().
              setStartDate(now()).
              setEndDate(now().plusDays(5));
    }

    public static Builder testPublicationEmpty() {
        return new Builder();
    }
}
