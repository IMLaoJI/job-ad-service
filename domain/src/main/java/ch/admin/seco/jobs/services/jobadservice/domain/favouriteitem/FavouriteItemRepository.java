package ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
public interface FavouriteItemRepository  extends JpaRepository<FavouriteItem, FavouriteItemId> {

}
