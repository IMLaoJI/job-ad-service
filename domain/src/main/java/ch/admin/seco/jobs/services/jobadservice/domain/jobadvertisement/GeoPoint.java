package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class GeoPoint {

    private double lon;
    private double lat;

    protected GeoPoint() {
        // For reflection libs
    }

    public GeoPoint(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoPoint geoPoint = (GeoPoint) o;
        return Double.compare(geoPoint.lon, lon) == 0 &&
                Double.compare(geoPoint.lat, lat) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lon, lat);
    }
}
