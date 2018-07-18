package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.api;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.*;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;

import java.util.List;
import java.util.stream.Collectors;

public class ConvertApiToNormalCreateJobAdvertisementDto {

    public static CreateJobAdvertisementDto convert(ApiCreateJobAdvertisementDto apiCreateDto) {
        return new CreateJobAdvertisementDto(
                apiCreateDto.isReportToAvam(),
                apiCreateDto.getExternalUrl(),
                apiCreateDto.getExternalReference(),
                convertContact(apiCreateDto.getContact()),
                convertPublication(apiCreateDto.getPublication()),
                convertJobDescriptions(apiCreateDto.getJobDescriptions()),
                convertCompany(apiCreateDto.getCompany()),
                convertEmployer(apiCreateDto.getEmployer()),
                convertEmployment(apiCreateDto.getEmployment()),
                convertCreateLocation(apiCreateDto.getLocation()),
                convertOccupation(apiCreateDto.getOccupation()),
                convertLanguageSkills(apiCreateDto.getLanguageSkills()),
                convertApplyChannel(apiCreateDto.getApplyChannel()),
                convertPublicContact(apiCreateDto.getPublicContact())
        );
    }

    private static ContactDto convertContact(ApiContactDto apiContact) {
        if (apiContact != null) {
            return new ContactDto(
                    apiContact.getSalutation(),
                    apiContact.getFirstName(),
                    apiContact.getLastName(),
                    apiContact.getPhone(),
                    apiContact.getEmail(),
                    apiContact.getLanguageIsoCode()
            );
        }
        return null;
    }

    private static PublicationDto convertPublication(ApiPublicationDto apiPublication) {
        if(apiPublication != null) {
            return new PublicationDto(
              apiPublication.getStartDate(),
              apiPublication.getEndDate(),
              apiPublication.isEuresDisplay(),
              apiPublication.isEuresAnonymous(),
              apiPublication.isPublicDisplay(),
              apiPublication.isPublicAnonymous(),
              apiPublication.isRestrictedDisplay(),
              apiPublication.isRestrictedAnonymous()
            );
        }
        return null;
    }

    private static List<JobDescriptionDto> convertJobDescriptions(List<ApiJobDescriptionDto> apiJobDescriptions) {
        if(apiJobDescriptions != null) {
            return apiJobDescriptions.stream()
                    .map(apiJobDescription -> new JobDescriptionDto(
                            apiJobDescription.getLanguageIsoCode(),
                            apiJobDescription.getTitle(),
                            apiJobDescription.getDescription()
                    ))
                    .collect(Collectors.toList());
        }
        return null;
    }

    private static CompanyDto convertCompany(ApiCompanyDto apiCompany) {
        if(apiCompany != null) {
            return new CompanyDto(
              apiCompany.getName(),
              apiCompany.getStreet(),
              apiCompany.getHouseNumber(),
              apiCompany.getPostalCode(),
              apiCompany.getCity(),
              apiCompany.getCountryIsoCode(),
              apiCompany.getPostOfficeBoxNumber(),
              apiCompany.getPostOfficeBoxPostalCode(),
              apiCompany.getPostOfficeBoxCity(),
              apiCompany.getPhone(),
              apiCompany.getEmail(),
              apiCompany.getWebsite(),
              apiCompany.isSurrogate()
            );
        }
        return null;
    }

    private static EmployerDto convertEmployer(ApiEmployerDto apiEmployer) {
        if(apiEmployer != null) {
            return new EmployerDto(
                    apiEmployer.getName(),
                    apiEmployer.getPostalCode(),
                    apiEmployer.getCity(),
                    apiEmployer.getCountryIsoCode()
            );
        }
        return null;
    }

    private static EmploymentDto convertEmployment(ApiEmploymentDto apiEmployment) {
        if(apiEmployment != null) {
            return new EmploymentDto(
                    apiEmployment.getStartDate(),
                    apiEmployment.getEndDate(),
                    apiEmployment.isShortEmployment(),
                    apiEmployment.isImmediately(),
                    apiEmployment.isPermanent(),
                    apiEmployment.getWorkloadPercentageMin(),
                    apiEmployment.getWorkloadPercentageMax(),
                    apiEmployment.getWorkForms()
            );
        }
        return null;
    }

    private static CreateLocationDto convertCreateLocation(ApiCreateLocationDto apiLocation) {
        if(apiLocation != null) {
            return new CreateLocationDto(
                    apiLocation.getRemarks(),
                    apiLocation.getCity(),
                    apiLocation.getPostalCode(),
                    apiLocation.getCountryIsoCode()
            );
        }
        return null;
    }

    private static OccupationDto convertOccupation(ApiOccupationDto apiOccupation) {
        if(apiOccupation != null) {
            return new OccupationDto(
                    apiOccupation.getAvamOccupationCode(),
                    apiOccupation.getWorkExperience(),
                    apiOccupation.getEducationCode()
            );
        }
        return null;
    }

    private static List<LanguageSkillDto> convertLanguageSkills(List<ApiLanguageSkillDto> apiLanguageSkills) {
        if(apiLanguageSkills != null) {
            return apiLanguageSkills.stream()
                    .map(apiLanguageSkill -> new LanguageSkillDto(
                            apiLanguageSkill.getLanguageIsoCode(),
                            apiLanguageSkill.getSpokenLevel(),
                            apiLanguageSkill.getWrittenLevel()
                    ))
                    .collect(Collectors.toList());
        }
        return null;
    }

    private static ApplyChannelDto convertApplyChannel(ApiApplyChannelDto apiApplyChannel) {
        if(apiApplyChannel != null) {
            return new ApplyChannelDto(
                    apiApplyChannel.getMailAddress(),
                    apiApplyChannel.getEmailAddress(),
                    apiApplyChannel.getPhoneNumber(),
                    apiApplyChannel.getFormUrl(),
                    apiApplyChannel.getAdditionalInfo()
            );
        }
        return null;
    }

    private static PublicContactDto convertPublicContact(ApiPublicContactDto apiPublicContact) {
        if(apiPublicContact != null) {
            return new PublicContactDto(
                    apiPublicContact.getSalutation(),
                    apiPublicContact.getFirstName(),
                    apiPublicContact.getLastName(),
                    apiPublicContact.getPhone(),
                    apiPublicContact.getEmail()
            );
        }
        return null;
    }

}
