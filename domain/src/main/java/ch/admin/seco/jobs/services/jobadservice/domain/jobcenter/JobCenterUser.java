package ch.admin.seco.jobs.services.jobadservice.domain.jobcenter;

public class JobCenterUser {

    private String code;

    private String email;

    private String phone;

    private String fax;

    private String externalId;

    private String firstName;

    private String lastName;

    private JobCenterAddress address;


    public String getCode() {
        return code;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getFax() {
        return fax;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public JobCenterAddress getAddress() {
        return address;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String code;
        private String email;
        private String phone;
        private String fax;
        private String externalId;
        private String firstName;
        private String lastName;
        private JobCenterAddress address;

        private Builder() {
        }

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setFax(String fax) {
            this.fax = fax;
            return this;
        }

        public Builder setExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setAddress(JobCenterAddress address) {
            this.address = address;
            return this;
        }

        public JobCenterUser build() {
            JobCenterUser jobCenterUser = new JobCenterUser();
            jobCenterUser.externalId = this.externalId;
            jobCenterUser.email = this.email;
            jobCenterUser.code = this.code;
            jobCenterUser.phone = this.phone;
            jobCenterUser.firstName = this.firstName;
            jobCenterUser.address = this.address;
            jobCenterUser.lastName = this.lastName;
            jobCenterUser.fax = this.fax;
            return jobCenterUser;
        }
    }
}
