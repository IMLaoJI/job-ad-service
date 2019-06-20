package ch.admin.seco.jobs.services.jobadservice.application.complaint;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Salutation;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Locale;

public class ContactInformationDto {

    @NotNull
    @Enumerated(EnumType.STRING)
    private Salutation salutation;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    private String phone;

    @Email
    @NotBlank
    @Size(max = 255)
    private String email;

    @NotNull
    private Locale contactLanguage;

    public Salutation getSalutation() {
        return salutation;
    }

    public ContactInformationDto setSalutation(Salutation salutation) {
        this.salutation = salutation;
        return this;
    }

    public String getName() {
        return name;
    }

    public ContactInformationDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public ContactInformationDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ContactInformationDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public Locale getContactLanguage() {
        return contactLanguage;
    }

    public ContactInformationDto setContactLanguage(Locale contactLanguage) {
        this.contactLanguage = contactLanguage;
        return this;
    }

    @Override
    public String toString() {
        return "ContactInformationDto{" +
                "salutation=" + salutation +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", contactLanguage=" + contactLanguage +
                '}';
    }
}
