package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.jobcenter;

import ch.admin.seco.alv.shared.feign.AlvUnauthorizedFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@AlvUnauthorizedFeignClient(name = "referenceservice", contextId = "jobcenter-api", url = "${feign.referenceservice.url:}", path = "/api/job-centers")
interface JobCenterApiClient {

    @GetMapping(value = "/by-location")
    JobCenterResource searchJobCenterByLocation(
            @RequestParam("countryCode") String countryCode,
            @RequestParam("postalCode") String postalCode
    );

    @GetMapping
    JobCenterResource searchJobCenterByCode(@RequestParam("code") String code, @RequestParam(name = "language") String language);

    @GetMapping
    List<JobCenterResource> findAllJobCenters();
}
