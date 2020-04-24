package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.AvamCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.ws.avam.source.WSOsteEgov;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JobAdvertisementFromAvamAssemblerTest {
    @Test
    public void shouldNotBeImmediatelyWhenStellenantrittIsNull() {
        // given
        JobAdvertisementFromAvamAssembler assembler = new JobAdvertisementFromAvamAssembler();
        WSOsteEgov avamJobAd = new WSOsteEgov();
        avamJobAd.setStellenantritt(null);
        // when
        AvamCreateJobAdvertisementDto jobAdvertisementDto = assembler.createCreateJobAdvertisementAvamDto(avamJobAd);
        // then
        assertThat(jobAdvertisementDto.getEmployment().isImmediately()).isFalse();
    }

    @Test
    public void shouldNotBeImmediatelyWhenStellenantrittIsNotNull() {
        // given
        JobAdvertisementFromAvamAssembler assembler = new JobAdvertisementFromAvamAssembler();
        WSOsteEgov avamJobAd = new WSOsteEgov();
        avamJobAd.setStellenantritt("2020-04-24-00.00.00.0");
        // when
        AvamCreateJobAdvertisementDto jobAdvertisementDto = assembler.createCreateJobAdvertisementAvamDto(avamJobAd);
        // then
        assertThat(jobAdvertisementDto.getEmployment().isImmediately()).isFalse();
    }

    @Test
    public void shouldBeImmediatelyWhenSetInAvam() {
        // given
        JobAdvertisementFromAvamAssembler assembler = new JobAdvertisementFromAvamAssembler();
        WSOsteEgov avamJobAd = new WSOsteEgov();
        avamJobAd.setAbSofort(Boolean.TRUE);
        // when
        AvamCreateJobAdvertisementDto jobAdvertisementDto = assembler.createCreateJobAdvertisementAvamDto(avamJobAd);
        // then
        assertThat(jobAdvertisementDto.getEmployment().isImmediately()).isTrue();
    }

    @Test
    public void shouldNotBeImmediatelyWhenSetInAvamWithFalse() {
        // given
        JobAdvertisementFromAvamAssembler assembler = new JobAdvertisementFromAvamAssembler();
        WSOsteEgov avamJobAd = new WSOsteEgov();
        avamJobAd.setAbSofort(Boolean.FALSE);
        // when
        AvamCreateJobAdvertisementDto jobAdvertisementDto = assembler.createCreateJobAdvertisementAvamDto(avamJobAd);
        // then
        assertThat(jobAdvertisementDto.getEmployment().isImmediately()).isFalse();
    }

    @Test
    public void shouldNotBeImmediatelyWhenNotSetInAvam() {
        // given
        JobAdvertisementFromAvamAssembler assembler = new JobAdvertisementFromAvamAssembler();
        WSOsteEgov avamJobAd = new WSOsteEgov();
        avamJobAd.setAbSofort(null);
        // when
        AvamCreateJobAdvertisementDto jobAdvertisementDto = assembler.createCreateJobAdvertisementAvamDto(avamJobAd);
        // then
        assertThat(jobAdvertisementDto.getEmployment().isImmediately()).isFalse();
    }
}
