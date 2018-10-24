package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkExperience;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ApiOccupationDto {

    @NotEmpty
    @Size(max = 16)
    @Pattern(regexp = "[0-9]*")
    private String avamOccupationCode;

    private WorkExperience workExperience;

    @Size(max = 8)
    @Pattern(regexp = "[0-9]*")
    private String educationCode;

    public String getAvamOccupationCode() {
        return avamOccupationCode;
    }

    public ApiOccupationDto setAvamOccupationCode(String avamOccupationCode) {
        this.avamOccupationCode = avamOccupationCode;
        return this;
    }

    public WorkExperience getWorkExperience() {
        return workExperience;
    }

    public ApiOccupationDto setWorkExperience(WorkExperience workExperience) {
        this.workExperience = workExperience;
        return this;
    }

    public String getEducationCode() {
        return educationCode;
    }

    public ApiOccupationDto setEducationCode(String educationCode) {
        this.educationCode = educationCode;
        return this;
    }
}
