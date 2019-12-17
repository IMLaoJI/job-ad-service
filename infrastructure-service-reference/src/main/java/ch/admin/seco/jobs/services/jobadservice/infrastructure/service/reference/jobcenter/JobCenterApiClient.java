package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.jobcenter;

import ch.admin.seco.alv.shared.feign.AlvUnauthorizedFeignClient;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenterUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@AlvUnauthorizedFeignClient(name = "referenceservice", contextId = "jobcenter-api", url = "${feign.referenceservice.url:}", path = "/api")
interface JobCenterApiClient {

    @GetMapping(value = "/job-centers/by-location")
    JobCenterResource searchJobCenterByLocation(
            @RequestParam("countryCode") String countryCode,
            @RequestParam("postalCode") String postalCode
    );

    @GetMapping(value = "/job-centers")
    JobCenterResource searchJobCenterByCode(@RequestParam("code") String code, @RequestParam(name = "language") String language);

    @GetMapping(value = "/job-centers")
    List<JobCenterResource> findAllJobCenters();

    @GetMapping(value = "/job-center-users/{externalId}")
    Optional<JobCenterUser> findJobCenterUserByJobCenterUseId(@PathVariable("externalId") String externalId);
}
