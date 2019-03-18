package ch.admin.seco.jobs.services.jobadservice.integration.x28.exporter;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Ignore("TODO: EXPORT TEST IS NOT YET WORKING")
public class X28JobAdExportTaskTest {

    @Before
    public void setUp() {

    }

    @Test
    public void testExport() {
        System.out.println("test");
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }
    }


}