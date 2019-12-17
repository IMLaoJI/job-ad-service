package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.jobcenter;

import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.ContactDisplayStyle;

public class JobCenterResource {

    private String id;
    private String code;
    private String email;
    private String phone;
    private String fax;
    private ContactDisplayStyle contactDisplayStyle;
    private AddressResource address;

    protected JobCenterResource() {
        // For reflection libs
    }

    public JobCenterResource(String id, String code, String email, String phone, String fax, ContactDisplayStyle contactDisplayStyle,
                             AddressResource address) {
        this.id = id;
        this.code = code;
        this.email = email;
        this.phone = phone;
        this.fax = fax;
        this.contactDisplayStyle = contactDisplayStyle;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }


    public ContactDisplayStyle getContactDisplayStyle() {
        return contactDisplayStyle;
    }

    public void setContactDisplayStyle(ContactDisplayStyle contactDisplayStyle) {
        this.contactDisplayStyle = contactDisplayStyle;
    }

    public AddressResource getAddress() {
        return address;
    }

    public void setAddress(AddressResource address) {
        this.address = address;
    }
}
