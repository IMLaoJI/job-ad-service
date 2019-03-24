package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;

public class ParentRelation {

    protected String name;

    public ParentRelation() {
    }

    public String getName() {
        return name;
    }

    private ParentRelation(Builder builder) {
        this.name = Condition.notNull(builder.name);
    }

    public static final class Builder {
        private String name;

        public Builder() {
        }


        public ParentRelation build() {
            return new ParentRelation(this);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

    }
}
