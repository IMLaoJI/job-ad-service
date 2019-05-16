package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.SearchProfileApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.SearchProfileNotExitsException;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.CreateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.ResolvedSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileResultDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.UpdateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.util.PaginationUtil;

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
