package ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional(propagation = Propagation.MANDATORY)
public interface FavouriteItemRepository extends JpaRepository<FavouriteItem, FavouriteItemId> {

    @Transactional(propagation = Propagation.SUPPORTS)
    @Query("select i from FavouriteItem i where i.jobAdvertisementId = :jobAdvertisementId and i.ownerUserId = :ownerUserId")
    Optional<FavouriteItem> findByJobAdvertisementIdAndOwnerId(@Param("jobAdvertisementId") JobAdvertisementId jobAdvertisementId, @Param("ownerUserId") String ownerUserId);

    @Transactional(propagation = Propagation.SUPPORTS)
    @Query("select i from FavouriteItem i where i.jobAdvertisementId in :jobAdvertisementIds and i.ownerUserId = :ownerUserId")
    List<FavouriteItem> findByJobAdvertisementIdsAndOwnerId(@Param("jobAdvertisementIds") Set<JobAdvertisementId> jobAdvertisementIds, @Param("ownerUserId") String ownerUserId);

}
