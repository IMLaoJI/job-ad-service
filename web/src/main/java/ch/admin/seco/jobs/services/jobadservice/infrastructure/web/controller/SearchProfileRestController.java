package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.SearchProfileApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.SearchProfileNotExitsException;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.CreateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.ResolvedSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileResultDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.UpdateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public ResolvedSearchProfileDto createSearchProfile(@RequestBody @Valid CreateSearchProfileDto createSearchProfileDto) throws AggregateNotFoundException {
        return this.searchProfileApplicationService.createSearchProfile(createSearchProfileDto);
    }

    @PutMapping("/{id}")
    public ResolvedSearchProfileDto updateSearchProfile(@PathVariable SearchProfileId id, @RequestBody UpdateSearchProfileDto updateSearchProfileDto)
            throws SearchProfileNotExitsException {
        updateSearchProfileDto.setId(id.getValue());
        return this.searchProfileApplicationService.updateSearchProfile(updateSearchProfileDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSearchProfile(@PathVariable SearchProfileId id) throws SearchProfileNotExitsException {
        this.searchProfileApplicationService.deleteSearchProfile(id);
    }

    @GetMapping("/{id}")
    public ResolvedSearchProfileDto findById(@PathVariable SearchProfileId id) {
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
