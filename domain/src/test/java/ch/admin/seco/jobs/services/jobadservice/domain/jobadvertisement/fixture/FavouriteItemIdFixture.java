package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;

public enum FavouriteItemIdFixture {
    FAV_ID_1, FAV_ID_2, FAV_ID_3, FAV_ID_4, FAV_ID_5, UNKNOWN;

    private final FavouriteItemId id;

    FavouriteItemIdFixture() {
        this.id = new FavouriteItemId(name());
    }

    public FavouriteItemId id() {
        return id;
    }
}
