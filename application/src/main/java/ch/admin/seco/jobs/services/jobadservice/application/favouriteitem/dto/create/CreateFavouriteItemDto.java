package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.create;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateFavouriteItemDto {

    @Size(max = 1000)
    private String note;

    @NotBlank
    private String ownerId;

    @NotNull
    private JobAdvertisementId jobAdvertisementId;

    public CreateFavouriteItemDto(String note, String ownerId, JobAdvertisementId jobAdvertisementId) {
        this.note = note;
        this.ownerId = ownerId;
        this.jobAdvertisementId = jobAdvertisementId;
    }

    public String getNote() {
        return note;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public JobAdvertisementId getJobAdvertisementId() {
        return jobAdvertisementId;
    }
}
