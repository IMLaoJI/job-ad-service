package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write;

import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.join.query.HasParentQueryBuilder;
import org.elasticsearch.join.query.ParentIdQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.TYPE_DOC;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument.JOB_ADVERTISEMENT_PARENT_RELATION_NAME;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

@Repository
public class FavouriteItemElasticsearchRepositoryImpl implements FavouriteItemElasticsearchRepository {

    private final ElasticsearchTemplate elasticsearchTemplate;

    private final ObjectMapper objectMapper;

    public FavouriteItemElasticsearchRepositoryImpl(ElasticsearchTemplate elasticsearchTemplate, ObjectMapper objectMapper) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public FavouriteItemDocument save(FavouriteItemDocument favouriteItemDocument) {
        UpdateRequest updateRequest = new UpdateRequest(ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT, TYPE_DOC, favouriteItemDocument.getId());
        updateRequest.routing(favouriteItemDocument.getJobAdvertisementRelations().getParent());
        try {
            String json = this.objectMapper.writeValueAsString(favouriteItemDocument);
            updateRequest.doc(json, XContentType.JSON);
            UpdateQuery updateQuery = new UpdateQuery();
            updateQuery.setUpdateRequest(updateRequest);
            updateQuery.setClazz(FavouriteItemDocument.class);
            updateQuery.setId(favouriteItemDocument.getId());
            updateQuery.setIndexName(ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT);
            updateQuery.setType(TYPE_DOC);
            updateQuery.setDoUpsert(true);
            elasticsearchTemplate.update(updateQuery);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return favouriteItemDocument;
    }

    @Override
    public List<FavouriteItemDocument> findByParent(String jobAdvertisementId) {
        SearchQuery searchFavouriteQuery = new NativeSearchQueryBuilder()
                .withQuery(new ParentIdQueryBuilder(FavouriteItemDocument.FAVOURITE_ITEM_RELATION_NAME, jobAdvertisementId))
                .build();
        return this.elasticsearchTemplate.queryForList(searchFavouriteQuery, FavouriteItemDocument.class);
    }

    @Override
    public Optional<FavouriteItemDocument> findByIdAndParent(String jobAdvertisementId, String favouriteItemId) {
        BoolQueryBuilder parentId = boolQuery()
                .must(new ParentIdQueryBuilder(FavouriteItemDocument.FAVOURITE_ITEM_RELATION_NAME, jobAdvertisementId))
                .must(QueryBuilders.termQuery("_id", favouriteItemId));
        SearchQuery searchFavouriteQuery = new NativeSearchQueryBuilder()
                .withQuery(parentId)
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
                .must(QueryBuilders.termQuery("favouriteItem.ownerId", ownerId.toLowerCase()));

        SearchQuery searchFavouriteQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .build();
        return this.elasticsearchTemplate.queryForList(searchFavouriteQuery, FavouriteItemDocument.class);
    }

    @Override
    public void deleteById(String favouriteItemId) {
        // TODO DELETE WITH PARENT ID
    }

    @Override
    public long count() {
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(new HasParentQueryBuilder(JOB_ADVERTISEMENT_PARENT_RELATION_NAME, matchAllQuery(), false))
                .build();
        return this.elasticsearchTemplate.count(query, FavouriteItemDocument.class);
    }

    @Override
    public void deleteAll() {
        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder(this.elasticsearchTemplate.getClient())
                        .filter(new HasParentQueryBuilder(JOB_ADVERTISEMENT_PARENT_RELATION_NAME, matchAllQuery(), false))
                        .source(ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT)
                        .refresh(true)
                        .get();
        // TODO LOG ME
        System.out.println(response.getDeleted());
    }

    @Override
    public Iterable<FavouriteItemDocument> saveAll(Iterable<FavouriteItemDocument> entities) {
        // TODO use bulk?
        entities.forEach(this::save);
        return entities;
    }

    private QueryBuilder matchesJobAdvertisementIds(List<String> jobAdvertisementIds) {
        return QueryBuilders.termsQuery("_id", jobAdvertisementIds.toArray(new String[0]));
    }
}
