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
	public Optional<Profession> findByAvamCode(String avamCode) {
		return this.occupationLabelApiClient.getOccupationMapping(ProfessionCodeType.AVAM.name(), avamCode)
				.map(this::toProfession);
	}

	@Override
	public boolean isKnownAvamCode(String avamCode) {
		return occupationLabelApiClient.isKnownAvamCode(avamCode);
	}

	private ProfessionSuggestion toProfessionSuggestion(OccupationLabelSuggestionResource resource) {
		if (resource == null) {
			return null;
		}
		return new ProfessionSuggestion(
				resource.getId(),
				resource.getCode(),
				resource.getType(),
				resource.getLanguage(),
				resource.getLabel(),
				resource.getMappings()
		);
	}

	private Profession toProfession(OccupationLabelMappingResource resource) {
		return new Profession.Builder()
				.setId(new ProfessionId(resource.getAvamCode()))
				.setAvamCode(resource.getAvamCode())
				.setChIsco3Code(resource.getChIsco3Code())
				.setChIsco5Code(resource.getChIsco5Code())
				.setBfsCode(resource.getBfsCode())
				.setLabel(resource.getDescription())
				.build();
	}

}
