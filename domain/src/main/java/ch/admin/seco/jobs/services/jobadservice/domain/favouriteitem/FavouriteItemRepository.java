package ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

@Transactional(propagation = Propagation.MANDATORY)
public interface FavouriteItemRepository  extends JpaRepository<FavouriteItem, FavouriteItemId> {

    @Query("select i from FavouriteItem i where i.jobAdvertisementId =: jobAdvertisementId and i.ownerId =: ownerId")
    Optional<FavouriteItem> findByJobAdvertisementIdAndOwnerId(JobAdvertisementId jobAdvertisementId, String ownerId);

    @Query("select i from FavouriteItem i where i.ownerId =: ownerId")
    Stream<FavouriteItem> findAllByOwnerId(String ownerId);
}
