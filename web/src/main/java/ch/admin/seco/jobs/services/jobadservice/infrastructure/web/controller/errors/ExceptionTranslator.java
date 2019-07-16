package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.errors;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.SearchProfileNameAlreadyExistsException;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.ConditionException;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.IllegalJobAdvertisementStatusTransitionException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import ch.admin.seco.alv.shared.error.handling.ExceptionTranslatorFilter;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.errors.ErrorConstants.ERR_CONCURRENCY_FAILURE;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807)
 */
@ControllerAdvice
public class ExceptionTranslator extends ExceptionTranslatorFilter implements ProblemHandling {
	@ExceptionHandler(AggregateNotFoundException.class)
	public ResponseEntity<Problem> handleAggregateNotFoundException(AggregateNotFoundException ex, NativeWebRequest request) {
		Problem problem = Problem.builder()
				.withStatus(Status.NOT_FOUND)
				.withType(ErrorConstants.ENTITY_NOT_FOUND_TYPE)
				.with("aggregateName", ex.getAggregateName())
				.with("aggregateId", ex.getIdentifierValue())
				.with("message", ex.getMessage())
				.build();
		return create(ex, problem, request);
	}

	@ExceptionHandler(ConditionException.class)
	public ResponseEntity<Problem> handleConditionException(ConditionException ex, NativeWebRequest request) {
		Problem problem = Problem.builder()
				.withStatus(Status.BAD_REQUEST)
				.withType(ErrorConstants.CONDITION_VIOLATION_TYPE)
				.with("message", ex.getMessage())
				.build();
		return create(ex, problem, request);
	}

	@ExceptionHandler(IllegalJobAdvertisementStatusTransitionException.class)
	public ResponseEntity<Problem> handleIllegalJobAdvertisementStatusTransitionException(IllegalJobAdvertisementStatusTransitionException ex, NativeWebRequest request) {
		Problem problem = Problem.builder()
				.withStatus(Status.BAD_REQUEST)
				.withType(ErrorConstants.STATUS_VIOLATION_TYPE)
				.with("message", ex.getMessage())
				.build();
		return create(ex, problem, request);
	}

	@ExceptionHandler(ConcurrencyFailureException.class)
	public ResponseEntity<Problem> handleConcurrencyFailure(ConcurrencyFailureException ex, NativeWebRequest request) {
		Problem problem = Problem.builder()
				.withType(ErrorConstants.DEFAULT_TYPE)
				.withStatus(Status.CONFLICT)
				.with("message", ERR_CONCURRENCY_FAILURE)
				.build();
		return create(ex, problem, request);
	}

	@ExceptionHandler(SearchProfileNameAlreadyExistsException.class)
	public ResponseEntity<Problem> handleSearchProfileNameAlreadyExistsException(SearchProfileNameAlreadyExistsException ex, NativeWebRequest request) {
		Problem problem = Problem.builder()
				.withType(ErrorConstants.SEARCH_PROFILE_EXISTS)
				.withStatus(Status.BAD_REQUEST)
				.with("message", ex.getMessage())
				.build();
		return create(ex, problem, request);
	}

}
