package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.*;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.UpdateJobAdvertisementFromAvamDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobContent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobDescription;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Location;

public class UpdateJobAdvertisementFromAvamDtoTestFixture {

    public static UpdateJobAdvertisementFromAvamDto updateJobAdvertisementFromAvamDto(JobAdvertisement jobAdvertisement) {
        JobContent jobContent = jobAdvertisement.getJobContent();
        JobDescription firstDescription = jobContent.getJobDescriptions().get(0);
        Location location = jobContent.getLocation();
        CreateLocationDto locationDto = testLocationDto(location);
        return new UpdateJobAdvertisementFromAvamDto()
                .setStellennummerAvam(jobAdvertisement.getStellennummerAvam())
                .setTitle(firstDescription.getTitle())
                .setDescription(firstDescription.getDescription())
                .setLanguageIsoCode(firstDescription.getLanguage().getLanguage())
                .setNumberOfJobs(jobContent.getNumberOfJobs())
                .setReportingObligation(jobAdvertisement.isReportingObligation())
                .setReportingObligationEndDate(jobAdvertisement.getReportingObligationEndDate())
                .setJobCenterCode(jobAdvertisement.getJobCenterCode())
                .setJobCenterUserId(jobAdvertisement.getJobCenterUserId())
                .setApprovalDate(jobAdvertisement.getApprovalDate())
                .setEmployment(EmploymentDto.toDto(jobContent.getEmployment()))
                .setApplyChannel(ApplyChannelDto.toDto(jobContent.getApplyChannel())) // This is only for test purpose. Generally only the displayApplyChannel is converted to ApplyChannelDto
                .setCompany(CompanyDto.toDto(jobContent.getCompany())) // This is only for test purpose. Generally only the displayCompany is converted to CompanyDto
                .setContact(ContactDto.toDto(jobAdvertisement.getContact()))
                .setLocation(locationDto)
                .setOccupations(OccupationDto.toDto(jobContent.getOccupations()))
                .setLanguageSkills(LanguageSkillDto.toDto(jobContent.getLanguageSkills()))
                .setPublication(PublicationDto.toDto(jobAdvertisement.getPublication()))
                .setPublicContact(PublicContactDto.toDto(jobContent.getPublicContact()));
    }

    private static CreateLocationDto testLocationDto(Location location) {
        return new CreateLocationDto()
                .setRemarks(location.getRemarks())
                .setCity(location.getCity())
                .setPostalCode(location.getPostalCode())
                .setCountryIsoCode(location.getCountryIsoCode());
    }
}
