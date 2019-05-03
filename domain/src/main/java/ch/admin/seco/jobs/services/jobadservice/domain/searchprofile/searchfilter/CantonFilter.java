package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class CantonFilter {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    public CantonFilter(@NotBlank String name, @NotBlank String code) {
        this.name = name;
        this.code = code;
    }

    private CantonFilter(){
        // FOR JPA
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CantonFilter that = (CantonFilter) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, code);
    }
}
