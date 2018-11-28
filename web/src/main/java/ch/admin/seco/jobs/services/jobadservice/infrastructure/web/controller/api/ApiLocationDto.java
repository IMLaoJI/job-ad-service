package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.GeoPoint;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.CountryIsoCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class ApiLocationDto {

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

    public String getRemarks() {
        return remarks;
    }

    public ApiLocationDto setRemarks(String remarks) {
        this.remarks = remarks;
        return this;
    }

    public String getCity() {
        return city;
    }

    public ApiLocationDto setCity(String city) {
        this.city = city;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public ApiLocationDto setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCommunalCode() {
        return communalCode;
    }

    public ApiLocationDto setCommunalCode(String communalCode) {
        this.communalCode = communalCode;
        return this;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public ApiLocationDto setRegionCode(String regionCode) {
        this.regionCode = regionCode;
        return this;
    }

    public String getCantonCode() {
        return cantonCode;
    }

    public ApiLocationDto setCantonCode(String cantonCode) {
        this.cantonCode = cantonCode;
        return this;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public ApiLocationDto setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
        return this;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public ApiLocationDto setCoordinates(GeoPoint coordinates) {
        this.coordinates = coordinates;
        return this;
    }
}
