package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.HtmlToMarkdownConverter;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.*;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobAdvertisementFromApiAssembler {

    private final HtmlToMarkdownConverter htmlToMarkdownConverter;

    JobAdvertisementFromApiAssembler(HtmlToMarkdownConverter htmlToMarkdownConverter) {
        this.htmlToMarkdownConverter = htmlToMarkdownConverter;
    }

    CreateJobAdvertisementDto convert(ApiCreateJobAdvertisementDto apiCreateDto) {
        return new CreateJobAdvertisementDto()
                .setReportToAvam(apiCreateDto.isReportToAvam())
                .setExternalUrl(apiCreateDto.getExternalUrl().trim())
                .setExternalReference(apiCreateDto.getExternalReference().trim())
                .setContact(convertContact(apiCreateDto.getContact()))
                .setPublication(convertPublication(apiCreateDto.getPublication()))
                .setNumberOfJobs(apiCreateDto.getNumberOfJobs().trim())
                .setJobDescriptions(convertJobDescriptions(apiCreateDto.getJobDescriptions()))
                .setCompany(convertCompany(apiCreateDto.getCompany()))
                .setEmployer(convertEmployer(apiCreateDto.getEmployer()))
                .setEmployment(convertEmployment(apiCreateDto.getEmployment()))
                .setLocation(convertCreateLocation(apiCreateDto.getLocation()))
                .setOccupation(convertOccupation(apiCreateDto.getOccupation()))
                .setLanguageSkills(convertLanguageSkills(apiCreateDto.getLanguageSkills()))
                .setApplyChannel(convertApplyChannel(apiCreateDto))
                .setPublicContact(convertPublicContact(apiCreateDto.getPublicContact()));
    }

    private ContactDto convertContact(ApiContactDto apiContact) {
        if (apiContact == null) {
            return null;
        }
        return new ContactDto()
                .setSalutation(apiContact.getSalutation())
                .setFirstName(apiContact.getFirstName().trim())
                .setLastName(apiContact.getLastName().trim())
                .setPhone(apiContact.getPhone().trim())
                .setEmail(apiContact.getEmail().trim())
                .setLanguageIsoCode(apiContact.getLanguageIsoCode().trim());
    }

    private PublicationDto convertPublication(ApiPublicationDto apiPublication) {
        if (apiPublication == null) {
            return null;
        }
        return new PublicationDto()
                .setStartDate(apiPublication.getStartDate())
                .setEndDate(apiPublication.getEndDate())
                .setEuresDisplay(apiPublication.isEuresDisplay())
                .setEuresAnonymous(apiPublication.isEuresAnonymous())
                .setPublicDisplay(apiPublication.isPublicDisplay())
                .setRestrictedDisplay(apiPublication.isRestrictedDisplay())
                .setCompanyAnonymous(apiPublication.isCompanyAnonymous());
    }

    private List<JobDescriptionDto> convertJobDescriptions(List<ApiJobDescriptionDto> apiJobDescriptions) {
        if (apiJobDescriptions == null) {
            return null;
        }
        return apiJobDescriptions.stream()
                .map(apiJobDescription -> new JobDescriptionDto()
                        .setLanguageIsoCode(apiJobDescription.getLanguageIsoCode().trim())
                        .setTitle(apiJobDescription.getTitle().trim())
                        .setDescription(htmlToMarkdownConverter.convert(apiJobDescription.getDescription().trim())
                        ))
                .collect(Collectors.toList());
    }


    private CompanyDto convertCompany(ApiCompanyDto apiCompany) {
        if (apiCompany == null) {
            return null;
        }
        return new CompanyDto()
                .setName(apiCompany.getName().trim())
                .setStreet(apiCompany.getStreet().trim())
                .setHouseNumber(apiCompany.getHouseNumber().trim())
                .setPostalCode(apiCompany.getPostalCode().trim())
                .setCity(apiCompany.getCity().trim())
                .setCountryIsoCode(apiCompany.getCountryIsoCode().trim())
                .setPostOfficeBoxNumber(apiCompany.getPostOfficeBoxNumber().trim())
                .setPostOfficeBoxPostalCode(apiCompany.getPostOfficeBoxPostalCode().trim())
                .setPostOfficeBoxCity(apiCompany.getPostOfficeBoxCity().trim())
                .setPhone(apiCompany.getPhone().trim())
                .setEmail(apiCompany.getEmail().trim())
                .setWebsite(apiCompany.getWebsite().trim())
                .setSurrogate(apiCompany.isSurrogate());
    }

    private EmployerDto convertEmployer(ApiEmployerDto apiEmployer) {
        if (apiEmployer == null) {
            return null;
        }
        return new EmployerDto()
                .setName(apiEmployer.getName().trim())
                .setPostalCode(apiEmployer.getPostalCode().trim())
                .setCity(apiEmployer.getCity().trim())
                .setCountryIsoCode(apiEmployer.getCountryIsoCode().trim());
    }

    private EmploymentDto convertEmployment(ApiEmploymentDto apiEmployment) {
        if (apiEmployment == null) {
            return null;
        }
        return new EmploymentDto()
                .setStartDate(apiEmployment.getStartDate())
                .setEndDate(apiEmployment.getEndDate())
                .setShortEmployment(apiEmployment.isShortEmployment())
                .setImmediately(apiEmployment.isImmediately())
                .setPermanent(apiEmployment.isPermanent())
                .setWorkloadPercentageMin(apiEmployment.getWorkloadPercentageMin())
                .setWorkloadPercentageMax(apiEmployment.getWorkloadPercentageMax())
                .setWorkForms(apiEmployment.getWorkForms());
    }

    private CreateLocationDto convertCreateLocation(ApiCreateLocationDto apiLocation) {
        if (apiLocation == null) {
            return null;
        }
        return new CreateLocationDto()
                .setRemarks(apiLocation.getRemarks().trim())
                .setCity(apiLocation.getCity().trim())
                .setPostalCode(apiLocation.getPostalCode().trim())
                .setCountryIsoCode(apiLocation.getCountryIsoCode().trim());
    }

    private OccupationDto convertOccupation(ApiOccupationDto apiOccupation) {
        if (apiOccupation == null) {
            return null;
        }
        return new OccupationDto()
                .setAvamOccupationCode(apiOccupation.getAvamOccupationCode().trim())
                .setWorkExperience(apiOccupation.getWorkExperience())
                .setEducationCode(apiOccupation.getEducationCode().trim());
    }

    private List<LanguageSkillDto> convertLanguageSkills(List<ApiLanguageSkillDto> apiLanguageSkills) {
        if (apiLanguageSkills == null) {
            return null;
        }
        return apiLanguageSkills.stream()
                .map(apiLanguageSkill -> new LanguageSkillDto()
                        .setLanguageIsoCode(apiLanguageSkill.getLanguageIsoCode().trim())
                        .setSpokenLevel(apiLanguageSkill.getSpokenLevel())
                        .setWrittenLevel(apiLanguageSkill.getWrittenLevel())
                )
                .collect(Collectors.toList());
    }

    private ApplyChannelDto convertApplyChannel(ApiCreateJobAdvertisementDto apiCreateDto) {
        ApiApplyChannelDto apiApplyChannel = apiCreateDto.getApplyChannel();
        if (apiApplyChannel == null) {
            return null;
        }
        return new ApplyChannelDto()
                .setRawPostAddress(apiApplyChannel.getMailAddress())
                .setPostAddress(AddressParser.parse(apiApplyChannel.getMailAddress(), apiCreateDto.getCompany().getName().trim()))
                .setEmailAddress(apiApplyChannel.getEmailAddress().trim())
                .setPhoneNumber(apiApplyChannel.getPhoneNumber().trim())
                .setFormUrl(apiApplyChannel.getFormUrl().trim())
                .setAdditionalInfo(apiApplyChannel.getAdditionalInfo().trim());
    }

    private PublicContactDto convertPublicContact(ApiPublicContactDto apiPublicContact) {
        if (apiPublicContact == null) {
            return null;
        }
        return new PublicContactDto()
                .setSalutation(apiPublicContact.getSalutation())
                .setFirstName(apiPublicContact.getFirstName().trim())
                .setLastName(apiPublicContact.getLastName().trim())
                .setPhone(apiPublicContact.getPhone().trim())
                .setEmail(apiPublicContact.getEmail().trim());
    }

    // TODO replace trim with this
    private static String safeTrimOrNull(String value) {
        return (hasText(value)) ? value.trim() : null;
    }
}
