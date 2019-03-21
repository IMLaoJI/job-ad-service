package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.GeoPointDto;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class RadiusSearchRequest {

    @NotNull
    private GeoPointDto geoPoint;

    //default distance unit: kilometers
    @Min(10)
    @NotNull
    private Integer distance;

    public GeoPointDto getGeoPoint() {
        return geoPoint;
    }

    public RadiusSearchRequest setGeoPoint(GeoPointDto geoPoint) {
        this.geoPoint = geoPoint;
        return this;
    }

    public Integer getDistance() {
        return distance;
    }

    public RadiusSearchRequest setDistance(Integer distance) {
        this.distance = distance;
        return this;
    }
}
