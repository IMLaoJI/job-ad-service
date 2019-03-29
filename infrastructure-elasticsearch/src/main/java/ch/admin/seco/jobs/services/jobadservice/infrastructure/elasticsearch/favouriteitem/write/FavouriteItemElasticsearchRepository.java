package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write;

import java.util.List;
import java.util.Optional;

public interface FavouriteItemElasticsearchRepository {

    FavouriteItemDocument save(FavouriteItemDocument favouriteItemDocument);

    List<FavouriteItemDocument> findByParent(String jobAdvertisementId);

    Optional<FavouriteItemDocument> findByIdAndParent(String jobAdvertisementId, String favouriteItemId);

    List<FavouriteItemDocument> findByOwnerAndParentIds(List<String> jobAdvertisementId, String ownerId);

    void deleteByParentId(String jobAdvertisementId);

    void deleteById(String jobAdvertisementId, String favouriteItemId);

    long count();

    void deleteAll();

    Iterable<FavouriteItemDocument> saveAll(Iterable<FavouriteItemDocument> entities);
}