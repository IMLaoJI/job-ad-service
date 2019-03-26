package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;

public class ChildRelation extends ParentRelation {

    private String parent;

    private ChildRelation(Builder builder) {
        this.name = Condition.notNull(builder.name);
        this.parent = Condition.notNull(builder.parent);
    }

    public String getParent() {
        return parent;
    }

    public static final class Builder {
        private String name;
        private String parent;

        public Builder() {
        }


        public ChildRelation build() {
            return new ChildRelation(this);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setParent(String parent) {
            this.parent = parent;
            return this;
        }

    }
}
