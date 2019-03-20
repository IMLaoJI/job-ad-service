package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.update;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class UpdateFavouriteItemDto {

    // TODO may need to change to string since I'm not sure if spring autom. converts it
    private FavouriteItemId id;

    private String note;

    public FavouriteItemId getId() {
        return id;
    }

    public void setId(FavouriteItemId id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
