package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.AuditUser;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventMockUtils;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.AccessTokenGenerator;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Employment;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementCreator;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementFactory;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Publication;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.ContactFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobContentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class JobAdvertisementFactoryTest {

    private static final String TEST_STELLEN_NUMMER_EGOV = "1000000";

    private DomainEventMockUtils domainEventMockUtils;
    private JobAdvertisementFactory jobAdvertisementFactory;

    @Mock
    private JobAdvertisementRepository repository;

    @Before
    public void setUp() {
        domainEventMockUtils = new DomainEventMockUtils();
        JobAdvertisementRepository jobAdvertisementRepository = spy(TestJobAdvertisementRepository.class);
        DataFieldMaxValueIncrementer stellennummerEgovGenerator = spy(DataFieldMaxValueIncrementer.class);
        AccessTokenGenerator accessTokenGenerator = spy(AccessTokenGenerator.class);
        when(stellennummerEgovGenerator.nextStringValue()).thenReturn(TEST_STELLEN_NUMMER_EGOV);
        jobAdvertisementFactory = new JobAdvertisementFactory(jobAdvertisementRepository, accessTokenGenerator, stellennummerEgovGenerator);
    }

    @After
    public void tearDown() {
        domainEventMockUtils.clearEvents();
    }

    @Test
    public void testCreateFromWebForm() {
        //given
        JobAdvertisementCreator creator = testJobAdvertisementCreator();

        //when
        JobAdvertisement jobAdvertisement = jobAdvertisementFactory.createFromWebForm(creator);

        //then
        assertThat(jobAdvertisement.getStatus()).isEqualTo(JobAdvertisementStatus.CREATED);
        assertThat(jobAdvertisement.getSourceSystem()).isEqualTo(SourceSystem.JOBROOM);
        assertThat(jobAdvertisement.getStellennummerEgov()).isEqualTo(TEST_STELLEN_NUMMER_EGOV);

        Employment employment = jobAdvertisement.getJobContent().getEmployment();
        Employment employmentCreator = creator.getJobContent().getEmployment();
        assertThat(employment.getStartDate()).isEqualTo(employmentCreator.getStartDate());
        assertThat(employment.getEndDate()).isEqualTo(employmentCreator.getEndDate());
        assertThat(employment.getWorkloadPercentageMin()).isEqualTo(employmentCreator.getWorkloadPercentageMin());
        assertThat(employment.getWorkloadPercentageMax()).isEqualTo(employmentCreator.getWorkloadPercentageMax());
        assertThat(employment.getWorkForms()).isNotNull();
        assertThat(employment.getWorkForms()).isEqualTo(employmentCreator.getWorkForms());

        JobAdvertisementEvent jobAdvertisementEvent = domainEventMockUtils.assertSingleDomainEventPublished(JobAdvertisementEvents.JOB_ADVERTISEMENT_CREATED.getDomainEventType());
        assertThat(jobAdvertisementEvent.getAggregateId()).isEqualTo(jobAdvertisement.getId());
    }

    @Test
    public void createFromApi() {
        //Prepare
        JobAdvertisementCreator creator = testJobAdvertisementCreator();

        //Execute
        JobAdvertisement jobAdvertisement = jobAdvertisementFactory.createFromApi(creator);

        //Validate
        assertThat(jobAdvertisement.getStatus()).isEqualTo(JobAdvertisementStatus.CREATED);
        assertThat(jobAdvertisement.getSourceSystem()).isEqualTo(SourceSystem.API);
        assertThat(jobAdvertisement.getStellennummerEgov()).isEqualTo(TEST_STELLEN_NUMMER_EGOV);

        Employment employment = jobAdvertisement.getJobContent().getEmployment();
        Employment employmentCreator = creator.getJobContent().getEmployment();
        assertThat(employment.getStartDate()).isEqualTo(employmentCreator.getStartDate());
        assertThat(employment.getEndDate()).isEqualTo(employmentCreator.getEndDate());
        assertThat(employment.getWorkloadPercentageMin()).isEqualTo(employmentCreator.getWorkloadPercentageMin());
        assertThat(employment.getWorkloadPercentageMax()).isEqualTo(employmentCreator.getWorkloadPercentageMax());
        assertThat(employment.getWorkForms()).isNotNull();
        assertThat(employment.getWorkForms()).isEqualTo(employmentCreator.getWorkForms());

        JobAdvertisementEvent jobAdvertisementEvent = domainEventMockUtils.assertSingleDomainEventPublished(JobAdvertisementEvents.JOB_ADVERTISEMENT_CREATED.getDomainEventType());
        assertThat(jobAdvertisementEvent.getAggregateId()).isEqualTo(jobAdvertisement.getId());
    }

    public static JobAdvertisementCreator testJobAdvertisementCreator() {
        return new JobAdvertisementCreator.Builder(createAuditUser())
                .setContact(ContactFixture.of(job01.id()).build())
                .setJobContent(JobContentFixture.of(job01.id()).build())
                .setPublication(new Publication.Builder().build())
                .build();
    }

    @SuppressWarnings("unchecked")
    static abstract class TestJobAdvertisementRepository implements JobAdvertisementRepository {

        @Override
        public JobAdvertisement save(JobAdvertisement jobAdvertisement) {
            return jobAdvertisement;
        }
    }

    private static AuditUser createAuditUser() {
        return new AuditUser("user-1","extern-1", "company-1", "My", "User", "my.user@example.org");
    }
}
