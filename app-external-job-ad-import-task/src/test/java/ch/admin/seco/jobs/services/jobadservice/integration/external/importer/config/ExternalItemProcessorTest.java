package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config;


import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.integration.external.jobadimport.Oste;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ExternalItemProcessorTest {

    @Autowired
    private ExternalItemProcessor externalItemProcessor;

    @Test
    public void testProcessWithValidation() {
        // given
        Oste oste = new Oste();

        //when
        CreateJobAdvertisementDto result = this.externalItemProcessor.process(oste);

        //then
        assertThat(result).isNull();
    }
}
