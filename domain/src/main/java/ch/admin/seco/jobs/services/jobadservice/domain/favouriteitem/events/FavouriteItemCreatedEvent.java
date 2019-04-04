package ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;

import static ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events.FavouriteItemEvents.FAVOURITE_ITEM_CREATED;


public class FavouriteItemCreatedEvent extends FavouriteItemEvent {

    public FavouriteItemCreatedEvent(FavouriteItem favouriteItem) {
        super(FAVOURITE_ITEM_CREATED, favouriteItem);
    }
}
