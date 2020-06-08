package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.profession;

import ch.admin.seco.jobs.services.jobadservice.domain.profession.Language;
import ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType;

import java.util.Map;

public class OccupationLabelSuggestionResource {

    private String id;

    private String code;

    private ProfessionCodeType type;

    private Language language;

    private String classifier;

    private String label;

    private Map<ProfessionCodeType, String> mappings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ProfessionCodeType getType() {
        return type;
    }

    public void setType(ProfessionCodeType type) {
        this.type = type;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<ProfessionCodeType, String> getMappings() {
        return mappings;
    }

    public void setMappings(Map<ProfessionCodeType, String> mappings) {
        this.mappings = mappings;
    }
}
