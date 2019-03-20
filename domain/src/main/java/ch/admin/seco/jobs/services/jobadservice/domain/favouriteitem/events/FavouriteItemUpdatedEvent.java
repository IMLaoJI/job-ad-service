package ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;

import static ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events.FavouriteItemEvents.FAVOURITE_ITEM_UPDATED;


public class FavouriteItemUpdatedEvent extends FavouriteItemEvent {

    public FavouriteItemUpdatedEvent(FavouriteItem favouriteItem) {
        super(FAVOURITE_ITEM_UPDATED, favouriteItem);
    }
}
