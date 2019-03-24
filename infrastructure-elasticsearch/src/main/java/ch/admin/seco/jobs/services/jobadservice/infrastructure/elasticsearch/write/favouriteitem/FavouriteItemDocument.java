package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.ElasticsearchIndexService.*;

@Document(indexName = INDEX_NAME_JOB_ADVERTISEMENT, type = TYPE_JOB_ADVERTISEMENT, shards = 1)
@Setting(settingPath = "config/elasticsearch/settings/folding-analyzer.json")
public class FavouriteItemDocument {

    @Id
    private String id;

    private String parentId;

    private FavouriteItem favouriteItem;

    protected FavouriteItemDocument() {}


    public FavouriteItemDocument(FavouriteItem favouriteItem) {
        this.favouriteItem = favouriteItem;
        this.id = favouriteItem.getId().getValue();
        this.parentId = favouriteItem.getJobAdvertisementId().getValue();
    }

    public String getId() {
        return id;
    }

    public FavouriteItem getFavouriteItem() {
        return favouriteItem;
    }

    public String getParentId() {
        return parentId;
    }
}
