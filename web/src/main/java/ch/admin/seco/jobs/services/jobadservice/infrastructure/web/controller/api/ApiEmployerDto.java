package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.CountryIsoCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ApiEmployerDto {

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 10)
    private String postalCode;

    @NotBlank
    @Size(max = 100)
    private String city;

    @NotBlank
    @CountryIsoCode
    private String countryIsoCode;

    public String getName() {
        return name;
    }

    public ApiEmployerDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public ApiEmployerDto setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCity() {
        return city;
    }

    public ApiEmployerDto setCity(String city) {
        this.city = city;
        return this;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public ApiEmployerDto setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
        return this;
    }
}
