package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.OccupationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Qualification;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkExperience;

public class ExternalOccupationDto {

    @NotBlank
    @Size(max = 16)
    private String avamOccupationCode;

    private WorkExperience workExperience;

    @Size(max = 8)
    private String educationCode;

    private Qualification qualificationCode;

    OccupationDto toOccupationDto() {
        return new OccupationDto()
                .setAvamOccupationCode(avamOccupationCode)
                .setWorkExperience(workExperience)
                .setEducationCode(educationCode)
                .setQualificationCode(qualificationCode);
    }

    public String getAvamOccupationCode() {
        return avamOccupationCode;
    }

    public ExternalOccupationDto setAvamOccupationCode(String avamOccupationCode) {
        this.avamOccupationCode = avamOccupationCode;
        return this;
    }

    public WorkExperience getWorkExperience() {
        return workExperience;
    }

    public ExternalOccupationDto setWorkExperience(WorkExperience workExperience) {
        this.workExperience = workExperience;
        return this;
    }

    public String getEducationCode() {
        return educationCode;
    }

    public ExternalOccupationDto setEducationCode(String educationCode) {
        this.educationCode = educationCode;
        return this;
    }

    public Qualification getQualificationCode() {
        return qualificationCode;
    }

    public ExternalOccupationDto setQualificationCode(Qualification qualificationCode) {
        this.qualificationCode = qualificationCode;
        return this;
    }
}
