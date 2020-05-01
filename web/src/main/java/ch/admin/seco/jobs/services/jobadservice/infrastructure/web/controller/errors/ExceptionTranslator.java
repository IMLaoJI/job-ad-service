package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.errors;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.SearchProfileNameAlreadyExistsException;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.JobAlertMaxAmountReachedException;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.application.security.Role;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.ConditionException;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.IllegalJobAdvertisementStatusTransitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.validation.ConstraintViolationProblem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.errors.ErrorConstants.ERR_CONCURRENCY_FAILURE;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807)
 */
@ControllerAdvice
public class ExceptionTranslator implements ProblemHandling {

	private static final String MESSAGE = "message";

	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionTranslator.class);

	private final CurrentUserContext currentUserContext;

	public ExceptionTranslator(CurrentUserContext currentUserContext) {
		this.currentUserContext = currentUserContext;
	}

	/**
	 * Post-process Problem payload to add the message key for front-end if needed
	 */
	@Override
	public ResponseEntity<Problem> process(@Nullable ResponseEntity<Problem> entity, NativeWebRequest request) {
		if (entity == null || entity.getBody() == null) {
			return entity;
		}
		final Problem problem = entity.getBody();

		logForApiUser(problem);

		if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
			return entity;
		}

		ProblemBuilder builder = Problem.builder()
				.withType(Problem.DEFAULT_TYPE.equals(problem.getType()) ? ErrorConstants.DEFAULT_TYPE : problem.getType())
				.withStatus(problem.getStatus())
				.withTitle(problem.getTitle())
				.with("path", request.getNativeRequest(HttpServletRequest.class).getRequestURI());

		if (problem instanceof ConstraintViolationProblem) {
			builder
					.with("violations", ((ConstraintViolationProblem) problem).getViolations())
					.with(MESSAGE, ErrorConstants.ERR_VALIDATION);
		} else {
			builder
					.withCause(((DefaultProblem) problem).getCause())
					.withDetail(problem.getDetail())
					.withInstance(problem.getInstance());
			problem.getParameters().forEach(builder::with);
			if (!problem.getParameters().containsKey(MESSAGE) && problem.getStatus() != null) {
				builder.with(MESSAGE, "error.http." + problem.getStatus().getStatusCode());
			}
		}

		return new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode());
	}

	@Override
	public ResponseEntity<Problem> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @Nonnull NativeWebRequest request) {
		BindingResult result = ex.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors().stream()
				.map(f -> new FieldError(f.getObjectName(), f.getField(), f.getDefaultMessage()))
				.collect(Collectors.toList());

		Problem problem = Problem.builder()
				.withType(ErrorConstants.CONSTRAINT_VIOLATION_TYPE)
				.withTitle("Method argument not valid")
				.withStatus(defaultConstraintViolationStatus())
				.with(MESSAGE, ErrorConstants.ERR_VALIDATION)
				.with("fieldErrors", fieldErrors)
				.build();
		return create(ex, problem, request);
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<Problem> handleNoSuchElementException(NoSuchElementException ex, NativeWebRequest request) {
		Problem problem = Problem.builder()
                .withTitle("Element not found")
				.withStatus(Status.NOT_FOUND)
                .withType(ErrorConstants.ENTITY_NOT_FOUND_TYPE)
				.with(MESSAGE, ErrorConstants.ENTITY_NOT_FOUND_TYPE)
				.build();
		return create(ex, problem, request);
	}

	@ExceptionHandler(AggregateNotFoundException.class)
	public ResponseEntity<Problem> handleAggregateNotFoundException(AggregateNotFoundException ex, NativeWebRequest request) {
		Problem problem = Problem.builder()
                .withTitle("Aggregate not found")
				.withStatus(Status.NOT_FOUND)
				.withType(ErrorConstants.ENTITY_NOT_FOUND_TYPE)
				.with("aggregateName", ex.getAggregateName())
				.with("aggregateId", ex.getIdentifierValue())
				.with(MESSAGE, ex.getMessage())
				.build();
		return create(ex, problem, request);
	}

	@ExceptionHandler(ConditionException.class)
	public ResponseEntity<Problem> handleConditionException(ConditionException ex, NativeWebRequest request) {
		Problem problem = Problem.builder()
                .withTitle("Condition violation")
				.withStatus(Status.BAD_REQUEST)
				.withType(ErrorConstants.CONDITION_VIOLATION_TYPE)
				.with(MESSAGE, ex.getMessage())
				.build();
		return create(ex, problem, request);
	}

	@ExceptionHandler(IllegalJobAdvertisementStatusTransitionException.class)
	public ResponseEntity<Problem> handleIllegalJobAdvertisementStatusTransitionException(IllegalJobAdvertisementStatusTransitionException ex, NativeWebRequest request) {
		Problem problem = Problem.builder()
                .withTitle("JobAdvertisement Status violation")
				.withStatus(Status.BAD_REQUEST)
				.withType(ErrorConstants.STATUS_VIOLATION_TYPE)
				.with(MESSAGE, ex.getMessage())
				.build();
		return create(ex, problem, request);
	}

	@ExceptionHandler(ConcurrencyFailureException.class)
	public ResponseEntity<Problem> handleConcurrencyFailure(ConcurrencyFailureException ex, NativeWebRequest request) {
		Problem problem = Problem.builder()
                .withTitle("Concurrency failure")
				.withType(ErrorConstants.DEFAULT_TYPE)
				.withStatus(Status.CONFLICT)
				.with(MESSAGE, ERR_CONCURRENCY_FAILURE)
				.build();
		return create(ex, problem, request);
	}

	@ExceptionHandler(SearchProfileNameAlreadyExistsException.class)
	public ResponseEntity<Problem> handleSearchProfileNameAlreadyExistsException(SearchProfileNameAlreadyExistsException ex, NativeWebRequest request) {
		Problem problem = Problem.builder()
                .withTitle("SearchProfile already exists")
				.withType(ErrorConstants.SEARCH_PROFILE_EXISTS)
				.withStatus(Status.BAD_REQUEST)
				.with(MESSAGE, ex.getMessage())
				.build();
		return create(ex, problem, request);
	}

	@ExceptionHandler(JobAlertMaxAmountReachedException.class)
	public ResponseEntity<Problem> handleJobAlertMaxAmountReachedException(JobAlertMaxAmountReachedException ex, NativeWebRequest request) {
		Problem problem = Problem.builder()
				.withTitle("Maximum amount of JobAlerts Reached!")
				.withType(ErrorConstants.JOB_ALERT_MAX_AMOUNT_REACHED)
				.withStatus(Status.PRECONDITION_FAILED)
				.with(MESSAGE, ex.getMessage())
				.build();
		return create(ex, problem, request);
	}

	private void logForApiUser(Problem problem) {
		if (this.currentUserContext.hasRole(Role.API)) {
			LOGGER.info("'{}' received from API user '{}' : '{}'", problem.getTitle(),
					this.currentUserContext.getCurrentUser().getUserId(), problem.getParameters().getOrDefault(MESSAGE, ErrorConstants.DEFAULT_TYPE));
		}
	}

}
