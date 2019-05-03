package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.Aggregate;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventPublisher;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileUpdatedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.SearchFilter;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
public class SearchProfile implements Aggregate<SearchProfile, SearchProfileId> {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "ID"))
    @Valid
    private SearchProfileId id;

    @NotNull
    private LocalDateTime createdTime;

    @NotNull
    private LocalDateTime updatedTime;

    @NotBlank
    @Size(max = 100) // TODO is there a limitation in size?
    private String name;

    @NotBlank
    private String ownerUserId;

    @Embedded
    @Valid
    @NotNull
    private SearchFilter searchFilter;

    private SearchProfile() {
        // FOR REFLECTION
    }

    private SearchProfile(Builder builder) {
        this.id = Condition.notNull(builder.id);
        this.createdTime = TimeMachine.now();
        this.updatedTime = this.createdTime;
        this.name = Condition.notNull(builder.name);
        this.ownerUserId = Condition.notBlank(builder.ownerUserId);
        this.searchFilter = Condition.notNull(builder.searchFilter);
    }

    public SearchProfileId getId() {
        return id;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public String getName() {
        return name;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public SearchFilter getSearchFilter() {
        return searchFilter;
    }

    public void update(String name, SearchFilter searchFilter) {
        this.name = name;
        this.searchFilter = searchFilter;
        this.updatedTime = TimeMachine.now();
        DomainEventPublisher.publish(new SearchProfileUpdatedEvent(this));
    }

    public static final class Builder {

        private SearchProfileId id;


        private String name;

        private String ownerUserId;

        private SearchFilter searchFilter;

        public Builder() { }

        public SearchProfile build() { return new SearchProfile(this); }

        public  Builder setId(SearchProfileId id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setOwnerUserId(String ownerUserId) {
            this.ownerUserId = ownerUserId;
            return this;
        }

        public Builder setSearchFilter(SearchFilter searchFilter) {
            this.searchFilter = searchFilter;
            return this;
        }
    }

    @Override
    public String toString() {
        return "SearchProfile{" +
                " id=" + id.getValue() + '\'' +
                ",name=" + name + '\'' +
                ",ownerUserId=" + ownerUserId + '\'' +
                "}";
    }
}
