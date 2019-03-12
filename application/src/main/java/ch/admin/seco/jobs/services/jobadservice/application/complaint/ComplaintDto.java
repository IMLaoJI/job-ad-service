package ch.admin.seco.jobs.services.jobadservice.application.complaint;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ComplaintDto {

    @NotBlank
    private String jobAdvertisementId;

    @Valid
    private ContactInformationDto contactInformation;

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
                ", contactInformation=" + contactInformation +
                ", complaintMessage='" + complaintMessage + '\'' +
                '}';
    }
}
