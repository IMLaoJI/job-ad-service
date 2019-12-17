package ch.admin.seco.jobs.services.jobadservice.domain.jobcenter;


import java.util.UUID;

public class JobCenterUser {
    private UUID id;

    private String code;

    private String email;

    private String phone;

    private String fax;

    private String externalId;

    private String firstName;

    private String lastName;

    private JobCenterAddress address;

    public UUID getId() {
        return id;
    }

    public JobCenterUser setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public JobCenterUser setCode(String code) {
        this.code = code;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public JobCenterUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public JobCenterUser setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getFax() {
        return fax;
    }

    public JobCenterUser setFax(String fax) {
        this.fax = fax;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public JobCenterUser setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public JobCenterUser setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public JobCenterUser setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public JobCenterAddress getAddress() {
        return address;
    }

    public JobCenterUser setAddress(JobCenterAddress address) {
        this.address = address;
        return this;
    }

    @Override
    public String toString() {
        return "JobCenterUser{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", fax='" + fax + '\'' +
                ", externalId='" + externalId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address=" + address +
                '}';
    }
}
