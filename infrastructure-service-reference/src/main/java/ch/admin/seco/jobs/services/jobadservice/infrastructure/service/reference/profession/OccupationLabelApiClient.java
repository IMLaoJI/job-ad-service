package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.profession;

import ch.admin.seco.alv.shared.feign.AlvUnauthorizedFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@AlvUnauthorizedFeignClient(name = "referenceservice", contextId = "occupation-api", url = "${feign.referenceservice.url:}", fallback = OccupationLabelApiClientFallback.class, path = "/api/occupations")
public interface OccupationLabelApiClient {

    @GetMapping("/label/mapping/{type}/{code}")
    OccupationLabelMappingResource getOccupationMapping(@PathVariable("type") String type, @PathVariable("code") String code);

    @GetMapping("/occupations/label/{id}")
    Optional<OccupationLabelSuggestionResource> getOccupationInfoById(@PathVariable("id") String id);

}

