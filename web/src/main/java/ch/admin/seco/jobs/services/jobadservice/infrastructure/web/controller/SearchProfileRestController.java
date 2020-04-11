package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.SearchProfileApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.SearchProfileNotExitsException;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.CreateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.JobAlertDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.ResolvedSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileResultDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.UpdateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.util.PaginationUtil;
import io.micrometer.core.annotation.Timed;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/searchProfiles")
public class SearchProfileRestController {

	private final SearchProfileApplicationService searchProfileApplicationService;

	public SearchProfileRestController(SearchProfileApplicationService searchProfileApplicationService) {
		this.searchProfileApplicationService = searchProfileApplicationService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResolvedSearchProfileDto createSearchProfile(@RequestBody @Valid CreateSearchProfileDto createSearchProfileDto) {
		return this.searchProfileApplicationService.createSearchProfile(createSearchProfileDto);
	}

	@PutMapping("/{id}")
	public ResolvedSearchProfileDto updateSearchProfile(@PathVariable SearchProfileId id, @RequestBody UpdateSearchProfileDto updateSearchProfileDto)
			throws SearchProfileNotExitsException {
		return this.searchProfileApplicationService.updateSearchProfile(id, updateSearchProfileDto);
	}

	@PostMapping("/jobalert/_action/subscribe/{id}")
	public ResolvedSearchProfileDto subscribeToJobAlert(@PathVariable SearchProfileId id, @RequestBody JobAlertDto jobAlertDto) {
		return this.searchProfileApplicationService.subscribeToJobAlert(id, jobAlertDto);
	}

	@PatchMapping("/jobalert/_action/unsubscribe/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("@searchProfileAuthorizationService.canUnsubscribeFromJobAlert(#id, #token)")
	public void unsubscribeFromJobAlert(@PathVariable SearchProfileId id, @RequestParam(required = false) String token) {
		this.searchProfileApplicationService.unsubscribeFromJobAlert(id);
	}

	@GetMapping("/jobalert/_action/release/{id}")
	@Timed
	public void releaseJobAlert(@PathVariable SearchProfileId id) {
		this.searchProfileApplicationService.manualReleaseJobAlert(id);
	}

	@GetMapping("/jobalert/housekeeping")
	@Timed
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void jobAlertHousekeeping(@RequestParam LocalDateTime beforeDateTime) {
		this.searchProfileApplicationService.jobAlertHousekeeping(beforeDateTime);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSearchProfile(@PathVariable SearchProfileId id) throws SearchProfileNotExitsException {
		this.searchProfileApplicationService.deleteSearchProfile(id);
	}

	@GetMapping("/{id}")
	public ResolvedSearchProfileDto findById(@PathVariable SearchProfileId id) throws SearchProfileNotExitsException {
		return this.searchProfileApplicationService.getSearchProfile(id);
	}

	@GetMapping("/_search")
	public ResponseEntity<List<SearchProfileResultDto>> findByOwnerUserId(@RequestParam String ownerUserId,
	                                                                      @RequestParam(name = "page", defaultValue = "0") int page,
	                                                                      @RequestParam(name = "size", defaultValue = "20") int size
	) {
		Page<SearchProfileResultDto> resultPage = this.searchProfileApplicationService.getSearchProfiles(ownerUserId, page, size);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(resultPage, "/api/searchProfiles/_search");
		return new ResponseEntity<>(resultPage.getContent(), headers, HttpStatus.OK);
	}

}
