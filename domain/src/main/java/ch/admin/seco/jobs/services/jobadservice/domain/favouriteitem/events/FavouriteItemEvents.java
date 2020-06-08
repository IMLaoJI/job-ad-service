package ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventType;

public enum FavouriteItemEvents {
    FAVOURITE_ITEM_CREATED(new DomainEventType("FAVOURITE_ITEM_CREATED")),
    FAVOURITE_ITEM_UPDATED(new DomainEventType("FAVOURITE_ITEM_UPDATED")),
    FAVOURITE_ITEM_DELETED(new DomainEventType("FAVOURITE_ITEM_DELETED"));

    private DomainEventType domainEventType;

    FavouriteItemEvents(DomainEventType domainEventType) {
        this.domainEventType = domainEventType;
    }

    public DomainEventType getDomainEventType() {
        return domainEventType;
    }

}
