package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.JobAdvertisementFromAvamAssembler.determineStartDateAndImmediately;
import static org.assertj.core.api.Assertions.*;

@RunWith(Parameterized.class)
public class JobAdvertisementFromAvamAssemblerTest {
    @Parameterized.Parameter(0)
    public Boolean abSofrot;

    @Parameterized.Parameter(1)
    public String stellenAntritt;

    @Parameterized.Parameter(2)
    public Boolean nachVereinbarung;

    @Parameterized.Parameter(3)
    public boolean immediately;

    @Parameterized.Parameter(4)
    public LocalDate startDate;


    @Parameterized.Parameters(name = "{index}: Test with abSofrot={0}, stellenAntritt={1}, nachVereinbarung={2} reust is immediately={3}, startDate={4}")
    public static Collection<Object[]> data() {
        LocalDate nextWeek = LocalDate.now().plusWeeks(1);
        String stellenAntritt = AvamDateTimeFormatter.formatLocalDate(nextWeek);
        Object[][] data = new Object[][]{
                {true, stellenAntritt, true, true, null},
                {true, stellenAntritt, false, true, null}, {true, stellenAntritt, null, true, null},
                {true, null, true, true, null},
                {true, null, false, true, null}, {true, null, null, true, null},
                {false, stellenAntritt, true, false, nextWeek}, {null, stellenAntritt, true, false, nextWeek},
                {false, stellenAntritt, false, false, nextWeek}, {null, stellenAntritt, null, false, nextWeek},
                {false, null, true, false, null}, {false, null, true, false, null},
                {false, null, false, true, null}, {null, null, false, true, null},
        };
        return Arrays.asList(data);
    }

    @Test
    public void testDetermineStartDateAndImmediately() {

        assertThat(determineStartDateAndImmediately(stellenAntritt, nachVereinbarung, abSofrot))
                .isEqualTo(ImmutablePair.of(startDate, immediately));
    }
}
