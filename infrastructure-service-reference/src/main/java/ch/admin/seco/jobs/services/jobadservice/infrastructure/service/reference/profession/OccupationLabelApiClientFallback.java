package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.profession;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class OccupationLabelApiClientFallback implements OccupationLabelApiClient {

    private static Logger LOG = LoggerFactory.getLogger(OccupationLabelApiClientFallback.class);

    @Override
    public OccupationLabelMappingResource getOccupationMapping(String type, String code) {
        LOG.warn("Fallback active for OccupationLabelApiClientFallback.getOccupationMapping(" + type + "," + code + ")");
        return null;
    }

    @Override
    public Optional<OccupationLabelSuggestionResource> getOccupationInfoById(String id) {
        LOG.warn("Fallback active for OccupationLabelApiClientFallback.getOccupationInfoById(" + id + ")");
        return Optional.empty();
    }

}
