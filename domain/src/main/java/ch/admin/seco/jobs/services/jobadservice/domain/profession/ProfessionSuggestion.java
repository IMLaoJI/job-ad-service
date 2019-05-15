package ch.admin.seco.jobs.services.jobadservice.domain.profession;

import java.util.Map;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;

public class ProfessionSuggestion {

    private String id;

    private String code;

    private ProfessionCodeType type;

    private Language language;

    private String label;

    private Map<ProfessionCodeType, String> mappings;

    private ProfessionSuggestion() {
    }

    public ProfessionSuggestion(String id) {
        this.id = Condition.notNull(id);
    }

    public ProfessionSuggestion(String id, String code, ProfessionCodeType type, Language language,
            String label, Map<ProfessionCodeType, String> mappings) {
        this(id);
        this.code = code;
        this.type = type;
        this.language = language;
        this.label = label;
        this.mappings = mappings;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public ProfessionCodeType getType() {
        return type;
    }

    public Language getLanguage() {
        return language;
    }

    public String getLabel() {
        return label;
    }

    public Map<ProfessionCodeType, String> getMappings() {
        return mappings;
    }
}
