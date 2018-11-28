package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Address;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.CountryIsoCode;

import javax.validation.constraints.NotBlank;

public class AddressDto {

    @NotBlank
    private String name;

    private String street;

    private String houseNumber;

    @NotBlank
    private String postalCode;

    @NotBlank
    private String city;

    private String postOfficeBoxNumber;

    private String postOfficeBoxPostalCode;

    private String postOfficeBoxCity;

    @NotBlank
    @CountryIsoCode
    private String countryIsoCode;

    public String getName() {
        return name;
    }

    public AddressDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public AddressDto setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public AddressDto setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public AddressDto setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCity() {
        return city;
    }

    public AddressDto setCity(String city) {
        this.city = city;
        return this;
    }

    public String getPostOfficeBoxNumber() {
        return postOfficeBoxNumber;
    }

    public AddressDto setPostOfficeBoxNumber(String postOfficeBoxNumber) {
        this.postOfficeBoxNumber = postOfficeBoxNumber;
        return this;
    }

    public String getPostOfficeBoxPostalCode() {
        return postOfficeBoxPostalCode;
    }

    public AddressDto setPostOfficeBoxPostalCode(String postOfficeBoxPostalCode) {
        this.postOfficeBoxPostalCode = postOfficeBoxPostalCode;
        return this;
    }

    public String getPostOfficeBoxCity() {
        return postOfficeBoxCity;
    }

    public AddressDto setPostOfficeBoxCity(String postOfficeBoxCity) {
        this.postOfficeBoxCity = postOfficeBoxCity;
        return this;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public AddressDto setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
        return this;
    }

    public static AddressDto toDto(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressDto()
                .setName(address.getName())
                .setStreet(address.getStreet())
                .setHouseNumber(address.getHouseNumber())
                .setPostalCode(address.getPostalCode())
                .setCity(address.getCity())
                .setPostOfficeBoxNumber(address.getPostOfficeBoxNumber())
                .setPostOfficeBoxPostalCode(address.getPostOfficeBoxPostalCode())
                .setPostOfficeBoxCity(address.getPostOfficeBoxCity())
                .setCountryIsoCode(address.getCountryIsoCode());
    }
}
