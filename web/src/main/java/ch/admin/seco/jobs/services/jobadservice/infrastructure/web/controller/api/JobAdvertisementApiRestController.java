package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.CreatedJobAdvertisementIdWithTokenDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.CancellationDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.CancellationResource;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.PageResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/public/jobAdvertisements/v1")
public class JobAdvertisementApiRestController {

    private final JobAdvertisementApplicationService jobAdvertisementApplicationService;

    private final JobAdvertisementFromApiAssembler jobAdvertisementFromApiAssembler;

    private final JobAdvertisementToApiAssembler jobAdvertisementToApiAssembler;

    @Autowired
    public JobAdvertisementApiRestController(JobAdvertisementApplicationService jobAdvertisementApplicationService, JobAdvertisementFromApiAssembler jobAdvertisementFromApiAssembler, JobAdvertisementToApiAssembler jobAdvertisementToApiAssembler) {
        this.jobAdvertisementApplicationService = jobAdvertisementApplicationService;
        this.jobAdvertisementFromApiAssembler = jobAdvertisementFromApiAssembler;
        this.jobAdvertisementToApiAssembler = jobAdvertisementToApiAssembler;
    }

    /**
     * Response status:
     * - 201 Created: The job ad has been successfully created
     * - 400 Bad Request: The request was malformed or invalid
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     */
    @PostMapping
    public ResponseEntity<ApiJobAdvertisementDto> createFromApi(@RequestBody @Valid ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDto) throws AggregateNotFoundException {
        CreateJobAdvertisementDto createJobAdvertisementDto = jobAdvertisementFromApiAssembler.convert(apiCreateJobAdvertisementDto);
        CreatedJobAdvertisementIdWithTokenDto createdJobAdvertisementIdWithTokenDto = jobAdvertisementApplicationService.createFromApi(createJobAdvertisementDto);
        ApiJobAdvertisementDto apiJobAdvertisementDto = jobAdvertisementToApiAssembler.convert(jobAdvertisementApplicationService.getById(new JobAdvertisementId(createdJobAdvertisementIdWithTokenDto.getJobAdvertisementId())));
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", createdJobAdvertisementIdWithTokenDto.getToken());
        return new ResponseEntity<>(apiJobAdvertisementDto, headers, HttpStatus.CREATED);
    }

    /**
     * Response status:
     * - 200 Ok: The page with job ads has been returned
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     */
    @GetMapping
    public PageResource<ApiJobAdvertisementDto> getJobAdvertisements(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "25") int size) {
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdTime")));
        return PageResource.of(jobAdvertisementToApiAssembler.convertPage(jobAdvertisementApplicationService.findOwnJobAdvertisements(pageRequest)));
    }

    /**
     * Response status:
     * - 200 Ok: The page with job ads has been returned
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     */
    @PostMapping("/_search")
    public PageResource<ApiJobAdvertisementDto> postJobAdvertisementsByStatus(@RequestBody @Valid ApiSearchRequestDto apiSearchRequestDto, @RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "25") int size) {
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdTime")));
        return PageResource.of(jobAdvertisementToApiAssembler.convertPage(jobAdvertisementApplicationService.findOwnJobAdvertisementsByStatus(pageRequest, apiSearchRequestDto.getStatus())));
    }

    /**
     * Response status:
     * - 200 Ok: The job ad has been returned
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     * - 404 Not Found: No job ad has be found for the given id
     */
    @GetMapping("/{id}")
    public ApiJobAdvertisementDto getJobAdvertisement(@PathVariable String id) {
        return jobAdvertisementToApiAssembler.convert(jobAdvertisementApplicationService.getById(new JobAdvertisementId(id)));
    }

    /**
     * Response status:
     * - 204 No Content: The job ad has been successfully updated
     * - 400 Bad Request: The request was malformed or invalid
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     * - 404 Not Found: No job ad has be found for the given fingerprint
     */
    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String id, @RequestBody CancellationResource cancellation) {
        CancellationDto cancellationDto = jobAdvertisementFromApiAssembler.convert(cancellation);
        jobAdvertisementApplicationService.cancel(new JobAdvertisementId(id), cancellationDto, null);
    }

}
