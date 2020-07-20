package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.location;

import ch.admin.seco.jobs.services.jobadservice.application.LocationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LocationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.GeoPoint;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import ch.admin.seco.jobs.services.jobadservice.application.TraceHelper;
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
    public Optional<LocationDto> findById(String id) {
        StopWatch stopWatch = TraceHelper.stopWatch();
        TraceHelper.startTask(".", "this.locationApiClient.getLocationById", stopWatch);

        Optional<LocationDto> locationDto = this.locationApiClient.getLocationById(UUID.fromString(id))
                .map(this::toLocation);
        TraceHelper.stopTask(stopWatch);

        return locationDto;
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
        StopWatch stopWatch = new StopWatch();
        TraceHelper.startTask(".", "locationApiClient.findLocationByPostalCodeAndCity", stopWatch);

        Optional<LocationResource> locationResource = (hasText(location.getPostalCode()) && hasText(location.getCity())) ?
                locationApiClient.findLocationByPostalCodeAndCity(location.getPostalCode(), location.getCity())
                : Optional.empty();
        TraceHelper.stopTask(stopWatch);

        return locationResource;
    }

    private boolean isManagedLocation(Location location) {
        return location != null
                && MANAGED_COUNTRY_CODES.contains(upperCase(location.getCountryIsoCode()));
    }


    private LocationDto toLocation(LocationResource resource) {
        return new LocationDto()
                .setId(resource.getId().toString())
                .setCity(resource.getCity())
                .setPostalCode(resource.getZipCode())
                .setCommunalCode(resource.getCommunalCode())
                .setRegionCode(resource.getRegionCode())
                .setCantonCode(resource.getCantonCode())
                .setCoordinates(toGeoPoint(resource));
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
                .setCoordinates(toGeoPoint(resource))
                .build();
    }

    private GeoPoint toGeoPoint(LocationResource locationResource) {
        GeoPointResource geoPoint = locationResource.getGeoPoint();
        return geoPoint == null ? null : new GeoPoint(geoPoint.getLongitude(), geoPoint.getLatitude());
    }
}
