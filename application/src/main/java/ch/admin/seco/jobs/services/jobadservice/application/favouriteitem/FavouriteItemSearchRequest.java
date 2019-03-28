package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem;

import javax.validation.constraints.NotNull;

public class FavouriteItemSearchRequest {

    private String id;

    @NotNull
    private String jobAdvertisementId;

    @NotNull
    private String ownerId;

    public String getJobAdvertisementId() {
        return jobAdvertisementId;
    }

    public void setJobAdvertisementId(String jobAdvertisementId) {
        this.jobAdvertisementId = jobAdvertisementId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FavouriteItemSearchRequest{" +
                "id='" + id + '\'' +
                "jobAdvertisementId='" + jobAdvertisementId + '\'' +
                ", ownerId=" +ownerId +
                '}';
    }
}
