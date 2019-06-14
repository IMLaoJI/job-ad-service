package ch.admin.seco.jobs.services.jobadservice.application.complaint;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ComplaintDto {

    @NotBlank
    private String jobAdvertisementId;

    @Valid
    @NotNull
    private ContactInformationDto contactInformation;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private ComplaintType complaintType;

    @NotBlank
    @Size(max = 1000)
    private String complaintMessage;


    public String getJobAdvertisementId() {
        return jobAdvertisementId;
    }

    public ComplaintDto setJobAdvertisementId(String jobAdvertisementId) {
        this.jobAdvertisementId = jobAdvertisementId;
        return this;
    }

    public ContactInformationDto getContactInformation() {
        return contactInformation;
    }

    public ComplaintDto setContactInformation(ContactInformationDto contactInformation) {
        this.contactInformation = contactInformation;
        return this;
    }

    public ComplaintType getComplaintType() {
        return complaintType;
    }

    public ComplaintDto setComplaintType(ComplaintType complaintType) {
        this.complaintType = complaintType;
        return this;
    }

    public String getComplaintMessage() {
        return complaintMessage;
    }

    public ComplaintDto setComplaintMessage(String complaintMessage) {
        this.complaintMessage = complaintMessage;
        return this;
    }

    @Override
    public String toString() {
        return "ComplaintDto{" +
                "jobAdvertisementId='" + jobAdvertisementId + '\'' +
                ", contactInformation=" + contactInformation + '\'' +
                ", complaintType=" + complaintType + '\'' +
                ", complaintMessage='" + complaintMessage + '\'' +
                '}';
    }
}
