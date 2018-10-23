package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.ValueObject;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenterAddress;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.Valid;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class Company implements ValueObject<Company> {

    @Embedded
    @Valid
    private Address address = new Address();

    // TODO @Size(max = 2)
    private String phone;

    private String email;

    private String website;

    private boolean surrogate;

    protected Company() {
        // For reflection libs
    }

    private Company(Builder builder) {
        this.address = new Address.Builder()
                .setName(Condition.notBlank(builder.name, "Name of a company can't be null"))
                .setStreet(builder.street)
                .setHouseNumber(builder.houseNumber)
                .setPostalCode(builder.postalCode)
                .setCity(builder.city)
                //.setCountryIsoCode(ondition.notBlank(builder.countryIsoCode, "Country of a company can't be null"))
                //todo: Review the countryIsoCode, because most of the jobs from x28 do not have country.
                .setCountryIsoCode(builder.countryIsoCode)
                .setPostOfficeBoxNumber(builder.postOfficeBoxNumber)
                .setPostOfficeBoxPostalCode(builder.postOfficeBoxPostalCode)
                .setPostOfficeBoxCity(builder.postOfficeBoxCity)
                .build();
        this.phone = builder.phone;
        this.email = builder.email;
        this.website = builder.website;
        this.surrogate = builder.surrogate;
    }

    public String getName() {
        return address.getName();
    }

    public String getStreet() {
        return address.getStreet();
    }

    public String getHouseNumber() {
        return address.getHouseNumber();
    }

    public String getPostalCode() {
        return address.getPostalCode();
    }

    public String getCity() {
        return address.getCity();
    }

    public String getPostOfficeBoxNumber() {
        return address.getPostOfficeBoxNumber();
    }

    public String getPostOfficeBoxPostalCode() {
        return address.getPostOfficeBoxPostalCode();
    }

    public String getPostOfficeBoxCity() {
        return address.getPostOfficeBoxCity();
    }

    public String getCountryIsoCode() {
        return address.getCountryIsoCode();
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public boolean isSurrogate() {
        return surrogate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Company company = (Company) o;
        return Objects.equals(address, company.address) &&
                Objects.equals(phone, company.phone) &&
                Objects.equals(email, company.email) &&
                Objects.equals(website, company.website) &&
                Objects.equals(surrogate, company.surrogate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, phone, email, website, surrogate);
    }

    @Override
    public String toString() {
        return "Company{" +
                "address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                ", surrogate='" + surrogate + '\'' +
                '}';
    }

    public static class Builder<T> {
        private T parentBuilder;
        private String name;
        private String street;
        private String houseNumber;
        private String postalCode;
        private String city;
        private String countryIsoCode;
        private String postOfficeBoxNumber;
        private String postOfficeBoxPostalCode;
        private String postOfficeBoxCity;
        private String phone;
        private String email;
        private String website;
        private boolean surrogate;

        public Builder(T parentBuilder) {
            this.parentBuilder = parentBuilder;
        }

        public Builder() {
        }

        public Builder(JobCenter jobCenter) {
            JobCenterAddress jobCenterAddress = jobCenter.getAddress();
            this.setName(jobCenterAddress.getName())
                    .setStreet(jobCenterAddress.getStreet())
                    .setHouseNumber(jobCenterAddress.getHouseNumber())
                    .setPostalCode(jobCenterAddress.getZipCode())
                    .setCity(jobCenterAddress.getCity())
                    .setCountryIsoCode("CH")
                    .setSurrogate(true);

            if (jobCenter.isShowContactDetailsToPublic()) {
                this.setPhone(jobCenter.getPhone())
                        .setEmail(jobCenter.getEmail());
            }
        }

        public Company build() {
            return new Company(this);
        }

        public T end() {
            return Condition.notNull(parentBuilder, "No parentBuilder has been set");
        }

        public Builder<T> setName(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> setStreet(String street) {
            this.street = street;
            return this;
        }

        public Builder<T> setHouseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
            return this;
        }

        public Builder<T> setPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder<T> setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder<T> setCountryIsoCode(String countryIsoCode) {
            this.countryIsoCode = countryIsoCode;
            return this;
        }

        public Builder<T> setPostOfficeBoxNumber(String postOfficeBoxNumber) {
            this.postOfficeBoxNumber = postOfficeBoxNumber;
            return this;
        }

        public Builder<T> setPostOfficeBoxPostalCode(String postOfficeBoxPostalCode) {
            this.postOfficeBoxPostalCode = postOfficeBoxPostalCode;
            return this;
        }

        public Builder<T> setPostOfficeBoxCity(String postOfficeBoxCity) {
            this.postOfficeBoxCity = postOfficeBoxCity;
            return this;
        }

        public Builder<T> setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder<T> setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder<T> setWebsite(String website) {
            this.website = website;
            return this;
        }

        public Builder<T> setSurrogate(boolean surrogate) {
            this.surrogate = surrogate;
            return this;
        }
    }
}
