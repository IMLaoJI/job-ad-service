package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external;

import javax.validation.constraints.Size;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicContactDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Salutation;

public class ExternalContactDto {

    private Salutation salutation;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String email;

    @Size(max = 5)
    private String languageIsoCode;

    protected ExternalContactDto() {
        // For reflection libs
    }

    public ExternalContactDto(Salutation salutation, String firstName, String lastName, String phone, String email, String languageIsoCode) {
        this.salutation = salutation;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.languageIsoCode = languageIsoCode;
    }

    ContactDto toContactDto() {
        return new ContactDto()
                .setSalutation(this.salutation)
                .setFirstName(this.firstName)
                .setLastName(this.lastName)
                .setPhone(this.phone)
                .setEmail(this.email)
                .setLanguageIsoCode(this.languageIsoCode);
    }

    PublicContactDto toPublicContactDto() {
        return new PublicContactDto()
                .setSalutation(this.salutation)
                .setFirstName(this.firstName)
                .setLastName(this.lastName)
                .setPhone(this.phone)
                .setEmail(this.email);
    }

    public Salutation getSalutation() {
        return salutation;
    }

    public void setSalutation(Salutation salutation) {
        this.salutation = salutation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguageIsoCode() {
        return languageIsoCode;
    }

    public void setLanguageIsoCode(String languageIsoCode) {
        this.languageIsoCode = languageIsoCode;
    }

}
