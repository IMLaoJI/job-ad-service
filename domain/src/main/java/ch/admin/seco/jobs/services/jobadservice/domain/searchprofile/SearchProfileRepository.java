package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hibernate.annotations.QueryHints.READ_ONLY;
import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;
import static org.hibernate.jpa.QueryHints.HINT_CACHE_MODE;
import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

@Transactional(propagation = Propagation.MANDATORY)
@Repository
public interface SearchProfileRepository extends JpaRepository<SearchProfile, SearchProfileId> {

    String HINT_FETCH_SIZE_VALUE = "100";

    @Transactional(propagation = Propagation.SUPPORTS)
    @Query("select sp from SearchProfile sp where sp.name = :name and sp.ownerUserId = :ownerUserId")
    Optional<SearchProfile> findByNameAndOwnerUserId(@Param("name") String name, @Param("ownerUserId") String ownerUserId);

    @Transactional(propagation = Propagation.SUPPORTS)
    @Query("select sp from SearchProfile sp where sp.ownerUserId = :ownerUserId")
    Page<SearchProfile> findAllByOwnerUserId(@Param("ownerUserId") String ownerUserId, Pageable pageable);

    @Transactional(propagation = Propagation.SUPPORTS)
    @Query("select sp from SearchProfile sp where sp.ownerUserId = :ownerUserId")
    List<SearchProfile> findAllByOwnerUserId(@Param("ownerUserId") String ownerUserId);

    @Query("select sp from SearchProfile sp where sp.ownerUserId = :ownerUserId and job_alert_email is not null")
    List<SearchProfile> findSearchProfilesWithJobAlertByOwner(String ownerUserId);

    @Query("select sp from SearchProfile sp where job_alert_next_release_at < :now")
    List<SearchProfile> findJobAlertsByNextReleaseAtBefore(LocalDateTime now);

    @QueryHints({
            @QueryHint(name = HINT_FETCH_SIZE, value = HINT_FETCH_SIZE_VALUE),
            @QueryHint(name = HINT_CACHE_MODE, value = "IGNORE"),
            @QueryHint(name = HINT_CACHEABLE, value = "false"),
            @QueryHint(name = READ_ONLY, value = "true")
    })
    @Query("select sp from SearchProfile sp where sp.jobAlert is not null")
    Stream<SearchProfile> streamAllSearchProfilesWithJobAlerts();

    @Query("Select count(sp) from SearchProfile sp where sp.jobAlert is not null")
    Integer countAllWithJobAlerts();

    @Query("select sp from SearchProfile sp where sp.createdTime < :localDateTime")
    List<SearchProfile> findJobAlertsCreatedBefore(LocalDateTime localDateTime);

}
