package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY)
public interface SearchProfileRepository  extends JpaRepository<SearchProfile, SearchProfileId> {

    @Transactional(propagation = Propagation.SUPPORTS)
    @Query("select sp from SearchProfile sp where sp.name = :name and sp.ownerUserId = :ownerUserId")
    Optional<SearchProfile> findByNameAndOwnerUserId(@Param("name") String name, @Param("ownerUserId") String ownerUserId);

    @Transactional(propagation = Propagation.SUPPORTS)
    @Query("select sp.id, sp.updatedTime, sp.name, sp.ownerUserId from SearchProfile sp where sp.ownerUserId = :ownerUserId")
    List<SearchProfileResult> findAllByOwnerUserId(@Param("ownerUserId") String ownerUserId);
}
