package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.update;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;

public class UpdateFavouriteItemDto {

    private FavouriteItemId id;

    private String note;

    public UpdateFavouriteItemDto(FavouriteItemId id, String note) {
        this.id = id;
        this.note = note;
    }

    public FavouriteItemId getId() {
        return id;
    }

    public String getNote() {
        return note;
    }
}
