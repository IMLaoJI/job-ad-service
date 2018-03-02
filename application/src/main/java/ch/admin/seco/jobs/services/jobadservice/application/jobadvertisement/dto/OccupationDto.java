package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Occupation;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkExperience;

import java.util.List;
import java.util.stream.Collectors;

public class OccupationDto {

    private String avamCode;
    private WorkExperience workExperience;

    protected OccupationDto() {
        // For reflection libs
    }

    public OccupationDto(String avamCode, WorkExperience workExperience) {
        this.avamCode = avamCode;
        this.workExperience = workExperience;
    }

    public String getAvamCode() {
        return avamCode;
    }

    public void setAvamCode(String avamCode) {
        this.avamCode = avamCode;
    }

    public WorkExperience getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(WorkExperience workExperience) {
        this.workExperience = workExperience;
    }

    public static OccupationDto toDto(Occupation occupation) {
        OccupationDto occupationDto = new OccupationDto();
        occupationDto.setAvamCode(occupation.getProfessionId().getValue());
        occupationDto.setWorkExperience(occupation.getWorkExperience());
        return occupationDto;
    }

    public static List<OccupationDto> toDto(List<Occupation> occupations) {
        return occupations.stream().map(OccupationDto::toDto).collect(Collectors.toList());
    }
}
