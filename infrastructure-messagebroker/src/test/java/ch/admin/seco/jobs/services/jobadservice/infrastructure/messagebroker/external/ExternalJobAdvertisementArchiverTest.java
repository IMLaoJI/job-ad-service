package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.external;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEvent;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventMockUtils;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.CREATED;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_PUBLIC;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents.JOB_ADVERTISEMENT_PUBLISH_EXPIRED;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.testJobAdvertisementWithExternalSourceSystemAndStatus;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ExternalJobAdvertisementArchiverTest {
    private static final String FINGERPRINT_1 = "fingerprint1";
    private static final String FINGERPRINT_2 = "fingerprint2";
    private static final String FINGERPRINT_3 = "fingerprint3";

    @Autowired
    private ExternalMessageLogRepository externalMessageLogRepository;

    @Autowired
    private JobAdvertisementRepository jobAdvertisementRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private DomainEventMockUtils domainEventMockUtils;

    private PrismeJobAdvertisementArchiverService sut; //System Under Test

    @Before
    public void setUp() {
        this.sut = new PrismeJobAdvertisementArchiverService(jobAdvertisementRepository, transactionTemplate, externalMessageLogRepository);
        domainEventMockUtils = new DomainEventMockUtils();
    }

    @Test
    public void scheduledArchiveExternalJobAds() {
        // given
        jobAdvertisementRepository.save(testJobAdvertisementWithExternalSourceSystemAndStatus(job01.id(), FINGERPRINT_1, CREATED));
        jobAdvertisementRepository.save(testJobAdvertisementWithExternalSourceSystemAndStatus(job02.id(), FINGERPRINT_2, PUBLISHED_PUBLIC));
        jobAdvertisementRepository.save(testJobAdvertisementWithExternalSourceSystemAndStatus(job03.id(), FINGERPRINT_3, PUBLISHED_PUBLIC));
        externalMessageLogRepository.save(new ExternalJobAdvertisementMessageLog(FINGERPRINT_2, now().minusDays(1)));
        externalMessageLogRepository.save(new ExternalJobAdvertisementMessageLog(FINGERPRINT_3, now()));

        // when
        this.sut.archiveExternalJobAdvertisements();

        // then
        DomainEvent domainEvent = domainEventMockUtils.assertSingleDomainEventPublished(JOB_ADVERTISEMENT_PUBLISH_EXPIRED.getDomainEventType());
        assertThat(domainEvent.getAggregateId()).isEqualTo(job02.id());
    }

    @Test
    public void shouldNotArchiveExternalJobAdsIfNoMessageReceivedOnTheSameDay() {
        // given
        jobAdvertisementRepository.save(testJobAdvertisementWithExternalSourceSystemAndStatus(job01.id(), FINGERPRINT_1, CREATED));
        jobAdvertisementRepository.save(testJobAdvertisementWithExternalSourceSystemAndStatus(job02.id(), FINGERPRINT_2, PUBLISHED_PUBLIC));
        jobAdvertisementRepository.save(testJobAdvertisementWithExternalSourceSystemAndStatus(job03.id(), FINGERPRINT_3, PUBLISHED_PUBLIC));
        externalMessageLogRepository.save(new ExternalJobAdvertisementMessageLog(FINGERPRINT_2, now().minusDays(1)));

        // when
        this.sut.archiveExternalJobAdvertisements();

        // then
        domainEventMockUtils.verifyNoEventsPublished();
    }
}
