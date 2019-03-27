package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;

import java.time.LocalDateTime;

public class FavouriteItemDto {

    private String id;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String note;

    private String ownerUserId;

    private String jobAdvertisementId;

    private FavouriteItemDto(String id, LocalDateTime createdTime, LocalDateTime updatedTime, String note, String ownerUserId, String jobAdvertisementId) {
        this.id = id;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.note = note;
        this.ownerUserId = ownerUserId;
        this.jobAdvertisementId = jobAdvertisementId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getJobAdvertisementId() {
        return jobAdvertisementId;
    }

    public void setJobAdvertisementId(String String) {
        this.jobAdvertisementId = String;
    }

    public static FavouriteItemDto toDto(FavouriteItem favouriteItem) {
        return new FavouriteItemDto(
                favouriteItem.getId().getValue(),
                favouriteItem.getCreatedTime(),
                favouriteItem.getUpdatedTime(),
                favouriteItem.getNote(),
                favouriteItem.getOwnerId(),
                favouriteItem.getJobAdvertisementId().getValue());
    }
}
