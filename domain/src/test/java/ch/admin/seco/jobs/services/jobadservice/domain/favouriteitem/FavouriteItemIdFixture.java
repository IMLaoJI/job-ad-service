package ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;

public enum FavouriteItemIdFixture {
    favItem01, favItem02, favItem03, favItem04, favItem05, favItem06, favItem07, favItem08, favItem09, favItem10,
    favItem11, favItem12, favItem13, favItem14, favItem15, favItem16, favItem17, favItem18, favItem19, favItem20;

    private final FavouriteItemId id;

    FavouriteItemIdFixture() {
        this.id = new FavouriteItemId(name());
    }

    public FavouriteItemId id() {
        return id;
    }
}
