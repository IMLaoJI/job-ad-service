package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.location;

import ch.admin.seco.jobs.services.jobadservice.application.LocationService;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.GeoPoint;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.springframework.util.StringUtils.hasText;

@Service
public class DefaultLocationService implements LocationService {

    private static final String COUNTRY_ISO_CODE_SWITZERLAND = "CH";

    private static final String COUNTRY_ISO_CODE_LIECHTENSTEIN = "LI";

    private static final Set<String> MANAGED_COUNTRY_CODES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            COUNTRY_ISO_CODE_LIECHTENSTEIN,
            COUNTRY_ISO_CODE_SWITZERLAND
    )));

    private final LocationApiClient locationApiClient;

    @Autowired
    public DefaultLocationService(LocationApiClient locationApiClient) {
        this.locationApiClient = locationApiClient;
    }

    @Override
    public Location enrichCodes(Location location) {
        if (!isManagedLocation(location)) {
            return location;
        }
        return findLocationIfHasPostalCode(location)
                .map(locationResource -> enrichLocationWithLocationResource(location, locationResource))
                .orElse(location);
    }

    @Override
    public boolean isLocationValid(Location location) {
        if (location == null) {
            return false;
        }
        return !isManagedLocation(location) || findLocationIfHasPostalCode(location).isPresent();
    }

    private Optional<LocationResource> findLocationIfHasPostalCode(Location location) {
        return hasText(location.getPostalCode()) ?
                locationApiClient.findLocationByPostalCodeAndCity(location.getPostalCode(), location.getCity())
                : Optional.empty();
    }

    private boolean isManagedLocation(Location location) {
        return location != null
                && MANAGED_COUNTRY_CODES.contains(upperCase(location.getCountryIsoCode()));
    }

    private Location enrichLocationWithLocationResource(Location location, LocationResource resource) {
        return new Location.Builder()
                .setRemarks(location.getRemarks())
                .setCity(location.getCity())
                .setPostalCode(location.getPostalCode())
                .setCommunalCode(resource.getCommunalCode())
                .setRegionCode(resource.getRegionCode())
                .setCantonCode(resource.getCantonCode())
                .setCountryIsoCode(location.getCountryIsoCode())
                .setCoordinates(getGeoPoint(resource))
                .build();
    }

    private GeoPoint getGeoPoint(LocationResource matchingLocationResource) {
        GeoPointResource geoPoint = matchingLocationResource.getGeoPoint();
        return geoPoint == null ? null : new GeoPoint(geoPoint.getLongitude(), geoPoint.getLatitude());
    }
}
