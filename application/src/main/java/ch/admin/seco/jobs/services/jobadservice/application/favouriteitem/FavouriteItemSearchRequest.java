package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem;

public class FavouriteItemSearchRequest {

    private String jobAdvertisementId;

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

    @Override
    public String toString() {
        return "FavouriteItemSearchRequest{" +
                "jobAdvertisementId='" + jobAdvertisementId + '\'' +
                ", ownerId=" +ownerId +
                '}';
    }
}
