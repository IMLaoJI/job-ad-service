package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.webform;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.CountryIsoCode;

import javax.validation.constraints.NotBlank;

public class WebformCreateAddressDto {

    @NotBlank
    private String name;

    private String street;

    private String houseNumber;

    private String postOfficeBoxNumber;

    @NotBlank
    private String postalCode;

    @NotBlank
    private String city;

    @NotBlank
    @CountryIsoCode
    private String countryIsoCode;

    public String getName() {
        return name;
    }

    public WebformCreateAddressDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public WebformCreateAddressDto setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public WebformCreateAddressDto setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public WebformCreateAddressDto setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCity() {
        return city;
    }

    public WebformCreateAddressDto setCity(String city) {
        this.city = city;
        return this;
    }

    public String getPostOfficeBoxNumber() {
        return postOfficeBoxNumber;
    }

    public WebformCreateAddressDto setPostOfficeBoxNumber(String postOfficeBoxNumber) {
        this.postOfficeBoxNumber = postOfficeBoxNumber;
        return this;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public WebformCreateAddressDto setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
        return this;
    }

}
