package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.profession;

import ch.admin.seco.jobs.services.jobadservice.application.ProfessionService;
import ch.admin.seco.jobs.services.jobadservice.domain.profession.Profession;
import ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType;
import ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionId;
import ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultProfessionService implements ProfessionService {

	private final OccupationLabelApiClient occupationLabelApiClient;

	@Autowired
	public DefaultProfessionService(OccupationLabelApiClient occupationLabelApiClient) {
		this.occupationLabelApiClient = occupationLabelApiClient;
	}

	@Override
	public Optional<ProfessionSuggestion> findById(String id) {
		return this.occupationLabelApiClient.getOccupationInfoById(id)
				.map(this::toProfessionSuggestion);
	}

	@Override
	public Profession findByAvamCode(String avamCode) {
		OccupationLabelMappingResource resource = occupationLabelApiClient.getOccupationMapping(ProfessionCodeType.AVAM.name(), avamCode);
		if (resource != null) {
			return new Profession(
					new ProfessionId(resource.getAvamCode()),
					resource.getAvamCode(),
					resource.getSbn3Code(),
					resource.getSbn5Code(),
					resource.getBfsCode(),
					resource.getDescription()
			);
		}
		return null;
	}

	@Override
	public boolean isValidAvamCode(String avamCode) {
		return occupationLabelApiClient.getOccupationMapping(ProfessionCodeType.AVAM.name(), avamCode) != null;
	}

	private ProfessionSuggestion toProfessionSuggestion(OccupationLabelSuggestionResource resource) {
		if (resource == null) {
			return null;
		}
		return new ProfessionSuggestion(
				resource.getId().toString(),
				resource.getCode(),
				resource.getType(),
				resource.getLanguage(),
				resource.getLabel(),
				resource.getMappings()
		);
	}

}
