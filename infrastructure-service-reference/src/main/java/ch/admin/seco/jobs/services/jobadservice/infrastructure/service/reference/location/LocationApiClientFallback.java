package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class LocationApiClientFallback implements LocationApiClient {

    private static Logger LOG = LoggerFactory.getLogger(LocationApiClientFallback.class);

    @Override
    public LocationResource getLocationById(UUID id) {
        LOG.warn("Fallback active for LocationApiClientFallback.getLocationById(" + id + ")");
        return null;
    }

    @Override
    public Optional<LocationResource> findLocationByPostalCodeAndCity(String zipCode, String city) {
        LOG.warn("Fallback active for LocationApiClientFallback.findLocationByPostalCodeAndCity(" + zipCode + "," + city + ")");
        return Optional.empty();
    }
}
