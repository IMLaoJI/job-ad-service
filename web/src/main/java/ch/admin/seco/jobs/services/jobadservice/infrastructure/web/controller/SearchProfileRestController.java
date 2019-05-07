package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.SearchProfileApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.SearchProfileNotExitsException;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileResultDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.create.CreateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.SearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.update.UpdateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.SearchFilter;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.util.PaginationUtil;
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

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    public SearchProfileDto createSearchProfile(@RequestBody @Valid CreateSearchProfileResource createSearchProfileResource)
            throws AggregateNotFoundException {

        CreateSearchProfileDto createSearchProfileDto = new CreateSearchProfileDto(
                createSearchProfileResource.name, createSearchProfileResource.userOwnerId, SearchFilterDto.toDto(createSearchProfileResource.searchFilter)
        );
        return this.searchProfileApplicationService.createSearchProfile(createSearchProfileDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SearchProfileDto updateSearchProfile(@PathVariable SearchProfileId id, @RequestBody UpdateSearchProfileResource updateSearchProfileResource)
            throws SearchProfileNotExitsException {
        UpdateSearchProfileDto updateSearchProfileDto = new UpdateSearchProfileDto(
                id, updateSearchProfileResource.name, SearchFilterDto.toDto(updateSearchProfileResource.searchFilter)
        );
        return this.searchProfileApplicationService.updateSearchProfile(updateSearchProfileDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSearchProfile(@PathVariable SearchProfileId id) throws SearchProfileNotExitsException {
        this.searchProfileApplicationService.deleteSearchProfile(id);
    }

    @GetMapping("/{id}")
    public SearchProfileDto findById(@PathVariable SearchProfileId id) {
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

    static class CreateSearchProfileResource {
        @NotBlank
        @Size(max = 100)
        public String name;

        @NotBlank
        public String userOwnerId;

        @NotNull
        public SearchFilter searchFilter;
    }

    static class UpdateSearchProfileResource {
        @NotBlank
        @Size(max = 100)
        public String name;

        @NotNull
        public SearchFilter searchFilter;
    }
}
