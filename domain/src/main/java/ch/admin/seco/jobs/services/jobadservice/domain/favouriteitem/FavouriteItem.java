package ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.Aggregate;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventPublisher;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events.FavouriteItemUpdatedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;

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
public class FavouriteItem implements Aggregate<FavouriteItem, FavouriteItemId> {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "ID"))
    @Valid
    private FavouriteItemId id;

    @NotNull
    private LocalDateTime createdTime;

    @NotNull
    private LocalDateTime updatedTime;

    @Size(max = 1000)
    private String note;

    @NotBlank
    private String ownerId;

    @NotNull
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "JOB_ADVERTISEMENT_ID"))
    private JobAdvertisementId jobAdvertisementId;

    private FavouriteItem(Builder builder) {
        this.id = Condition.notNull(builder.id);
        this.createdTime = TimeMachine.now();
        this.updatedTime = this.createdTime;
        this.note = builder.note;
        this.ownerId = Condition.notBlank(builder.ownerId);
        this.jobAdvertisementId = Condition.notNull(builder.jobAdvertismentId);
    }

    private FavouriteItem() {
        // FOR REFLECTION
    }

    public FavouriteItemId getId() {
        return id;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public String getNote() {
        return note;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public JobAdvertisementId getJobAdvertisementId() {
        return jobAdvertisementId;
    }

    public void update(String note) {
        this.note = note;
        this.updatedTime = TimeMachine.now();
        DomainEventPublisher.publish(new FavouriteItemUpdatedEvent(this));
    }

    public static final class Builder {
        private FavouriteItemId id;
        private LocalDateTime updatedTime;
        private String note;
        private String ownerId;
        private JobAdvertisementId jobAdvertismentId;

        public Builder() {
        }


        public FavouriteItem build() {
            return new FavouriteItem(this);
        }

        public Builder setId(FavouriteItemId id) {
            this.id = id;
            return this;
        }

        public Builder setUpdatedTime(LocalDateTime updatedTime) {
            this.updatedTime = updatedTime;
            return this;
        }

        public LocalDateTime getUpdatedTime() {
            return updatedTime;
        }

        public Builder setNote(String note) {
            this.note = note;
            return this;
        }

        public Builder setOwnerId(String ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder setJobAdvertismentId(JobAdvertisementId jobAdvertismentId) {
            this.jobAdvertismentId = jobAdvertismentId;
            return this;
        }
    }
}
