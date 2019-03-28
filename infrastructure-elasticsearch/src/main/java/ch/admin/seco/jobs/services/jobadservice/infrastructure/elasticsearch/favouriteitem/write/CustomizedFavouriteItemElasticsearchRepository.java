package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write;

import java.util.List;

public interface CustomizedFavouriteItemElasticsearchRepository {

    FavouriteItemDocument findByIdAndParent(String jobAdvertisementId, String favouriteItemId);

    List<FavouriteItemDocument> findByOwnerAndParentIds(List<String> jobAdvertisementId, String ownerId);

}
