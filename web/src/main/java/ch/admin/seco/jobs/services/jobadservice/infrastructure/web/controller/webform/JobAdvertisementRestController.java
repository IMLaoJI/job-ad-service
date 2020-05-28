package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.webform;

import ch.admin.seco.jobs.services.jobadservice.application.IsSysAdmin;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.CancellationDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.EventData;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.EventStore;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.CancellationResource;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.PageResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/jobAdvertisements")
public class JobAdvertisementRestController {

    private final JobAdvertisementApplicationService jobAdvertisementApplicationService;

    private final EventStore eventStore;

    private final JobAdvertisementFromWebAssembler jobAdvertisementFromWebAssembler;


    public JobAdvertisementRestController(JobAdvertisementApplicationService jobAdvertisementApplicationService, EventStore eventStore, JobAdvertisementFromWebAssembler jobAdvertisementFromWebAssembler) {
        this.jobAdvertisementApplicationService = jobAdvertisementApplicationService;
        this.eventStore = eventStore;
        this.jobAdvertisementFromWebAssembler = jobAdvertisementFromWebAssembler;
    }

    /**
     * Response status:
     * - 201 Created: The job ad has been successfully created
     * - 400 Bad Request: The request was malformed or invalid
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public JobAdvertisementDto createFromWebform(@RequestBody @Valid WebformCreateJobAdvertisementDto createJobAdvertisementFromWebDto) throws AggregateNotFoundException {
        CreateJobAdvertisementDto createJobAdvertisementDto = jobAdvertisementFromWebAssembler.convert(createJobAdvertisementFromWebDto);
        JobAdvertisementId jobAdvertisementId = jobAdvertisementApplicationService.createFromWebForm(createJobAdvertisementDto);
        return jobAdvertisementApplicationService.getById(jobAdvertisementId);
    }

    /**
     * Response status:
     * - 200 Ok: The page with job ads has been returned
     */
    @GetMapping
    public PageResource<JobAdvertisementDto> getAll(@RequestParam(name = "page", defaultValue = "0") int page,
                                                    @RequestParam(name = "size", defaultValue = "25") int size) {
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdTime")));
        return PageResource.of(jobAdvertisementApplicationService.findAllPaginated(pageRequest));
    }

    /**
     * Response status:
     * - 200 Ok: The job ad has been returned
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     * - 404 Not Found: No job ad has be found for the given id
     */
    @GetMapping("/{id}")
    public JobAdvertisementDto getOne(@PathVariable String id) throws AggregateNotFoundException {
        final JobAdvertisementDto jobAdvertisementDto = jobAdvertisementApplicationService.getById(new JobAdvertisementId(id));
        return jobAdvertisementDto;
    }

    /**
     * Response status:
     * - 200 Ok: The job ad has been returned
     * - 404 Not Found: No job ad has be found for the given accessToken
     */
    @GetMapping("/token/{accessToken}")
    public JobAdvertisementDto getOneByAccessToken(@PathVariable String accessToken) throws AggregateNotFoundException {
        return jobAdvertisementApplicationService.getByAccessToken(accessToken);
    }

    /**
     * Response status:
     * - 200 Ok: The job ad has been returned
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     * - 404 Not Found: No job ad has be found for the given stellennummerEgov
     */
    @GetMapping("/byStellennummerEgov/{stellennummerEgov}")
    public JobAdvertisementDto getOneByStellennummerEgov(@PathVariable String stellennummerEgov) throws AggregateNotFoundException {
        return jobAdvertisementApplicationService.getByStellennummerEgov(stellennummerEgov);
    }

    /**
     * Response status:
     * - 200 Ok: The job ad has been returned
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     * - 404 Not Found: No job ad has be found for the given stellennummerAvam
     */
    @GetMapping("/byStellennummerAvam/{stellennummerAvam}")
    public JobAdvertisementDto getOneByStellennummerAvam(@PathVariable String stellennummerAvam) throws AggregateNotFoundException {
        return jobAdvertisementApplicationService.getByStellennummerAvam(stellennummerAvam);
    }

    /**
     * Response status:
     * - 200 Ok: The job ad has been returned
     * - 404 Not Found: No job ad has be found for the given fingerprint
     */
    @GetMapping("/byFingerprint/{fingerprint}")
    public JobAdvertisementDto getOneByFingerprint(@PathVariable String fingerprint) {
        return jobAdvertisementApplicationService.getByFingerprint(fingerprint);
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
    @PreAuthorize("@jobAdvertisementAuthorizationService.canCancel(#id, #token)")
    public void cancel(@PathVariable String id, @RequestParam(required = false) String token, @RequestBody CancellationResource cancellation) {
        CancellationDto cancellationDto = jobAdvertisementFromWebAssembler.convert(cancellation);
        jobAdvertisementApplicationService.cancel(new JobAdvertisementId(id), cancellationDto, token);
    }

    /**
     * Response status:
     * - 200 Ok: The page with events has been returned for this job ad
     * - 404 Not Found: No job ad has be found for the given id
     */
    @GetMapping("/{id}/events")
    @IsSysAdmin
    public PageResource<EventData> getEventsOfJobAdvertisement(@PathVariable String id) throws AggregateNotFoundException {
        return PageResource.of(eventStore.findByAggregateId(id, JobAdvertisement.class.getSimpleName(), 0, 100));
    }

    /**
     * Response status:
     * - 204 No Content: The job ad has been successfully updated
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     * - 404 Not Found: No job ad has be found for the given id
     */
    @PostMapping("/{id}/retry/inspect")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @IsSysAdmin
    public void retryInspect(@PathVariable String id) {
        jobAdvertisementApplicationService.inspect(new JobAdvertisementId(id));
    }

    /**
     * Response status:
     * - 204 No Content: The job ad has been successfully updated
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     */
    @GetMapping("/_actions/update-job-centers")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @IsSysAdmin
    public void updateJobCenters() {
        this.jobAdvertisementApplicationService.updateJobCenters();
    }

    /**
     * Response status:
     * - 204 No Content: The job ad has been successfully updated
     * - 401 Unauthorized: User is not logged in
     * - 403 Forbidden: User has not the required permission to perform this action
     * - 404 Not Found: No job center has be found for the given jobCenterCode
     */
    @GetMapping("/_actions/update-job-centers/{jobCenterCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @IsSysAdmin
    public void updateJobCenter(@PathVariable String jobCenterCode) {
        this.jobAdvertisementApplicationService.updateJobCenter(jobCenterCode);
    }

}
