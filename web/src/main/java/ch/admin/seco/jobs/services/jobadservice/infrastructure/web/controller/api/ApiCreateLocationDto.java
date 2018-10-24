package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.CountryIsoCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ApiCreateLocationDto {

    @Size(max = 50)
    private String remarks;

    @NotBlank
    @Size(max = 50)
    private String city;

    @NotBlank
    @Size(max = 10)
    private String postalCode;

    @NotBlank
    @CountryIsoCode
    private String countryIsoCode;

    public String getRemarks() {
        return remarks;
    }

    public ApiCreateLocationDto setRemarks(String remarks) {
        this.remarks = remarks;
        return this;
    }

    public String getCity() {
        return city;
    }

    public ApiCreateLocationDto setCity(String city) {
        this.city = city;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public ApiCreateLocationDto setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public ApiCreateLocationDto setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
        return this;
    }
}
