package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.read.jobadvertisement.dto;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.GeoPointDto;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class RadiusSearchDto {

    @NotNull
    private GeoPointDto geoPoint;

    @Min(10)
    @NotNull
    private Integer distance;

    public GeoPointDto getGeoPoint() {
        return geoPoint;
    }

    public RadiusSearchDto setGeoPoint(GeoPointDto geoPoint) {
        this.geoPoint = geoPoint;
        return this;
    }

    public Integer getDistance() {
        return distance;
    }

    public RadiusSearchDto setDistance(Integer distance) {
        this.distance = distance;
        return this;
    }
}
