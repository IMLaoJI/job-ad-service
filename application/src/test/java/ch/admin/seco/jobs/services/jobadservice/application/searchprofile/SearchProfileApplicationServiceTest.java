package ch.admin.seco.jobs.services.jobadservice.application.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventMockUtils;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class SearchProfileApplicationServiceTest {

    @Autowired
    private SearchProfileApplicationService sut;

    @Autowired
    private SearchProfileRepository searchProfileRepository;

    private DomainEventMockUtils domainEventMockUtils;

    @Before
    public void setUp() {
        this.domainEventMockUtils = new DomainEventMockUtils();
    }

    @After
    public void tearDown() {
        this.searchProfileRepository.deleteAll();
        this.domainEventMockUtils.clearEvents();
    }

    @Test
    public void testCreate() {
        // implement tests
    }
}
