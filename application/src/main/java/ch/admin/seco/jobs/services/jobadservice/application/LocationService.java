package ch.admin.seco.jobs.services.jobadservice.application;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LocationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Location;

import java.util.Optional;

public interface LocationService {

    Optional<LocationDto> findById(String id);

    Location enrichCodes(Location location);

    boolean isLocationValid(Location location);
}
