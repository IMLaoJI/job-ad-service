package ch.admin.seco.jobs.services.jobadservice.application;

import ch.admin.seco.jobs.services.jobadservice.domain.profession.Profession;

import java.util.Optional;

public interface ProfessionService {

    Optional<Profession> findById(String id);

    Profession findByAvamCode(String avamCode);

}
