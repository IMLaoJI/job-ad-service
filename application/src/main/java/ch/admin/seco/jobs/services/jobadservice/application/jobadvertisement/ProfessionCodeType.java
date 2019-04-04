package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProfessionCodeType {
    AVAM,
    X28,
    BFS,
    SBN3,
    SBN5;

    @JsonCreator
    public static ProfessionCodeType fromString(String key) {
        for (ProfessionCodeType type : ProfessionCodeType.values()) {
            if (type.name().equalsIgnoreCase(key)) {
                return type;
            }
        }
        return null;
    }
}
