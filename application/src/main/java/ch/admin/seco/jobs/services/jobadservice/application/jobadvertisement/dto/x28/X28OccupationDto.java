package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.x28;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.OccupationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Qualification;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkExperience;

public class X28OccupationDto {

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

    public X28OccupationDto setAvamOccupationCode(String avamOccupationCode) {
        this.avamOccupationCode = avamOccupationCode;
        return this;
    }

    public WorkExperience getWorkExperience() {
        return workExperience;
    }

    public X28OccupationDto setWorkExperience(WorkExperience workExperience) {
        this.workExperience = workExperience;
        return this;
    }

    public String getEducationCode() {
        return educationCode;
    }

    public X28OccupationDto setEducationCode(String educationCode) {
        this.educationCode = educationCode;
        return this;
    }

    public Qualification getQualificationCode() {
        return qualificationCode;
    }

    public X28OccupationDto setQualificationCode(Qualification qualificationCode) {
        this.qualificationCode = qualificationCode;
        return this;
    }
}
