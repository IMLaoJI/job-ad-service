package ch.admin.seco.jobs.services.jobadservice.integration.external.exporter;

//import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
//import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
//import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture;
//import com.jcraft.jsch.ChannelSftp;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
import org.junit.runner.RunWith;
//import org.springframework.batch.core.ExitStatus;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.test.JobLauncherTestUtils;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.integration.file.remote.session.Session;
//import org.springframework.integration.file.remote.session.SessionFactory;
//import org.springframework.integration.sftp.outbound.SftpMessageHandler;
//import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.mock;
//import static org.springframework.batch.core.BatchStatus.COMPLETED;
//import static org.springframework.util.StringUtils.quote;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ExternalJobAdvertisementExportTaskTest {

   /* @Autowired
    private SftpMessageHandler sftpMessageHandler;

    @Autowired
    private JobAdvertisementRepository jobAdvertisementRepository;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Before
    public void setUp() {
        for (int i = 0; i < 100; i++) {
            this.jobAdvertisementRepository.saveAndFlush(JobAdvertisementTestFixture.createJob(new JobAdvertisementId("Test-" + i)));
        }
    }

    @Test
    public void testExport() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        // assert job execution
        assertThat(jobExecution.getStatus()).as("JobExecution Status").isEqualTo(COMPLETED);
        assertThat(jobExecution.getExitStatus()).as("JobExecution ExitStatus").isEqualTo(ExitStatus.COMPLETED);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        SftpMessageHandler sftpMessageHandler() {
            SftpMessageHandler sftpMessageHandler = new SftpMessageHandler(new SftpRemoteFileTemplate(new SessionFactory<ChannelSftp.LsEntry>() {
                @Override
                public Session<ChannelSftp.LsEntry> getSession() {
                    return mock(Session.class);
                }
            }));
            sftpMessageHandler.setRemoteDirectoryExpressionString(quote("testDir"));
            return sftpMessageHandler;
        }

        @Bean
        JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }
    }*/


}
