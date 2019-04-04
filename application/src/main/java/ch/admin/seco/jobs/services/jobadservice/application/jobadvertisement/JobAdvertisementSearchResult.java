package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.FavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;

public class JobAdvertisementSearchResult {

    private JobAdvertisementDto jobAdvertisement;

    private FavouriteItemDto favouriteItem;

    public JobAdvertisementSearchResult(JobAdvertisementDto jobAdvertisement, FavouriteItemDto favouriteItem) {
        this.jobAdvertisement = jobAdvertisement;
        this.favouriteItem = favouriteItem;
    }

    private JobAdvertisementSearchResult() {
    }

    public JobAdvertisementDto getJobAdvertisement() {
        return jobAdvertisement;
    }

    public FavouriteItemDto getFavouriteItem() {
        return favouriteItem;
    }
}
