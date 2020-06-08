package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateId;
import ch.admin.seco.jobs.services.jobadservice.core.domain.IdGenerator;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class SearchProfileId implements AggregateId<SearchProfileId> {

    private String value;

    public SearchProfileId() {
        this(IdGenerator.timeBasedUUID().toString());
    }

    public SearchProfileId(String value) {
        this.value = Condition.notBlank(value);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchProfileId searchProfileId = (SearchProfileId) o;
        return Objects.equals(value, searchProfileId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "SearchProfileId{" +
                "value='" + value + '\'' +
                "}";
    }
}
