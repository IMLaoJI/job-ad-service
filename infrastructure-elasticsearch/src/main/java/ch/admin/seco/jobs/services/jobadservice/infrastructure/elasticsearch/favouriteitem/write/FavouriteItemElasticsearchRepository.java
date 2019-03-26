package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FavouriteItemElasticsearchRepository extends ElasticsearchRepository<FavouriteItemDocument, String> {
}
