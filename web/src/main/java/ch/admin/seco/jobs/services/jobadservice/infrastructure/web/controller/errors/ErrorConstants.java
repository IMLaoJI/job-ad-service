package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.errors;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";

    public static final String ERR_VALIDATION = "error.validation";

    public static final String PROBLEM_BASE_URL = "http://www.job-room.ch/job-ad-service/problem";

    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");

    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");

    public static final URI PARAMETERIZED_TYPE = URI.create(PROBLEM_BASE_URL + "/parameterized");

    public static final URI ENTITY_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/entity-not-found");

    public static final URI CONDITION_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/condition-violation");

    public static final URI STATUS_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/status-violation");

    public static final URI SEARCH_PROFILE_EXISTS = URI.create(PROBLEM_BASE_URL + "/search-profile/already-exists");

    public static final URI JOB_ALERT_MAX_AMOUNT_REACHED = URI.create(PROBLEM_BASE_URL + "/job-alert/max-amount-reached");

    private ErrorConstants() {
    }
}
