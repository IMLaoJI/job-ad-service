package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;

import java.time.LocalDateTime;

public class FavouriteItemDto {

    private FavouriteItemId id;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String note;

    private String ownerId;

    private JobAdvertisementId jobAdvertisementId;

    private FavouriteItemDto(FavouriteItemId id, LocalDateTime createdTime, LocalDateTime updatedTime, String note, String ownerId, JobAdvertisementId jobAdvertisementId) {
        this.id = id;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.note = note;
        this.ownerId = ownerId;
        this.jobAdvertisementId = jobAdvertisementId;
    }

    public FavouriteItemId getId() {
        return id;
    }

    public void setId(FavouriteItemId id) {
        this.id = id;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public JobAdvertisementId getJobAdvertisementId() {
        return jobAdvertisementId;
    }

    public void setJobAdvertisementId(JobAdvertisementId jobAdvertisementId) {
        this.jobAdvertisementId = jobAdvertisementId;
    }

    public static FavouriteItemDto toDto(FavouriteItem favouriteItem) {
        return new FavouriteItemDto(
                favouriteItem.getId(),
                favouriteItem.getCreatedTime(),
                favouriteItem.getUpdatedTime(),
                favouriteItem.getNote(),
                favouriteItem.getOwnerId(),
                favouriteItem.getJobAdvertisementId());
    }
}
