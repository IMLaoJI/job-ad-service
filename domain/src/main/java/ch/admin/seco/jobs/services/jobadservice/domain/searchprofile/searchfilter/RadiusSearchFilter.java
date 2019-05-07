package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.GeoPoint;

import javax.persistence.*;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class RadiusSearchFilter {

    private GeoPoint geoPoint;

    private Integer distance;

    public RadiusSearchFilter(GeoPoint geoPoint, Integer distance) {
        this.geoPoint = geoPoint;
        this.distance = distance;
    }

    private RadiusSearchFilter(){
        // FOR JPA
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RadiusSearchFilter that = (RadiusSearchFilter) o;
        return Objects.equals(geoPoint, that.geoPoint) &&
                Objects.equals(distance, that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geoPoint, distance);
    }
}
