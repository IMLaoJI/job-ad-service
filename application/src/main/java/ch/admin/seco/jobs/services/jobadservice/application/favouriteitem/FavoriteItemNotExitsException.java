package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;

class FavoriteItemNotExitsException extends AggregateNotFoundException {

    FavoriteItemNotExitsException(FavouriteItemId favouriteItemId) {
        super(FavouriteItem.class, favouriteItemId.getValue());
    }
}
