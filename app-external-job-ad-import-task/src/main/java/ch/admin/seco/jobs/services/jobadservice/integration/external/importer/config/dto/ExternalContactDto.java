package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config.dto;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicContactDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Salutation;

import javax.validation.constraints.Size;

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

    public ExternalContactDto() {
        // For reflection libs
    }


    public Salutation getSalutation() {
        return salutation;
    }

    public ExternalContactDto setSalutation(Salutation salutation) {
        this.salutation = salutation;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public ExternalContactDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public ExternalContactDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public ExternalContactDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ExternalContactDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getLanguageIsoCode() {
        return languageIsoCode;
    }

    public ExternalContactDto setLanguageIsoCode(String languageIsoCode) {
        this.languageIsoCode = languageIsoCode;
        return this;
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

}
