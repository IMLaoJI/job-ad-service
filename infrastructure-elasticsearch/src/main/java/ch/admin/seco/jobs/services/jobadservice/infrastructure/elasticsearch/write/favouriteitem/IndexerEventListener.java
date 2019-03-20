package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events.FavouriteItemEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component("favourite-item-event-listener")
public class IndexerEventListener {


    @TransactionalEventListener
    public void handle(FavouriteItemEvent event) {
        // TODO DF-831
    }
}
