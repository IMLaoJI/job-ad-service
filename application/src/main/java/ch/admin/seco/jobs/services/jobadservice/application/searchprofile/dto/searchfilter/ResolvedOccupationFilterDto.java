package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType;
import ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionSuggestion;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.OccupationFilterType;

public class ResolvedOccupationFilterDto {

	private String id;

	private String code;

	private ProfessionCodeType type;

	private OccupationFilterType filterType;

	private String label;

	private Map<ProfessionCodeType, String> mappings;

	public ResolvedOccupationFilterDto() {
	}

	public String getId() {
		return id;
	}

	public ResolvedOccupationFilterDto setId(String id) {
		this.id = id;
		return this;
	}

	public String getCode() {
		return code;
	}

	public ResolvedOccupationFilterDto setCode(String code) {
		this.code = code;
		return this;
	}

	public ProfessionCodeType getType() {
		return type;
	}

	public ResolvedOccupationFilterDto setType(ProfessionCodeType type) {
		this.type = type;
		this.filterType = isClassification(type);
		return this;
	}

	public OccupationFilterType getFilterType() {
		return filterType;
	}

	public String getLabel() {
		return label;
	}

	public ResolvedOccupationFilterDto setLabel(String label) {
		this.label = label;
		return this;
	}

	public Map<ProfessionCodeType, String> getMappings() {
		return mappings;
	}

	public ResolvedOccupationFilterDto setMappings(Map<ProfessionCodeType, String> mappings) {
		this.mappings = mappings;
		return this;
	}

	public static ResolvedOccupationFilterDto toDto(ProfessionSuggestion suggestion) {
		if (suggestion == null) {
			return null;
		}
		return new ResolvedOccupationFilterDto()
				.setId(suggestion.getId())
				.setCode(suggestion.getCode())
				.setType(suggestion.getType())
				.setLabel(suggestion.getLabel())
				.setMappings(suggestion.getMappings());
	}

	public static List<ResolvedOccupationFilterDto> toDto(List<ProfessionSuggestion> suggestionList) {
		return suggestionList.stream()
				.map(ResolvedOccupationFilterDto::toDto)
				.collect(Collectors.toList());
	}

	private static OccupationFilterType isClassification(final ProfessionCodeType codeType) {
		if (isCHISCO(codeType) ) {
			return OccupationFilterType.CLASSIFICATION;
		}
		return OccupationFilterType.OCCUPATION;
	}


    private static boolean isCHISCO(final ProfessionCodeType codeType) {
        return ProfessionCodeType.CHISCO3.equals(codeType) || ProfessionCodeType.CHISCO5.equals(codeType);
    }
}
