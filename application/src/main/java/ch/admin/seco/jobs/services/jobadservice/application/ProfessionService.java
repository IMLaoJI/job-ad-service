package ch.admin.seco.jobs.services.jobadservice.application;

import ch.admin.seco.jobs.services.jobadservice.domain.profession.Profession;
import ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionSuggestion;

import java.util.Optional;

public interface ProfessionService {

    Optional<ProfessionSuggestion> findById(String id);

	Optional<Profession> findByAvamCode(String avamCode);

    boolean isKnownAvamCode(String avamCode);
}
