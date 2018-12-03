package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.profession;

import org.springframework.stereotype.Component;

@Component
public class OccupationLabelApiClientFallback implements OccupationLabelApiClient {

    @Override
    public OccupationLabelMappingResource getOccupationMapping(String type, String code) {
        return null;
    }

}
