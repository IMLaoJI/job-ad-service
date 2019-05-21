package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto;

public class GeoPointDto {

    private double lon;

    private double lat;

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public GeoPointDto setLon(double lon) {
        this.lon = lon;
        return this;
    }

    public GeoPointDto setLat(double lat) {
        this.lat = lat;
        return this;
    }

    @Override
    public String toString() {
        return "" + lat + ", " + lon;
    }
}
