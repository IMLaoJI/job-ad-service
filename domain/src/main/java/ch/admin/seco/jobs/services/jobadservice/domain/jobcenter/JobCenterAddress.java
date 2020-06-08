package ch.admin.seco.jobs.services.jobadservice.domain.jobcenter;

public class JobCenterAddress {

    private String name;
    private String city;
    private String street;
    private String houseNumber;
    private String zipCode;

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private String city;
        private String street;
        private String houseNumber;
        private String zipCode;

        private Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
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

        public Builder setZipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public JobCenterAddress build() {
            JobCenterAddress jobCenterAddress = new JobCenterAddress();
            jobCenterAddress.city = this.city;
            jobCenterAddress.zipCode = this.zipCode;
            jobCenterAddress.street = this.street;
            jobCenterAddress.name = this.name;
            jobCenterAddress.houseNumber = this.houseNumber;
            return jobCenterAddress;
        }
    }
}
