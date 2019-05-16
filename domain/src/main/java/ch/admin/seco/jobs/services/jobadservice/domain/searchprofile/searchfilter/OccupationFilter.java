package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class OccupationFilter {

    @NotBlank
    private String labelId;

    public OccupationFilter(String labelId) {
        this.labelId = labelId;
    }

    private OccupationFilter() {
        // FOR JPA
    }

    public String getLabelId() {
        return labelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OccupationFilter that = (OccupationFilter) o;
        return Objects.equals(labelId, that.labelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(labelId);
    }
}
