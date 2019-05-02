package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class OccupationFilter {

    @NotBlank
    private String labelId;

    @Enumerated(EnumType.STRING)
    private OccupationFilterType type;

    public OccupationFilter(String labelId, OccupationFilterType type) {
        this.labelId = labelId;
        this.type = type;
    }

    private OccupationFilter() {
        // FOR JPA
    }

    public String getLabelId() {
        return labelId;
    }

    public OccupationFilterType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OccupationFilter that = (OccupationFilter) o;
        return Objects.equals(labelId, that.labelId) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(labelId, type);
    }
}
