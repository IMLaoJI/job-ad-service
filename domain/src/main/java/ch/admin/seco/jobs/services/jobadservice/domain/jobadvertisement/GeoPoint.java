package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

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
}
