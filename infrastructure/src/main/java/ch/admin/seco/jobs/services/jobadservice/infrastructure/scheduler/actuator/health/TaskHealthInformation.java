package ch.admin.seco.jobs.services.jobadservice.infrastructure.scheduler.actuator.health;

import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskHealthInformation {

    private final LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private Duration duration;

    private Throwable failureException;

    TaskHealthInformation() {
        this.startedAt = TimeMachine.now();
    }

    LocalDateTime getStartedAt() {
        return startedAt;
    }

    LocalDateTime getEndedAt() {
        return endedAt;
    }

    Duration getDuration() {
        return duration;
    }

    Throwable getFailureException() {
        return failureException;
    }

    void completed() {
        this.endedAt = TimeMachine.now();
        this.duration = Duration.between(this.startedAt, this.endedAt);
    }

    void failed(Throwable e) {
        this.completed();
        this.failureException = e;
    }

    @Override
    public String toString() {
        return "TaskHealthInformation{" +
                "startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", duration=" + duration +
                ", failureException=" + failureException +
                '}';
    }
}
