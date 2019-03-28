package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.HasParentQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument.JOB_ADVERTISEMENT_PARENT_RELATION_NAME;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Component
public class CustomizedFavouriteItemElasticsearchRepositoryImpl implements CustomizedFavouriteItemElasticsearchRepository {

    private final ElasticsearchTemplate elasticsearchTemplate;

    public CustomizedFavouriteItemElasticsearchRepositoryImpl(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Optional<FavouriteItemDocument> findByIdAndParent(String jobAdvertisementId, String favouriteItemId) {
        BoolQueryBuilder boolQueryJob = boolQuery().should(matchQuery("id", jobAdvertisementId));

        BoolQueryBuilder boolQuery = boolQuery()
                .must(new HasParentQueryBuilder(JOB_ADVERTISEMENT_PARENT_RELATION_NAME, boolQueryJob, true))
                .must(QueryBuilders.matchQuery("favouriteItem.id", favouriteItemId));

        SearchQuery searchFavouriteQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .build();

        List<FavouriteItemDocument> favouriteItemDocumentList = this.elasticsearchTemplate.queryForList(searchFavouriteQuery, FavouriteItemDocument.class);
        if (favouriteItemDocumentList.size() == 0) {
            return Optional.empty();
        } else if (favouriteItemDocumentList.size() == 1) {
            return Optional.of(favouriteItemDocumentList.get(0));
        } else {
            throw new NonUniqueResultException();
        }
    }

    @Override
    public List<FavouriteItemDocument> findByOwnerAndParentIds(List<String> jobAdvertisementIds, String ownerId) {

        BoolQueryBuilder boolQuery = boolQuery()
                .must(new HasParentQueryBuilder(JOB_ADVERTISEMENT_PARENT_RELATION_NAME, matchesJobAdvertisementIds(jobAdvertisementIds), true))
                .must(QueryBuilders.matchQuery("favouriteItem.ownerId", ownerId));

        SearchQuery searchFavouriteQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .build();
        return this.elasticsearchTemplate.queryForList(searchFavouriteQuery, FavouriteItemDocument.class);
    }

    private BoolQueryBuilder matchesJobAdvertisementIds(List<String> jobAdvertisementIds) {
        BoolQueryBuilder boolQueryJob = boolQuery();
        for (String jobAdvertisementId : jobAdvertisementIds) {
            boolQueryJob = boolQueryJob.should(matchQuery("id", jobAdvertisementId));
        }
        return boolQueryJob;
    }
}
