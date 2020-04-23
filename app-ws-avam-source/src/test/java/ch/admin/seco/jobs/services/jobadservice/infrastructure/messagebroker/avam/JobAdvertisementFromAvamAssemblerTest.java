package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.AvamCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.ws.avam.source.WSOsteEgov;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JobAdvertisementFromAvamAssemblerTest {
    @Test
    public void shouldNotBeImmediately() {
        // given
        JobAdvertisementFromAvamAssembler assembler = new JobAdvertisementFromAvamAssembler();
        WSOsteEgov avamJobAd = new WSOsteEgov();
        avamJobAd.setStellenantritt(null);
        // when
        AvamCreateJobAdvertisementDto jobAdvertisementDto = assembler.createCreateJobAdvertisementAvamDto(avamJobAd);
        // then
        assertThat(jobAdvertisementDto.getEmployment().isImmediately()).isFalse();
    }
}
