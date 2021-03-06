package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events.FavouriteItemEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events.FavouriteItemEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Component("favourite-item-event-listener")
public class IndexerEventListener {

    private static Logger LOG = LoggerFactory.getLogger(IndexerEventListener.class);

    private FavouriteItemRepository favouriteItemRepository;

    private FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository;

    public IndexerEventListener(FavouriteItemRepository favouriteItemRepository,
                                FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository) {
        this.favouriteItemRepository = favouriteItemRepository;
        this.favouriteItemElasticsearchRepository = favouriteItemElasticsearchRepository;
    }

    @TransactionalEventListener
    public void handle(FavouriteItemEvent event) {
        if (event.getDomainEventType() == FavouriteItemEvents.FAVOURITE_ITEM_DELETED.getDomainEventType()) {
            this.favouriteItemElasticsearchRepository.deleteById(event.getJobAdvertisementId(), event.getAggregateId());
        } else {
            indexFavouriteItem(event);
        }
    }

    private void indexFavouriteItem(FavouriteItemEvent event) {
        Optional<FavouriteItem> favouriteItemOptional = this.favouriteItemRepository.findById(event.getAggregateId());
        if (favouriteItemOptional.isPresent()) {
            FavouriteItem favouriteItem = favouriteItemOptional.get();
            this.favouriteItemElasticsearchRepository.save(new FavouriteItemDocument(favouriteItem));
        } else {
            LOG.warn("FavouriteItem not found for the given id: {}", event.getAggregateId());
        }
    }
}
