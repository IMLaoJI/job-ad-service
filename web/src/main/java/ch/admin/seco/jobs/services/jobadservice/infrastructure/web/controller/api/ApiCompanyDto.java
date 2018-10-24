package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Company;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.CountryIsoCode;

public class ApiCompanyDto {

    @NotBlank
    @Size(max=255)
    private String name;

    @Size(max=50)
    private String street;

    @Size(max=10)
    private String houseNumber;

    @NotBlank
    @Size(max=10)
    private String postalCode;

    @NotBlank
    @Size(max=100)
    private String city;

    @NotBlank
    @CountryIsoCode
    private String countryIsoCode;

    @Size(max=10)
    private String postOfficeBoxNumber;

    @Size(max=10)
    private String postOfficeBoxPostalCode;

    @Size(max=100)
    private String postOfficeBoxCity;

    @Size(max=20)
    private String phone;

    @Size(max=50)
    private String email;

    @Size(max=255)
    private String website;

    private boolean surrogate;

    public String getName() {
        return name;
    }

    public ApiCompanyDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public ApiCompanyDto setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public ApiCompanyDto setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public ApiCompanyDto setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCity() {
        return city;
    }

    public ApiCompanyDto setCity(String city) {
        this.city = city;
        return this;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public ApiCompanyDto setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
        return this;
    }

    public String getPostOfficeBoxNumber() {
        return postOfficeBoxNumber;
    }

    public ApiCompanyDto setPostOfficeBoxNumber(String postOfficeBoxNumber) {
        this.postOfficeBoxNumber = postOfficeBoxNumber;
        return this;
    }

    public String getPostOfficeBoxPostalCode() {
        return postOfficeBoxPostalCode;
    }

    public ApiCompanyDto setPostOfficeBoxPostalCode(String postOfficeBoxPostalCode) {
        this.postOfficeBoxPostalCode = postOfficeBoxPostalCode;
        return this;
    }

    public String getPostOfficeBoxCity() {
        return postOfficeBoxCity;
    }

    public ApiCompanyDto setPostOfficeBoxCity(String postOfficeBoxCity) {
        this.postOfficeBoxCity = postOfficeBoxCity;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public ApiCompanyDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ApiCompanyDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getWebsite() {
        return website;
    }

    public ApiCompanyDto setWebsite(String website) {
        this.website = website;
        return this;
    }

    public boolean isSurrogate() {
        return surrogate;
    }

    public ApiCompanyDto setSurrogate(boolean surrogate) {
        this.surrogate = surrogate;
        return this;
    }
}
