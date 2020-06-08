package ch.admin.seco.jobs.services.jobadservice.domain.jobcenter;

public class JobCenter {

    private String code;

    private String email;

    private String phone;

    private String fax;

    private ContactDisplayStyle contactDisplayStyle;

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

    public ContactDisplayStyle getContactDisplayStyle() {
        return contactDisplayStyle;
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
        private ContactDisplayStyle contactDisplayStyle;
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

        public Builder setContactDisplayStyle(ContactDisplayStyle contactDisplayStyle) {
            this.contactDisplayStyle = contactDisplayStyle;
            return this;
        }

        public Builder setAddress(JobCenterAddress address) {
            this.address = address;
            return this;
        }

        public JobCenter build() {
            JobCenter jobCenter = new JobCenter();
            jobCenter.code = this.code;
            jobCenter.phone = this.phone;
            jobCenter.fax = this.fax;
            jobCenter.contactDisplayStyle = this.contactDisplayStyle;
            jobCenter.email = this.email;
            jobCenter.address = this.address;
            return jobCenter;
        }
    }
}
