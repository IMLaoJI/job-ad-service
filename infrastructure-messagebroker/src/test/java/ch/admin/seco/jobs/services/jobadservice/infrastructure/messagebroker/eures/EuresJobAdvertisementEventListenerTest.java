package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.eures;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementEvents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture.testJobAdvertisement;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(Parameterized.class)
public class EuresJobAdvertisementEventListenerTest {

    private JobAdvertisementEvents event;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Mock
    private EuresMessagingService euresMessagingService;

    @InjectMocks
    private EuresJobAdvertisementEventListener euresJobAdvertisementEventListener;

    @Parameters(name = "Should handle JobAdvertisementEvent : {0}")
    public static List<JobAdvertisementEvents> events() {
        return Arrays.asList(JobAdvertisementEvents.values());
    }

    public EuresJobAdvertisementEventListenerTest(JobAdvertisementEvents event) {
        this.event = event;
    }

    @Test
    public void shouldHandleJobAdvertisementEvent() {
        //given
        JobAdvertisement jobAdvertisement = testJobAdvertisement().build();
        JobAdvertisementEvent jobAdvertisementEvent = new JobAdvertisementEvent(this.event, jobAdvertisement);

        // when
        euresJobAdvertisementEventListener.handleJobAdvertisementEvent(jobAdvertisementEvent);

        // then
        verify(euresMessagingService).sendEuresEventToMessageBroker(jobAdvertisement.getId().getValue());
    }
}