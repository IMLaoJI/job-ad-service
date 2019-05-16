package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.GeoPoint;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Location;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.CountryIsoCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class LocationDto {

    private String id;

    private String remarks;

    @Size(max = 50)
    private String city;

    @NotEmpty
    private String postalCode;

    private String communalCode;

    private String regionCode;

    private String cantonCode;

    @NotBlank
    @CountryIsoCode
    private String countryIsoCode;

    private GeoPoint coordinates;

    public LocationDto() {
        // For reflection libs
    }

    public LocationDto(String id, String remarks, String city, String postalCode, String communalCode, String regionCode, String cantonCode, String countryIsoCode, GeoPoint coordinates) {
        this.id = id;
        this.remarks = remarks;
        this.city = city;
        this.postalCode = postalCode;
        this.communalCode = communalCode;
        this.regionCode = regionCode;
        this.cantonCode = cantonCode;
        this.countryIsoCode = countryIsoCode;
        this.coordinates = coordinates;
    }

    public String getId() {
        return id;
    }

    public LocationDto setId(String id) {
        this.id = id;
        return this;
    }

    public String getRemarks() {
        return remarks;
    }

    public LocationDto setRemarks(String remarks) {
        this.remarks = remarks;
        return this;
    }

    public String getCity() {
        return city;
    }

    public LocationDto setCity(String city) {
        this.city = city;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public LocationDto setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCommunalCode() {
        return communalCode;
    }

    public LocationDto setCommunalCode(String communalCode) {
        this.communalCode = communalCode;
        return this;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public LocationDto setRegionCode(String regionCode) {
        this.regionCode = regionCode;
        return this;
    }

    public String getCantonCode() {
        return cantonCode;
    }

    public LocationDto setCantonCode(String cantonCode) {
        this.cantonCode = cantonCode;
        return this;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public LocationDto setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
        return this;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public LocationDto setCoordinates(GeoPoint coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public static LocationDto toDto(Location location) {
        if(location == null) {
            return null;
        }

        LocationDto locationDto = new LocationDto();
        locationDto.setRemarks(location.getRemarks());
        locationDto.setCity(location.getCity());
        locationDto.setPostalCode(location.getPostalCode());
        locationDto.setCommunalCode(location.getCommunalCode());
        locationDto.setRegionCode(location.getRegionCode());
        locationDto.setCantonCode(location.getCantonCode());
        locationDto.setCountryIsoCode(location.getCountryIsoCode());
        locationDto.setCoordinates(location.getCoordinates());
        return locationDto;
    }
}
