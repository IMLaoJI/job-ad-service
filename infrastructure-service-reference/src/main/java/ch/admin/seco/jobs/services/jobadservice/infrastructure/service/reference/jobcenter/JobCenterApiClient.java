package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.jobcenter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "referenceservice", contextId = "job-center-api", url = "${feign.referenceservice.url:}", fallback = JobCenterApiClientFallback.class, decode404 = true)
interface JobCenterApiClient {

    @GetMapping(value = "/api/job-centers/by-location")
    JobCenterResource searchJobCenterByLocation(
            @RequestParam("countryCode") String countryCode,
            @RequestParam("postalCode") String postalCode
    );

    @GetMapping(value = "/api/job-centers")
    JobCenterResource searchJobCenterByCode(@RequestParam("code") String code, @RequestParam(name = "language") String language);

    @GetMapping(value = "/api/job-centers")
    List<JobCenterResource> findAllJobCenters();
}
