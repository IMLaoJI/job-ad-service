package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventType;

public enum SearchProfileEvents {

    SEARCH_PROFILE_CREATED(new DomainEventType("SEARCH_PROFILE_CREATED")),
    SEARCH_PROFILE_UPDATED(new DomainEventType("SEARCH_PROFILE_UPDATED")),
    SEARCH_PROFILE_DELETED(new DomainEventType("SEARCH_PROFILE_DELETED")),
    JOBALERT_SUBSCRIBED_EVENT(new DomainEventType("JOBALERT_SUBSCRIBED_EVENT")),
    JOBALERT_UNSUBSCRIBED_EVENT(new DomainEventType("JOBALERT_UNSUBSCRIBED_EVENT")),
    JOBALERT_RELEASED_EVENT(new DomainEventType("JOBALERT_RELEASED_EVENT"));

    private DomainEventType domainEventType;

    SearchProfileEvents(DomainEventType domainEventType) {
        this.domainEventType = domainEventType;
    }

    public DomainEventType getDomainEventType() {
        return domainEventType;
    }
}
