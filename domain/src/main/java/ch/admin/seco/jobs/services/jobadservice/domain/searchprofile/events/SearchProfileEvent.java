package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;

public abstract class SearchProfileEvent extends DomainEvent<SearchProfileId> {

    private SearchProfileId searchProfileId;

    private String name;

    SearchProfileEvent(SearchProfileEvents searchProfileEventType, SearchProfile searchProfile) {
        super(searchProfileEventType.getDomainEventType(), SearchProfile.class.getSimpleName());
        this.searchProfileId = searchProfile.getId();
        this.name = searchProfile.getName();
    }

    @Override
    public SearchProfileId getAggregateId() {
        return this.searchProfileId;
    }

    private String getName() {
        return this.name;
    }

    private void setSearchProfileId(SearchProfileId searchProfileId) {
        this.searchProfileId = searchProfileId;
        additionalAttributes.put("searchProfileId", searchProfileId.getValue());
    }

    private void setName(String name) {
        this.name = name;
        additionalAttributes.put("name", name);
    }
}
