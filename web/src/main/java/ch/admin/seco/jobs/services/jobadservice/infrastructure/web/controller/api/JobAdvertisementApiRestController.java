package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.CancellationResource;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.PageResource;

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

    @PostMapping
    public ApiJobAdvertisementDto createFromApi(@RequestBody @Valid ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDto) throws AggregateNotFoundException {
        CreateJobAdvertisementDto createJobAdvertisementDto = jobAdvertisementFromApiAssembler.convert(apiCreateJobAdvertisementDto);
        JobAdvertisementId jobAdvertisementId = jobAdvertisementApplicationService.createFromApi(createJobAdvertisementDto);
        return jobAdvertisementToApiAssembler.convert(jobAdvertisementApplicationService.getById(jobAdvertisementId));
    }

    @GetMapping
    public PageResource<ApiJobAdvertisementDto> getJobAdvertisements(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "25") int size
    ) {
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdTime")));
        return PageResource.of(jobAdvertisementToApiAssembler.convertPage(jobAdvertisementApplicationService.findOwnJobAdvertisements(pageRequest)));
    }

    @GetMapping("/{id}")
    public ApiJobAdvertisementDto getJobAdvertisement(
            @PathVariable String id
    ) {
        return jobAdvertisementToApiAssembler.convert(jobAdvertisementApplicationService.getById(new JobAdvertisementId(id)));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}/cancel")
    public void cancel(
            @PathVariable String id,
            @RequestBody CancellationResource cancellation
    ) {
        jobAdvertisementApplicationService.cancel(new JobAdvertisementId(id), TimeMachine.now().toLocalDate(), cancellation.getCode(), SourceSystem.API, null);
    }

}
