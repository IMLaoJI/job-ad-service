package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventMockUtils;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture.testJobAdvertisement;
import static com.google.common.collect.Sets.immutableEnumSet;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;

public class JobAdvertisementUpdateTest {

    private DomainEventMockUtils domainEventMockUtils;

    @Before
    public void setUp() {
        domainEventMockUtils = new DomainEventMockUtils();
    }

    @After
    public void tearDown() {
        TimeMachine.reset();
        domainEventMockUtils.clearEvents();
    }

    @Test
    public void shouldUpdateFields() {
        //given
        JobAdvertisement jobAdvertisement = testJobAdvertisement()
                .setStatus(JobAdvertisementStatus.PUBLISHED_PUBLIC)
                .build();

        LocalDate currentDate = now();
        Set<WorkForm> workFormSet = immutableEnumSet(WorkForm.HOME_WORK);
        JobAdvertisementUpdater updater = new JobAdvertisementUpdater.Builder(null)
                .setCompany(
                        new Company.Builder()
                                .setName("new-name")
                                .setStreet("new-street")
                                .setPostalCode("postalCode")
                                .setCity("city")
                                .setCountryIsoCode("CH")
                                .build()
                )
                .setEmployment(
                        new Employment.Builder()
                                .setShortEmployment(true)
                                .setWorkloadPercentageMax(90)
                                .setWorkloadPercentageMin(40)
                                .setPermanent(false)
                                .setImmediately(true)
                                .setStartDate(currentDate)
                                .setEndDate(currentDate.plusDays(31))
                                .setWorkForms(workFormSet)
                                .build()
                )
                .build();
        //when
        jobAdvertisement.update(updater);

        //then
        assertThat(jobAdvertisement.getStatus()).isEqualTo(JobAdvertisementStatus.PUBLISHED_PUBLIC);

        Company company = jobAdvertisement.getJobContent().getCompany();
        assertThat(company).isNotNull();
        assertThat(company.getName()).isEqualTo("new-name");
        assertThat(company.getStreet()).isEqualTo("new-street");
        assertThat(company.getHouseNumber()).isNull();
        assertThat(company.getPostalCode()).isEqualTo("postalCode");
        assertThat(company.getCity()).isEqualTo("city");
        assertThat(company.getCountryIsoCode()).isEqualTo("CH");
        assertThat(company.getPostOfficeBoxNumber()).isNull();
        assertThat(company.getPostOfficeBoxPostalCode()).isNull();
        assertThat(company.getPostOfficeBoxCity()).isNull();

        Employment employment = jobAdvertisement.getJobContent().getEmployment();
        assertThat(employment).isNotNull();
        assertThat(employment.isShortEmployment()).isTrue();
        assertThat(employment.isPermanent()).isFalse();
        assertThat(employment.isImmediately()).isTrue();
        assertThat(employment.getStartDate()).isEqualTo(currentDate);
        assertThat(employment.getEndDate()).isEqualTo(currentDate.plusDays(31));
        assertThat(employment.getWorkloadPercentageMin()).isEqualTo(40);
        assertThat(employment.getWorkloadPercentageMax()).isEqualTo(90);
        assertThat(employment.getWorkForms()).isNotNull();
        assertThat(employment.getWorkForms()).isEqualTo(workFormSet);

        JobAdvertisementEvent jobAdvertisementEvent = domainEventMockUtils.assertSingleDomainEventPublished(JobAdvertisementEvents.JOB_ADVERTISEMENT_UPDATED.getDomainEventType());
        assertThat(jobAdvertisementEvent.getAggregateId()).isEqualTo(jobAdvertisement.getId());
    }

    @Test
    public void shouldReactivateFromArchivedBeforeStartAndEndDate() {
        //given
        TimeMachine.useFixedClockAt(LocalDateTime.of(2019, 1, 18, 0, 0));
        JobAdvertisement jobAdvertisement = testJobAdvertisement()
                .setStatus(JobAdvertisementStatus.ARCHIVED)
                .setPublication(
                        new Publication.Builder()
                                .setStartDate(LocalDate.of(2019, 1, 10))
                                .setEndDate(LocalDate.of(2019, 1, 15))
                                .build()
                )
                .build();
        JobAdvertisementUpdater updater = new JobAdvertisementUpdater.Builder(null)
                .setPublication(
                        new Publication.Builder()
                                .setStartDate(LocalDate.of(2019, 1, 20))
                                .setEndDate(LocalDate.of(2019, 1, 25))
                                .build()
                )
                .build();

        //when
        jobAdvertisement.update(updater);

        //then
        assertThat(jobAdvertisement.getStatus()).isEqualTo(JobAdvertisementStatus.REFINING);
    }

