package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.CountryIsoCode;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class Address {

    // TODO @Size(max = 255)
    private String name;

    // TODO @Size(max = 60)
    private String street;

    // TODO @Size(max = 10)
    private String houseNumber;

    // TODO @Size(max = 10)
    private String postalCode;

    // TODO @Size(max = 100)
    private String city;

    // TODO @Size(max = 20)
    private String postOfficeBoxNumber;

    // TODO @Size(max = 10)
    private String postOfficeBoxPostalCode;

    // TODO @Size(max = 100)
    private String postOfficeBoxCity;

    // TODO @Size(max = 2)
    @CountryIsoCode
    private String countryIsoCode;

    protected Address() {
        // For reflection libs
    }

    private Address(Builder builder) {
        this.name = builder.name;
        this.street = builder.street;
        this.houseNumber = builder.houseNumber;
        this.postalCode = builder.postalCode;
        this.city = builder.city;
        this.postOfficeBoxNumber = builder.postOfficeBoxNumber;
        this.postOfficeBoxPostalCode = builder.postOfficeBoxPostalCode;
        this.postOfficeBoxCity = builder.postOfficeBoxCity;
        this.countryIsoCode = builder.countryIsoCode;
    }

    public String getName() {
        return name;
    }

    Address setName(String name) {
        this.name = name;
        return this;
    }

    public String getStreet() {
        return street;
    }

    Address setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    Address setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    Address setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCity() {
        return city;
    }

    Address setCity(String city) {
        this.city = city;
        return this;
    }

    public String getPostOfficeBoxNumber() {
        return postOfficeBoxNumber;
    }

    Address setPostOfficeBoxNumber(String postOfficeBoxNumber) {
        this.postOfficeBoxNumber = postOfficeBoxNumber;
        return this;
    }

    public String getPostOfficeBoxPostalCode() {
        return postOfficeBoxPostalCode;
    }

    Address setPostOfficeBoxPostalCode(String postOfficeBoxPostalCode) {
        this.postOfficeBoxPostalCode = postOfficeBoxPostalCode;
        return this;
    }

    public String getPostOfficeBoxCity() {
        return postOfficeBoxCity;
    }

    Address setPostOfficeBoxCity(String postOfficeBoxCity) {
        this.postOfficeBoxCity = postOfficeBoxCity;
        return this;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    Address setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(name, address.name) &&
                Objects.equals(street, address.street) &&
                Objects.equals(houseNumber, address.houseNumber) &&
                Objects.equals(postalCode, address.postalCode) &&
                Objects.equals(city, address.city) &&
                Objects.equals(postOfficeBoxNumber, address.postOfficeBoxNumber) &&
                Objects.equals(postOfficeBoxPostalCode, address.postOfficeBoxPostalCode) &&
                Objects.equals(postOfficeBoxCity, address.postOfficeBoxCity) &&
                Objects.equals(countryIsoCode, address.countryIsoCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, street, houseNumber, postalCode, city, postOfficeBoxNumber, postOfficeBoxPostalCode, postOfficeBoxCity, countryIsoCode);
    }

    @Override
    public String toString() {
        return "Address{" +
                "name='" + name + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                ", postOfficeBoxNumber='" + postOfficeBoxNumber + '\'' +
                ", postOfficeBoxPostalCode='" + postOfficeBoxPostalCode + '\'' +
                ", postOfficeBoxCity='" + postOfficeBoxCity + '\'' +
                ", countryIsoCode='" + countryIsoCode + '\'' +
                '}';
    }

    public static class Builder {
        private String name;
        private String street;
        private String houseNumber;
        private String postalCode;
        private String city;
        private String postOfficeBoxNumber;
        private String postOfficeBoxPostalCode;
        private String postOfficeBoxCity;
        private String countryIsoCode;

        public Builder() {
        }

        public Address build() {
            return new Address(this);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setStreet(String street) {
            this.street = street;
            return this;
        }

        public Builder setHouseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
            return this;
        }

        public Builder setPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder setCountryIsoCode(String countryIsoCode) {
            this.countryIsoCode = countryIsoCode;
            return this;
        }

        public Builder setPostOfficeBoxNumber(String postOfficeBoxNumber) {
            this.postOfficeBoxNumber = postOfficeBoxNumber;
            return this;
        }

        public Builder setPostOfficeBoxPostalCode(String postOfficeBoxPostalCode) {
            this.postOfficeBoxPostalCode = postOfficeBoxPostalCode;
            return this;
        }

        public Builder setPostOfficeBoxCity(String postOfficeBoxCity) {
            this.postOfficeBoxCity = postOfficeBoxCity;
            return this;
        }

    }
}
