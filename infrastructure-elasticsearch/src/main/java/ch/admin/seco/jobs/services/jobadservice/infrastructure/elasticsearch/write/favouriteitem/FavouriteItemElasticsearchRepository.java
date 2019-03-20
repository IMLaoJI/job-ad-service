package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.favouriteitem;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FavouriteItemElasticsearchRepository extends ElasticsearchRepository<FavouriteItemDocument, String> {
}
