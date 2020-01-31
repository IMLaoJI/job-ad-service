package ch.admin.seco.jobs.services.jobadservice.domain.profession;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;

public class Profession {

    private ProfessionId id;

    private String avamCode;

    private String chIsco3Code;

    private String chIsco5Code;

    private String bfsCode;

    private String label;

    protected Profession() {
        // For reflection libs
    }

    private Profession(Builder builder){
        this.id = Condition.notNull(builder.id);
        this.avamCode = builder.avamCode;
        this.chIsco3Code = builder.chIsco3Code;
        this.chIsco5Code = builder.chIsco5Code;
        this.bfsCode = builder.bfsCode;
        this.label = builder.label;
    }

    public ProfessionId getId() {
        return id;
    }

    public String getAvamCode() {
        return avamCode;
    }

    public String getChIsco3Code() {
        return chIsco3Code;
    }

    public String getChIsco5Code() {
        return chIsco5Code;
    }

    public String getBfsCode() {
        return bfsCode;
    }

    public String getLabel() {
        return label;
    }

    public static class Builder {
        private ProfessionId id;

        private String avamCode;

        private String chIsco3Code;

        private String chIsco5Code;

        private String bfsCode;

        private String label;

        public Builder setId(ProfessionId id) {
            this.id = id;
            return this;
        }

        public Builder setAvamCode(String avamCode) {
            this.avamCode = avamCode;
            return this;
        }

        public Builder setChIsco3Code(String chIsco3Code) {
            this.chIsco3Code = chIsco3Code;
            return this;
        }

        public Builder setChIsco5Code(String chIsco5Code) {
            this.chIsco5Code = chIsco5Code;
            return this;
        }

        public Builder setBfsCode(String bfsCode) {
            this.bfsCode = bfsCode;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Profession build() {
            return new Profession(this);
        }

    }
}
