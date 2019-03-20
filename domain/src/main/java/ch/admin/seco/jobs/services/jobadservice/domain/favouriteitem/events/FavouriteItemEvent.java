package ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;

public abstract class FavouriteItemEvent extends DomainEvent<FavouriteItemId> {

    private FavouriteItemId favouriteItemId;

    FavouriteItemEvent(FavouriteItemEvents favouriteItemEvent, FavouriteItem favouriteItem) {
        super(favouriteItemEvent.getDomainEventType(), FavouriteItem.class.getSimpleName());
        this.favouriteItemId = favouriteItem.getId();
        additionalAttributes.put("favouriteItemId", favouriteItemId.getValue());
    }

    @Override
    public FavouriteItemId getAggregateId() {
        return this.favouriteItemId;
    }
}
