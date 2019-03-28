package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write;

import java.util.List;
import java.util.Optional;

public interface CustomizedFavouriteItemElasticsearchRepository {

    void customSave(FavouriteItemDocument favouriteItemDocument);

    Optional<FavouriteItemDocument> findByIdAndParent(String jobAdvertisementId, String favouriteItemId);

    List<FavouriteItemDocument> findByOwnerAndParentIds(List<String> jobAdvertisementId, String ownerId);

}
