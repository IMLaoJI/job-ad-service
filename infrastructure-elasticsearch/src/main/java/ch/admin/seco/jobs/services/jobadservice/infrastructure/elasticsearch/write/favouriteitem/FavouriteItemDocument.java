package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.ElasticsearchIndexService.INDEX_NAME_FAVOURITE_ITEM;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.ElasticsearchIndexService.TYPE_FAVOURITE_ITEM;

@Document(indexName = INDEX_NAME_FAVOURITE_ITEM, type = TYPE_FAVOURITE_ITEM)
@Mapping(mappingPath = "config/elasticsearch/mappings/favourite-item.json")
@Setting(settingPath = "config/elasticsearch/settings/folding-analyzer.json")
public class FavouriteItemDocument {

    @Id
    private String id;

    private FavouriteItem favouriteItem;

    protected FavouriteItemDocument() {}

    public FavouriteItemDocument(FavouriteItem favouriteItem) {
        this.favouriteItem = favouriteItem;
        this.id = favouriteItem.getId().getValue();
    }

    public String getId() {
        return id;
    }

    public FavouriteItem getFavouriteItem() {
        return favouriteItem;
    }
}
