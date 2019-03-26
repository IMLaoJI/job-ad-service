package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;

class FavouriteItemAlreadyExists extends RuntimeException {

    FavouriteItemAlreadyExists(FavouriteItemId favouriteItemId, JobAdvertisementId jobAdvertisementId, String ownerid) {
        super("FavouriteItem couldn't be created. User: '" + ownerid + "' already has a FavouriteItem: '" + favouriteItemId.getValue() + "' for JobAdvertismentId: '" + jobAdvertisementId.getValue() + "'");
    }
}
