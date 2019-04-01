package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementSearchResult;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementSearchService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.ManagedJobAdSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobDescriptionDto;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read.ElasticJobAdvertisementSearchService;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.util.PaginationUtil;
import io.micrometer.core.annotation.Timed;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@RestController
@RequestMapping("/api/jobAdvertisements")
public class JobAdvertisementSearchController {

    private final JobAdvertisementSearchService jobAdvertisementSearchService;

    public JobAdvertisementSearchController(JobAdvertisementSearchService jobAdvertisementSearchService) {
        this.jobAdvertisementSearchService = jobAdvertisementSearchService;
    }

    @PostMapping("/_search")
    @Timed
    public ResponseEntity<List<JobAdvertisementSearchResult>> searchJobs(
            @RequestBody @Valid JobAdvertisementSearchRequest jobAdvertisementSearchRequest,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(defaultValue = "score") ElasticJobAdvertisementSearchService.SearchSort sort
    ) {

        Page<JobAdvertisementSearchResult> resultPage = jobAdvertisementSearchService.search(jobAdvertisementSearchRequest, page, size, sort)
                //todo: Discuss where to put the HTML cleanup. This is suboptimal concerning performance
                .map(this::sanitizeJobDescription);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(resultPage, "/api/_search/jobs");
        return new ResponseEntity<>(resultPage.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/_search/managed")
    @Timed
    public ResponseEntity<List<JobAdvertisementDto>> searchManagedJobAds(
            @RequestBody @Valid ManagedJobAdSearchRequest searchRequest, Pageable pageable) {

        Page<JobAdvertisementDto> resultPage = jobAdvertisementSearchService.searchManagedJobAds(searchRequest, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(resultPage, "/api/jobAdvertisements/_search/managed");
        return new ResponseEntity<>(resultPage.getContent(), headers, HttpStatus.OK);
    }

    private JobAdvertisementSearchResult sanitizeJobDescription(JobAdvertisementSearchResult searchResult) {
        for (JobDescriptionDto jobDescriptionDto : searchResult.getJobAdvertisement().getJobContent().getJobDescriptions()) {
            String sanitizedDescription = "";
            if (hasText(jobDescriptionDto.getDescription())) {
                sanitizedDescription = Jsoup.clean(
                        jobDescriptionDto.getDescription(),
                        "",
                        new Whitelist().addTags("em"),
                        new Document.OutputSettings().prettyPrint(false));
            }
            jobDescriptionDto.setDescription(sanitizedDescription);
        }

        return searchResult;
    }

}
