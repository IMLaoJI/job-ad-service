package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.external;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ExternalMessageLogRepository extends JpaRepository<ExternalJobAdvertisementMessageLog, String> {

    long countByLastMessageDateEquals(LocalDate date);
}
