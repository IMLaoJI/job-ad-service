package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class JobAdvertisementStatusTest {

    @Test
    public void validateTransitionTo() {
        JobAdvertisementStatus status = JobAdvertisementStatus.ARCHIVED;

        JobAdvertisementStatus result = status.validateTransitionTo(JobAdvertisementStatus.REFINING);

        assertThat(result).isEqualTo(JobAdvertisementStatus.REFINING);
    }

    @Test
    public void notValidateTransitionTo() {
        JobAdvertisementStatus status = JobAdvertisementStatus.ARCHIVED;

        assertThatExceptionOfType(IllegalJobAdvertisementStatusTransitionException.class)
                .isThrownBy(() -> status.validateTransitionTo(JobAdvertisementStatus.CREATED));
    }

    @Test
    public void canTransitTo() {
        JobAdvertisementStatus status = JobAdvertisementStatus.ARCHIVED;

        boolean result = status.canTransitTo(JobAdvertisementStatus.REFINING);

        assertThat(result).isTrue();
    }

    @Test
    public void canNotTransitTo() {
        JobAdvertisementStatus status = JobAdvertisementStatus.ARCHIVED;

        boolean result = status.canTransitTo(JobAdvertisementStatus.CREATED);

        assertThat(result).isFalse();
    }

    @Test
    public void isInAnyStates() {
        JobAdvertisementStatus status = JobAdvertisementStatus.ARCHIVED;

        boolean result = status.isInAnyStates(JobAdvertisementStatus.REFINING, JobAdvertisementStatus.ARCHIVED);

        assertThat(result).isTrue();
    }

    @Test
    public void isNotInAnyStates() {
        JobAdvertisementStatus status = JobAdvertisementStatus.ARCHIVED;

        boolean result = status.isInAnyStates(JobAdvertisementStatus.REFINING, JobAdvertisementStatus.CREATED);

        assertThat(result).isFalse();
    }

    @Test
    public void isEqualOrForwardDestination() {
        boolean result1 = JobAdvertisementStatus.isEqualOrForwardDestination(JobAdvertisementStatus.ARCHIVED, JobAdvertisementStatus.ARCHIVED);
        boolean result2 = JobAdvertisementStatus.isEqualOrForwardDestination(JobAdvertisementStatus.ARCHIVED, JobAdvertisementStatus.REFINING);
        boolean result3 = JobAdvertisementStatus.isEqualOrForwardDestination(JobAdvertisementStatus.CREATED, JobAdvertisementStatus.ARCHIVED);

        assertThat(result1).isTrue();
        assertThat(result2).isTrue();
        assertThat(result3).isTrue();
    }

    @Test
    public void isNotEqualOrForwardDestination() {
        boolean result = JobAdvertisementStatus.isEqualOrForwardDestination(JobAdvertisementStatus.ARCHIVED, JobAdvertisementStatus.CREATED);

        assertThat(result).isFalse();
    }
}