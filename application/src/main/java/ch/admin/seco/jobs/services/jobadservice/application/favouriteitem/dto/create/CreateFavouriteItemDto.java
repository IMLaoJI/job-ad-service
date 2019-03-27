package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.create;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateFavouriteItemDto {

    @Size(max = 1000)
    private String note;

    @NotBlank
    private String ownerUserId;

    @NotNull
    private JobAdvertisementId jobAdvertisementId;

    public CreateFavouriteItemDto(String note, String ownerUserId, JobAdvertisementId jobAdvertisementId) {
        this.note = note;
        this.ownerUserId = ownerUserId;
        this.jobAdvertisementId = jobAdvertisementId;
    }

    public CreateFavouriteItemDto() {
    }

    public String getNote() {
        return note;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public JobAdvertisementId getJobAdvertisementId() {
        return jobAdvertisementId;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public void setJobAdvertisementId(JobAdvertisementId jobAdvertisementId) {
        this.jobAdvertisementId = jobAdvertisementId;
    }
}
