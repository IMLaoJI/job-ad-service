package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.location;

import ch.admin.seco.alv.shared.feign.AlvUnauthorizedFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.UUID;

@AlvUnauthorizedFeignClient(name = "referenceservice", contextId = "location-api", url = "${feign.referenceservice.url:}", fallback = LocationApiClientFallback.class, path = "/api/localities" )
public interface LocationApiClient {

    @GetMapping("/{id}")
    LocationResource getLocationById(@PathVariable("id") UUID id);

    @GetMapping
    Optional<LocationResource> findLocationByPostalCodeAndCity(@RequestParam("zipCode") String zipCode, @RequestParam("city") String city);
}

