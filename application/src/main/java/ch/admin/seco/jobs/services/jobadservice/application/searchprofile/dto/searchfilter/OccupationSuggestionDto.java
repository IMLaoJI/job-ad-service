package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter;

import ch.admin.seco.jobs.services.jobadservice.domain.profession.Language;
import ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType;
import ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionSuggestion;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.OccupationFilterType;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OccupationSuggestionDto {

    private String id;

    private String code;

    private ProfessionCodeType type;

    private Language language;

    private OccupationFilterType classifier;

    private String label;

    private Map<ProfessionCodeType, String> mappings;

    public OccupationSuggestionDto() {
    }

    public String getId() {
        return id;
    }

    public OccupationSuggestionDto setId(String id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public OccupationSuggestionDto setCode(String code) {
        this.code = code;
        return this;
    }

    public ProfessionCodeType getType() {
        return type;
    }

    public OccupationSuggestionDto setType(ProfessionCodeType type) {
        this.type = type;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public OccupationSuggestionDto setLanguage(Language language) {
        this.language = language;
        return this;
    }

    public OccupationFilterType getClassifier() {
        classifier = isClassification(this.type);
        return classifier;
    }

    public OccupationSuggestionDto setClassifier(OccupationFilterType classifier) {
        this.classifier = classifier;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public OccupationSuggestionDto setLabel(String label) {
        this.label = label;
        return this;
    }

    public Map<ProfessionCodeType, String> getMappings() {
        return mappings;
    }

    public OccupationSuggestionDto setMappings(Map<ProfessionCodeType, String> mappings) {
        this.mappings = mappings;
        return this;
    }

    public static OccupationSuggestionDto toDto(ProfessionSuggestion suggestion) {
        if (suggestion == null) {
            return null;
        }
        return new OccupationSuggestionDto()
                .setId(suggestion.getId())
                .setCode(suggestion.getCode())
                .setType(suggestion.getType())
                .setLanguage(suggestion.getLanguage())
                .setClassifier(isClassification(suggestion.getType()))
                .setLabel(suggestion.getLabel())
                .setMappings(suggestion.getMappings());
    }

    public static List<OccupationSuggestionDto> toDto(List<ProfessionSuggestion> suggestionList) {
        if (CollectionUtils.isEmpty(suggestionList)) {
            return null;
        }
        return suggestionList.stream().map(OccupationSuggestionDto::toDto).collect(Collectors.toList());
    }

    public static OccupationFilterType isClassification(ProfessionCodeType codeType) {
        if (ProfessionCodeType.SBN3.equals(codeType) || ProfessionCodeType.SBN5.equals(codeType)) {
            return OccupationFilterType.CLASSIFICATION;
        }
        return OccupationFilterType.OCCUPATION;
    }
}
