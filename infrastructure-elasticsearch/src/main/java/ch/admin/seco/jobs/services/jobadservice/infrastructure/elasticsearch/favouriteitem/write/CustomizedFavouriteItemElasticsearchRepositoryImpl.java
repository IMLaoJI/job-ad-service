package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write;

import java.util.List;

public class CustomizedFavouriteItemElasticsearchRepositoryImpl implements CustomizedFavouriteItemElasticsearchRepository {

    @Override
    public FavouriteItemDocument findByIdAndParent(String jobAdvertisementId, String favouriteItemId) {
        return null;
    }

    @Override
    public List<FavouriteItemDocument> findByOwnerAndParentIds(List<String> jobAdvertisementId, String ownerId) {
        return null;
    }
}
