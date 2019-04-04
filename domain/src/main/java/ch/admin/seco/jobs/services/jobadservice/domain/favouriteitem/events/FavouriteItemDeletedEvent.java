package ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;

public class FavouriteItemDeletedEvent extends FavouriteItemEvent {

    public FavouriteItemDeletedEvent(FavouriteItem favouriteItem) {
        super(FavouriteItemEvents.FAVOURITE_ITEM_DELETED, favouriteItem);
    }

}