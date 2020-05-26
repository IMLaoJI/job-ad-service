package ch.admin.seco.jobs.services.jobadservice.application.complaint;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Locale;

public class ComplaintDto {

    @NotBlank
    private String jobAdvertisementId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ComplaintType complaintType;

    @NotNull
    private Locale locale;



    public String getJobAdvertisementId() {
        return jobAdvertisementId;
    }

    public ComplaintDto setJobAdvertisementId(String jobAdvertisementId) {
        this.jobAdvertisementId = jobAdvertisementId;
        return this;
    }

    public ComplaintType getComplaintType() {
        return complaintType;
    }

    public ComplaintDto setComplaintType(ComplaintType complaintType) {
        this.complaintType = complaintType;
        return this;
    }

    public Locale getLocale() {
        return locale;
    }

    public ComplaintDto setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    @Override
    public String toString() {
        return "ComplaintDto{" +
                "jobAdvertisementId='" + jobAdvertisementId + '\'' +
                ", complaintType=" + complaintType +
                ", locale=" + locale +
                '}';
    }
}
