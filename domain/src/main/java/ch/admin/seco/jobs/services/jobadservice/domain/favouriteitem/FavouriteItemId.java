package ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateId;
import ch.admin.seco.jobs.services.jobadservice.core.domain.IdGenerator;

import java.util.Objects;

public class FavouriteItemId implements AggregateId<FavouriteItemId> {

    private String value;

    public FavouriteItemId() {
        this(IdGenerator.timeBasedUUID().toString());
    }

    public FavouriteItemId(String value) {
        this.value = Condition.notBlank(value).toLowerCase();
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
        FavouriteItemId favouriteItemId = (FavouriteItemId) o;
        return Objects.equals(value, favouriteItemId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "FavouriteItemId{" +
                "value='" + value + '\'' +
                '}';
    }
}
