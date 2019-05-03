package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.GeoPoint;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.RadiusSearchFilter;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class RadiusSearchFilterDto {

    private GeoPoint geoPoint;

    private Integer distance;

    public RadiusSearchFilterDto() {}

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public RadiusSearchFilterDto setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
        return this;
    }

    public Integer getDistance() {
        return distance;
    }

    public RadiusSearchFilterDto setDistance(Integer distance) {
        this.distance = distance;
        return this;
    }

    public static RadiusSearchFilterDto toDto(RadiusSearchFilter radiusSearchFilter) {
        if (radiusSearchFilter == null) {
            return null;
        }
        return new RadiusSearchFilterDto()
                .setGeoPoint(radiusSearchFilter.getGeoPoint())
                .setDistance(radiusSearchFilter.getDistance());
    }

    public static List<RadiusSearchFilterDto> toDto(List<RadiusSearchFilter> radiusSearchFilters) {
        if (CollectionUtils.isEmpty(radiusSearchFilters)) {
            return null;
        }
        return radiusSearchFilters.stream().map(RadiusSearchFilterDto::toDto).collect(Collectors.toList());
    }
}
