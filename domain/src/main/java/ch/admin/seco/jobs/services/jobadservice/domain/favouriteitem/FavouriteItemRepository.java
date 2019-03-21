package ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
public interface FavouriteItemRepository  extends JpaRepository<FavouriteItem, FavouriteItemId> {

    @Query("select i from FavouriteItem i where i.ownerId =: ownerId")
    Page<FavouriteItem> findByIdOwnerId(Pageable pageable, @Param("ownerId") String ownerId);

    @Query("select i from FavouriteItem i where i.jobAdvertisementId.value =: jobAdvertisementId and i.ownerId =: ownerId")
    Page<FavouriteItem> findByJobAdvertisementId(Pageable pageable, JobAdvertisementId jobAdvertisementId, String ownerId);
}
