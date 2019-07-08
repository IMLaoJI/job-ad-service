package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement;

import static org.hibernate.jpa.QueryHints.HINT_CACHE_MODE;
import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
public interface JobAdvertisementRepository extends JpaRepository<JobAdvertisement, JobAdvertisementId> {

    @Query("select j from JobAdvertisement j " +
            "where j.status = ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.REFINING and j.publication.startDate <= :currentDate")
    Stream<JobAdvertisement> findAllWherePublicationShouldStart(@Param("currentDate") LocalDate currentDate);

    @Query("select j from JobAdvertisement j " +
            "where j.status = ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_RESTRICTED and j.reportingObligationEndDate < :currentDate")
    Stream<JobAdvertisement> findAllWhereBlackoutNeedToExpire(@Param("currentDate") LocalDate currentDate);

    @Query("select j from JobAdvertisement j " +
            "where j.status = ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_PUBLIC and j.publication.endDate < :currentDate")
    Stream<JobAdvertisement> findAllWherePublicationNeedToExpire(@Param("currentDate") LocalDate currentDate);

    @Query("select j from JobAdvertisement j " +
            "where j.sourceSystem = ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem.EXTERN " +
            "and j.status = ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_PUBLIC")
    Stream<JobAdvertisement> findAllPublishedExtern();

    Optional<JobAdvertisement> findByStellennummerAvam(String stellennummerAvam);

    Optional<JobAdvertisement> findByStellennummerEgov(String stellennummerEgov);

    Optional<JobAdvertisement> findByFingerprint(String fingerprint);

    @Query("select j from JobAdvertisement j where j.stellennummerAvam = :stellennummer or j.stellennummerEgov = :stellennummer")
    Optional<JobAdvertisement> findByStellennummerAvamOrStellennummerEgov(@Param("stellennummer") String stellennummer);

    @Query("select j from JobAdvertisement j where j.owner.accessToken = :accessToken")
    Optional<JobAdvertisement> findOneByAccessToken(@Param("accessToken") String accessToken);

    @QueryHints({
            @QueryHint(name = HINT_FETCH_SIZE, value = "1000"),
            @QueryHint(name = HINT_CACHE_MODE, value = "IGNORE")})
    @Query("select j from JobAdvertisement j order by j.publication.startDate desc")
    Stream<JobAdvertisement> streamAll();

    @Query("select j from JobAdvertisement j where j.owner.userId = :userId or j.owner.companyId = :companyId")
    Page<JobAdvertisement> findOwnJobAdvertisements(Pageable pageable, @Param("userId") String userId, @Param("companyId") String companyId);

    @Query("select j from JobAdvertisement j where status in :status and j.owner.userId = :userId or j.owner.companyId = :companyId ")
    Page<JobAdvertisement> findJobAdvertisementsByStatus(Pageable pageable, @Param("userId") String userId, @Param("companyId") String companyId, @Param("status") List<JobAdvertisementStatus> status);

    @Query("select j from JobAdvertisement j where j.jobCenterCode = :jobCenterCode")
    Stream<JobAdvertisement> findByJobCenterCode(@Param("jobCenterCode") String jobCenterCode);
}