    @Test
    public void shouldReactivateFromArchivedBetweenStartAndEndDate() {
        //given
        TimeMachine.useFixedClockAt(LocalDateTime.of(2019, 1, 22, 0, 0));
        JobAdvertisement jobAdvertisement = testJobAdvertisement()
                .setStatus(JobAdvertisementStatus.ARCHIVED)
                .setPublication(
                        new Publication.Builder()
                                .setStartDate(LocalDate.of(2019, 1, 10))
                                .setEndDate(LocalDate.of(2019, 1, 15))
                                .build()
                )
                .build();
        JobAdvertisementUpdater updater = new JobAdvertisementUpdater.Builder(null)
                .setPublication(
                        new Publication.Builder()
                                .setStartDate(LocalDate.of(2019, 1, 20))
                                .setEndDate(LocalDate.of(2019, 1, 25))
                                .build()
                )
                .build();

        //when
        jobAdvertisement.update(updater);

        //then
        assertThat(jobAdvertisement.getStatus()).isEqualTo(JobAdvertisementStatus.REFINING);
    }

    @Test
    public void shouldReactivateFromArchivedAfterStartAndEndDate() {
        //given
        TimeMachine.useFixedClockAt(LocalDateTime.of(2019, 1, 28, 0, 0));
        JobAdvertisement jobAdvertisement = testJobAdvertisement()
                .setStatus(JobAdvertisementStatus.ARCHIVED)
                .setPublication(
                        new Publication.Builder()
                                .setStartDate(LocalDate.of(2019, 1, 10))
                                .setEndDate(LocalDate.of(2019, 1, 15))
                                .build()
                )
                .build();
        JobAdvertisementUpdater updater = new JobAdvertisementUpdater.Builder(null)
                .setPublication(
                        new Publication.Builder()
                                .setStartDate(LocalDate.of(2019, 1, 20))
                                .setEndDate(LocalDate.of(2019, 1, 25))
                                .build()
                )
                .build();

        //when
        jobAdvertisement.update(updater);

        //then
        assertThat(jobAdvertisement.getStatus()).isEqualTo(JobAdvertisementStatus.ARCHIVED);
    }

    @Test
    public void shouldAdjournPublicationWhenBeforeStartAndEndDate() {
        //given
        TimeMachine.useFixedClockAt(LocalDateTime.of(2019, 1, 18, 0, 0));
        JobAdvertisement jobAdvertisement = testJobAdvertisement()
                .setStatus(JobAdvertisementStatus.PUBLISHED_PUBLIC)
                .setPublication(
                        new Publication.Builder()
                                .setStartDate(LocalDate.of(2019, 1, 10))
                                .setEndDate(LocalDate.of(2019, 1, 15))
                                .build()
                )
                .build();
        JobAdvertisementUpdater updater = new JobAdvertisementUpdater.Builder(null)
                .setPublication(
                        new Publication.Builder()
                                .setStartDate(LocalDate.of(2019, 1, 20))
                                .setEndDate(LocalDate.of(2019, 1, 25))
                                .build()
                )
                .build();

        //when
        jobAdvertisement.update(updater);

        //then
        assertThat(jobAdvertisement.getStatus()).isEqualTo(JobAdvertisementStatus.REFINING);
    }

    @Test
    public void shouldNotAdjournPublicationWhenBetweenStartAndEndDate() {
        //given
        TimeMachine.useFixedClockAt(LocalDateTime.of(2019, 1, 18, 0, 0));
        JobAdvertisement jobAdvertisement = testJobAdvertisement()
                .setStatus(JobAdvertisementStatus.PUBLISHED_PUBLIC)
                .setPublication(
                        new Publication.Builder()
                                .setStartDate(LocalDate.of(2019, 1, 10))
                                .setEndDate(LocalDate.of(2019, 1, 15))
                                .build()
                )
                .build();
        JobAdvertisementUpdater updater = new JobAdvertisementUpdater.Builder(null)
                .setPublication(
                        new Publication.Builder()
                                .setStartDate(LocalDate.of(2019, 1, 10))
                                .setEndDate(LocalDate.of(2019, 1, 25))
                                .build()
                )
                .build();

        //when
        jobAdvertisement.update(updater);

        //then
        assertThat(jobAdvertisement.getStatus()).isEqualTo(JobAdvertisementStatus.PUBLISHED_PUBLIC);
    }

}
