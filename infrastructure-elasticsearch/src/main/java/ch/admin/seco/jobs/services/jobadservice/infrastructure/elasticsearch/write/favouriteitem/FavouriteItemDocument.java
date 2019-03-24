package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.ChildRelation;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.ElasticsearchIndexService.TYPE_JOB_ADVERTISEMENT;

@Document(indexName = INDEX_NAME_JOB_ADVERTISEMENT, type = TYPE_JOB_ADVERTISEMENT, shards = 1, replicas = 0)
@Mapping(mappingPath = "config/elasticsearch/mappings/job-advertisement.json")
@Setting(settingPath = "config/elasticsearch/settings/folding-analyzer.json")
public class FavouriteItemDocument {

    public static final String FAVOURITE_ITEM = "favouriteItem";

    @Id
    private String id;

    private FavouriteItem favouriteItem;

    private ChildRelation childRelation;

    protected FavouriteItemDocument() {}


    public FavouriteItemDocument(FavouriteItem favouriteItem) {
        this.favouriteItem = favouriteItem;
        this.id = favouriteItem.getId().getValue();
        this.childRelation = new ChildRelation.Builder()
                .setName(FAVOURITE_ITEM)
                .setParent(favouriteItem.getJobAdvertisementId().getValue())
                .build();
    }

    public String getId() {
        return id;
    }

    public FavouriteItem getFavouriteItem() {
        return favouriteItem;
    }

    public ChildRelation getChildRelation() {
        return childRelation;
    }
}
