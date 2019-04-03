package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ChildRelation;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.TYPE_DOC;

@Document(indexName = INDEX_NAME_JOB_ADVERTISEMENT, type = TYPE_DOC)
public class FavouriteItemDocument {

    public static final String FAVOURITE_ITEM_RELATION_NAME = "rel_favouriteItem";

    @Id
    private String id;

    private FavouriteItem favouriteItem;

    private ChildRelation jobAdvertisementRelations;

    private FavouriteItemDocument() {
    }

    public FavouriteItemDocument(FavouriteItem favouriteItem) {
        this.favouriteItem = favouriteItem;
        this.id = favouriteItem.getId().getValue();
        this.jobAdvertisementRelations = new ChildRelation.Builder()
                .setName(FAVOURITE_ITEM_RELATION_NAME)
                .setParent(favouriteItem.getJobAdvertisementId().getValue())
                .build();
    }

    public String getId() {
        return id;
    }

    public FavouriteItem getFavouriteItem() {
        return favouriteItem;
    }

    public ChildRelation getJobAdvertisementRelations() {
        return jobAdvertisementRelations;
    }

    public String toString() {
        return "FavouriteItemDocument{" +
                "id=" + id +
                ", jobAdId=" + favouriteItem.getJobAdvertisementId().getValue() +
                ", ownerUserId=" + favouriteItem.getOwnerUserId() +
                '}';

    }
}
