package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.ValueObject;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class Occupation implements ValueObject<Occupation> {

    // TODO @Size(max = 16)
    private String avamOccupationCode;

    // TODO @Size(max = 16)
    @Column(name = "SBN3_CODE")
    private String sbn3Code;

    // TODO @Size(max = 16)
    @Column(name = "SBN5_CODE")
    private String sbn5Code;

    @Size(max= 3)
    @Column(name = "CHISCO3_CODE")
    private String chIsco3Code;

    @Size(max= 5)
    @Column(name = "CHISCO5_CODE")
    private String chIsco5Code;

    // TODO @Size(max = 16)
    private String bfsCode;

    private String label;

    @Enumerated(EnumType.STRING)
    private WorkExperience workExperience;

    // TODO @Size(max = 16)
    private String educationCode;

    @Enumerated(EnumType.STRING)
    private Qualification qualificationCode;

    protected Occupation() {
        // For reflection libs
    }

    public Occupation(Builder builder) {
        this.avamOccupationCode = Condition.notBlank(builder.avamOccupationCode);
        this.sbn3Code = builder.sbn3Code;
        this.sbn5Code = builder.sbn5Code;
        this.chIsco3Code = builder.chIsco3Code;
        this.chIsco5Code = builder.chIsco5Code;
        this.bfsCode = builder.bfsCode;
        this.label = builder.label;
        this.workExperience = builder.workExperience;
        this.educationCode = builder.educationCode;
        this.qualificationCode = builder.qualificationCode;
    }

    public String getAvamOccupationCode() {
        return avamOccupationCode;
    }

    public String getSbn3Code() {
        return sbn3Code;
    }

    public String getSbn5Code() {
        return sbn5Code;
    }

    public String getBfsCode() {
        return bfsCode;
    }

    public String getChIsco3Code() {
        return chIsco3Code;
    }

    public String getChIsco5Code() {
        return chIsco5Code;
    }

    public String getLabel() {
        return label;
    }

    public WorkExperience getWorkExperience() {
        return workExperience;
    }

    public String getEducationCode() {
        return educationCode;
    }

    public Qualification getQualificationCode() {
        return qualificationCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Occupation that = (Occupation) o;
        return Objects.equals(avamOccupationCode, that.avamOccupationCode) &&
                Objects.equals(sbn3Code, that.sbn3Code) &&
                Objects.equals(sbn5Code, that.sbn5Code) &&
                Objects.equals(bfsCode, that.bfsCode) &&
                Objects.equals(label, that.label) &&
                workExperience == that.workExperience &&
                Objects.equals(educationCode, that.educationCode) &&
                qualificationCode == that.qualificationCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(avamOccupationCode, sbn3Code, sbn5Code, bfsCode, label, workExperience, educationCode, qualificationCode);
    }

    @Override
    public String toString() {
        return "Occupation{" +
                "avamOccupationCode='" + avamOccupationCode + '\'' +
                ", sbn3Code='" + sbn3Code + '\'' +
                ", sbn5Code='" + sbn5Code + '\'' +
                ", bfsCode='" + bfsCode + '\'' +
                ", label='" + label + '\'' +
                ", workExperience=" + workExperience +
                ", educationCode='" + educationCode + '\'' +
                ", qualificationCode=" + qualificationCode +
                '}';
    }

    public static final class Builder {
        private String avamOccupationCode;
        private String sbn3Code;
        private String sbn5Code;
        private String chIsco3Code;
        private String chIsco5Code;
        private String bfsCode;
        private String label;
        private WorkExperience workExperience;
        private String educationCode;
        private Qualification qualificationCode;

        public Builder() {
        }

        public Occupation build() {
            return new Occupation(this);
        }

        public Builder setAvamOccupationCode(String avamOccupationCode) {
            this.avamOccupationCode = avamOccupationCode;
            return this;
        }

        public Builder setSbn3Code(String sbn3Code) {
            this.sbn3Code = sbn3Code;
            return this;
        }

        public Builder setSbn5Code(String sbn5Code) {
            this.sbn5Code = sbn5Code;
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

        public Builder setWorkExperience(WorkExperience workExperience) {
            this.workExperience = workExperience;
            return this;
        }

        public Builder setEducationCode(String educationCode) {
            this.educationCode = educationCode;
            return this;
        }

        public Builder setQualification(Qualification qualification) {
            this.qualificationCode = qualification;
            return this;
        }
    }
}
