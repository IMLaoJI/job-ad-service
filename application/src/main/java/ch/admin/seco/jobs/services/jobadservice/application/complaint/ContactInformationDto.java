package ch.admin.seco.jobs.services.jobadservice.application.complaint;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Salutation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ContactInformationDto {

    @NotNull
    private Salutation salutation;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String phone;

    @Email
    @NotBlank
    private String email;

    public Salutation getSalutation() {
        return salutation;
    }

    public ContactInformationDto setSalutation(Salutation salutation) {
        this.salutation = salutation;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public ContactInformationDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public ContactInformationDto setLastName(String lastName) {
        this.lastName = lastName;
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

    @Override
    public String toString() {
        return "ContactInformationDto{" +
                "salutation=" + salutation +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
