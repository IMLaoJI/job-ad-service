package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.read;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ReadFavouriteItemByJobAdvertisementIdDto {

    @NotBlank
    private String ownerId;

    @NotNull
    private JobAdvertisementId jobAdvertisementId;

    public ReadFavouriteItemByJobAdvertisementIdDto(@NotBlank String ownerId, @NotNull JobAdvertisementId jobAdvertisementId) {
        this.ownerId = ownerId;
        this.jobAdvertisementId = jobAdvertisementId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public JobAdvertisementId getJobAdvertisementId() {
        return jobAdvertisementId;
    }
}
