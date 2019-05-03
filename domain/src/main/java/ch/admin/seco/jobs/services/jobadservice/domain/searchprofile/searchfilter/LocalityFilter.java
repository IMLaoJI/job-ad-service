package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class LocalityFilter {

    @NotBlank
    private String localityId;

    public LocalityFilter(@NotBlank String localityId) {
        this.localityId = localityId;
    }

    private LocalityFilter(){
        // FOR JPA
    }

    public String getLocalityId() {
        return localityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalityFilter that = (LocalityFilter) o;
        return Objects.equals(localityId, that.localityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localityId);
    }
}
