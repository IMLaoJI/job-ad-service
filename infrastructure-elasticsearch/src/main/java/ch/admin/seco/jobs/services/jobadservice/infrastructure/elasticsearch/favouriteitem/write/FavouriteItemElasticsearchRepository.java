package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.TYPE_DOC;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument.JOB_ADVERTISEMENT_PARENT_RELATION_NAME;
import static org.elasticsearch.index.query.QueryBuilders.*;

public class FavouriteItemElasticsearchRepository {

    private static Logger LOG = LoggerFactory.getLogger(FavouriteItemElasticsearchRepository.class);

    private final ElasticsearchTemplate elasticsearchTemplate;

    private final ObjectMapper objectMapper;

    public FavouriteItemElasticsearchRepository(ElasticsearchTemplate elasticsearchTemplate, ObjectMapper objectMapper) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.objectMapper = objectMapper;
    }

    public void save(FavouriteItemDocument favouriteItemDocument) {
        final ElasticsearchPersistentEntity persistentEntity = this.elasticsearchTemplate.getPersistentEntityFor(favouriteItemDocument.getClass());
        final String identifier = (String) persistentEntity.getIdentifierAccessor(favouriteItemDocument).getIdentifier();

        Assert.notNull(identifier, "No id for document");

        final UpdateRequest updateRequest = new UpdateRequest(persistentEntity.getIndexName(), persistentEntity.getIndexType(), identifier);
        updateRequest.routing(favouriteItemDocument.getJobAdvertisementRelations().getParent());

        try {
            String json = this.objectMapper.writeValueAsString(favouriteItemDocument);
            updateRequest.doc(json, XContentType.JSON);
            UpdateQuery updateQuery = new UpdateQuery();
            updateQuery.setUpdateRequest(updateRequest);
            updateQuery.setClazz(persistentEntity.getType());
            updateQuery.setId(identifier);
            updateQuery.setIndexName(persistentEntity.getIndexName());
            updateQuery.setType(persistentEntity.getIndexType());
            updateQuery.setDoUpsert(true);
            elasticsearchTemplate.update(updateQuery);
        } catch (IOException e) {
            LOG.error("Indexing {} not successful.", favouriteItemDocument.toString());
            throw new UncheckedIOException(e);
        }
        LOG.debug("Index of {} successfully created or updated.", favouriteItemDocument.toString());

    }

    public List<FavouriteItemDocument> findByParent(JobAdvertisementId jobAdvertisementId) {
        SearchQuery searchFavouriteQuery = new NativeSearchQueryBuilder()
                .withFilter(new ParentIdQueryBuilder(FavouriteItemDocument.FAVOURITE_ITEM_RELATION_NAME, jobAdvertisementId.getValue()))
                .build();
        return this.elasticsearchTemplate.queryForList(searchFavouriteQuery, FavouriteItemDocument.class);
    }

    public Optional<FavouriteItemDocument> findById(JobAdvertisementId jobAdvertisementId, FavouriteItemId favouriteItemId) {
        SearchQuery searchFavouriteQuery = new NativeSearchQueryBuilder()
                .withFilter(boolQuery()
                        .must(new ParentIdQueryBuilder(FavouriteItemDocument.FAVOURITE_ITEM_RELATION_NAME, jobAdvertisementId.getValue()))
                        .must(QueryBuilders.termQuery("_id", favouriteItemId.getValue())))
                .build();
        List<FavouriteItemDocument> favouriteItemDocumentList = this.elasticsearchTemplate.queryForList(searchFavouriteQuery, FavouriteItemDocument.class);
        if (favouriteItemDocumentList.size() == 0) {
            LOG.error("No results found for JobAdvertisement {} and FavouriteItem {}, but should be one.", jobAdvertisementId.getValue(), favouriteItemId.getValue());
            return Optional.empty();
        } else if (favouriteItemDocumentList.size() == 1) {
            return Optional.of(favouriteItemDocumentList.get(0));
        } else {
            LOG.error("Multiple results found for JobAdvertisement {} and FavouriteItem {}, but should be only one.", jobAdvertisementId.getValue(), favouriteItemId.getValue());
            throw new NonUniqueResultException();
        }
    }

    public List<FavouriteItemDocument> findByOwnerAndParentIds(List<JobAdvertisementId> jobAdvertisementIds, String ownerUserId) {
        BoolQueryBuilder boolQuery = boolQuery()
                .must(new HasParentQueryBuilder(JOB_ADVERTISEMENT_PARENT_RELATION_NAME, matchesJobAdvertisementIds(jobAdvertisementIds), true))
                .must(QueryBuilders.termQuery("favouriteItem.ownerUserId", ownerUserId));

        SearchQuery searchFavouriteQuery = new NativeSearchQueryBuilder()
                .withFilter(boolQuery)
                .build();
        return this.elasticsearchTemplate.queryForList(searchFavouriteQuery, FavouriteItemDocument.class);
    }


    public void deleteByParentId(JobAdvertisementId jobAdvertisementId) {
        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder(this.elasticsearchTemplate.getClient())
                        .filter(new HasParentQueryBuilder(JOB_ADVERTISEMENT_PARENT_RELATION_NAME, termQuery("_id", jobAdvertisementId.getValue()), false))
                        .source(ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT)
                        .refresh(true)
                        .get();
        LOG.debug("{} Index(es) deleted for parent jobAdId: {}.", response.getDeleted(), jobAdvertisementId);
    }

    public void deleteById(JobAdvertisementId jobAdvertisementId, FavouriteItemId favouriteItemId) {
        this.elasticsearchTemplate.getClient()
                .prepareDelete()
                .setParent(jobAdvertisementId.getValue())
                .setId(favouriteItemId.getValue())
                .setIndex(ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT)
                .setType(TYPE_DOC)
                .get();
        LOG.debug("Index deleted for id: {} and parent jobAdId: {}.", favouriteItemId, jobAdvertisementId);
    }

    public long count() {
        SearchQuery query = new NativeSearchQueryBuilder()
                .withFilter(new HasParentQueryBuilder(JOB_ADVERTISEMENT_PARENT_RELATION_NAME, matchAllQuery(), false))
                .build();
        return this.elasticsearchTemplate.count(query, FavouriteItemDocument.class);
    }

    public void deleteAll() {
        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder(this.elasticsearchTemplate.getClient())
                        .filter(new HasParentQueryBuilder(JOB_ADVERTISEMENT_PARENT_RELATION_NAME, matchAllQuery(), false))
                        .source(ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT)
                        .refresh(true)
                        .get();
        LOG.debug("{} Index(es) deleted.", response.getDeleted());
    }

    public Iterable<FavouriteItemDocument> saveAll(Iterable<FavouriteItemDocument> entities) {
        entities.forEach(this::save);
        return entities;
    }

    private QueryBuilder matchesJobAdvertisementIds(List<JobAdvertisementId> jobAdvertisementIds) {
        return QueryBuilders.termsQuery("_id", jobAdvertisementIds.stream()
                .map(JobAdvertisementId::getValue)
                .map(String::toLowerCase)
                .collect(Collectors.toList()));
    }
}
