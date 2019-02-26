package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

public class ApiGeoPointDto {

    private double longitude;
    private double latitude;

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public ApiGeoPointDto setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public ApiGeoPointDto setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }
}
