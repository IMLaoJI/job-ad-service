package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;

@Component
@Transactional
public class FavouriteItemAuthorizationService {

    private final CurrentUserContext currentUserContext;

    private final FavouriteItemRepository favouriteItemRepository;

    public FavouriteItemAuthorizationService(CurrentUserContext currentUserContext, FavouriteItemRepository favouriteItemRepository) {
        this.currentUserContext = currentUserContext;
        this.favouriteItemRepository = favouriteItemRepository;
    }

    public boolean matchesCurrentUserId(String ownerId) {
        String userId = this.currentUserContext.getCurrentUser().getUserId();
        return (userId != null) && userId.equals(ownerId);
    }

    public boolean canUpdate(FavouriteItemId favouriteItemId) {
        Optional<FavouriteItem> byId = this.favouriteItemRepository.findById(favouriteItemId);
        if (!byId.isPresent()) {
            return true;
        }
        FavouriteItem favouriteItem = byId.get();
        return favouriteItem.getOwnerId().equals(this.currentUserContext.getCurrentUser().getUserId());
    }
}
