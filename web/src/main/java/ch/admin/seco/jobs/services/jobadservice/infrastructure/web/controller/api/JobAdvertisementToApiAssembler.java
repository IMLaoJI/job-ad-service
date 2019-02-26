package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.HtmlToMarkdownConverter;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.*;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.GeoPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobAdvertisementToApiAssembler {

    private final HtmlToMarkdownConverter htmlToMarkdownConverter;

    JobAdvertisementToApiAssembler(HtmlToMarkdownConverter htmlToMarkdownConverter) {
        this.htmlToMarkdownConverter = htmlToMarkdownConverter;
    }

    ApiJobAdvertisementDto convert(JobAdvertisementDto jobAdvertisement) {
        if (jobAdvertisement == null) {
            return null;
        }
        return new ApiJobAdvertisementDto()
                .setId(jobAdvertisement.getId())
                .setStatus(jobAdvertisement.getStatus())
                .setSourceSystem(jobAdvertisement.getSourceSystem())
                .setExternalReference(jobAdvertisement.getExternalReference())
                .setStellennummerEgov(jobAdvertisement.getStellennummerEgov())
                .setStellennummerAvam(jobAdvertisement.getStellennummerAvam())
                .setFingerprint(jobAdvertisement.getFingerprint())
                .setReportingObligation(jobAdvertisement.isReportingObligation())
                .setReportingObligationEndDate(jobAdvertisement.getReportingObligationEndDate())
                .setReportToAvam(jobAdvertisement.isReportToAvam())
                .setJobCenterCode(jobAdvertisement.getJobCenterCode())
                .setApprovalDate(jobAdvertisement.getApprovalDate())
                .setRejectionDate(jobAdvertisement.getRejectionDate())
                .setRejectionCode(jobAdvertisement.getRejectionCode())
                .setRejectionReason(jobAdvertisement.getRejectionReason())
                .setCancellationDate(jobAdvertisement.getCancellationDate())
                .setCancellationCode(jobAdvertisement.getCancellationCode())
                .setPublication(convertPublication(jobAdvertisement.getPublication()))
                .setJobContent(convertJobContent(jobAdvertisement.getJobContent()));
    }

    public List<ApiJobAdvertisementDto> convert(List<JobAdvertisementDto> jobAdvertisements) {
        if (jobAdvertisements == null) {
            return null;
        }
        return jobAdvertisements.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    public Page<ApiJobAdvertisementDto> convertPage(Page<JobAdvertisementDto> jobAdvertisements) {
        if (jobAdvertisements == null) {
            return null;
        }
        return new PageImpl<>(
                convert(jobAdvertisements.getContent()),
                jobAdvertisements.getPageable(),
                jobAdvertisements.getTotalElements()
        );
    }

    private ApiPublicationDto convertPublication(PublicationDto publication) {
        if (publication == null) {
            return null;
        }
        return new ApiPublicationDto()
                .setStartDate(publication.getStartDate())
                .setEndDate(publication.getEndDate())
                .setEuresDisplay(publication.isEuresDisplay())
                .setEuresAnonymous(publication.isEuresAnonymous())
                .setPublicDisplay(publication.isPublicDisplay())
                .setRestrictedDisplay(publication.isRestrictedDisplay())
                .setCompanyAnonymous(publication.isCompanyAnonymous());
    }

    private ApiJobContentDto convertJobContent(JobContentDto jobContent) {
        if (jobContent == null) {
            return null;
        }
        return new ApiJobContentDto()
                .setExternalUrl(jobContent.getExternalUrl())
                .setNumberOfJobs(jobContent.getNumberOfJobs())
                .setJobDescriptions(convertJobDescriptions(jobContent.getJobDescriptions()))
                .setCompany(convertCompany(jobContent.getCompany()))
                .setEmployment(convertEmployment(jobContent.getEmployment()))
                .setLocation(convertLocation(jobContent.getLocation()))
                .setOccupations(convertOccupations(jobContent.getOccupations()))
                .setLanguageSkills(convertLanguageSkills(jobContent.getLanguageSkills()))
                .setApplyChannel(convertApplyChannel(jobContent.getApplyChannel()))
                .setPublicContact(convertPublicContact(jobContent.getPublicContact()));
    }

    private List<ApiJobDescriptionDto> convertJobDescriptions(List<JobDescriptionDto> jobDescriptions) {
        if (jobDescriptions == null) {
            return null;
        }
        return jobDescriptions.stream()
                .map(jobDescription -> new ApiJobDescriptionDto()
                        .setLanguageIsoCode(jobDescription.getLanguageIsoCode())
                        .setTitle(jobDescription.getTitle())
                        .setDescription(htmlToMarkdownConverter.convert(jobDescription.getDescription()))
                )
                .collect(Collectors.toList());
    }

    private ApiCompanyDto convertCompany(CompanyDto company) {
        if (company == null) {
            return null;
        }
        return new ApiCompanyDto()
                .setName(company.getName())
                .setStreet(company.getStreet())
                .setHouseNumber(company.getHouseNumber())
                .setPostalCode(company.getPostalCode())
                .setCity(company.getCity())
                .setCountryIsoCode(company.getCountryIsoCode())
                .setPostOfficeBoxNumber(company.getPostOfficeBoxNumber())
                .setPostOfficeBoxPostalCode(company.getPostOfficeBoxPostalCode())
                .setPostOfficeBoxCity(company.getPostOfficeBoxCity())
                .setPhone(company.getPhone())
                .setEmail(company.getEmail())
                .setWebsite(company.getWebsite())
                .setSurrogate(company.isSurrogate());
    }

    private ApiEmploymentDto convertEmployment(EmploymentDto employment) {
        if (employment == null) {
            return null;
        }
        return new ApiEmploymentDto()
                .setStartDate(employment.getStartDate())
                .setEndDate(employment.getEndDate())
                .setShortEmployment(employment.isShortEmployment())
                .setImmediately(employment.isImmediately())
                .setPermanent(employment.isPermanent())
                .setWorkloadPercentageMin(employment.getWorkloadPercentageMin())
                .setWorkloadPercentageMax(employment.getWorkloadPercentageMax())
                .setWorkForms(employment.getWorkForms());
    }

    private ApiLocationDto convertLocation(LocationDto location) {
        if (location == null) {
            return null;
        }
        return new ApiLocationDto()
                .setRemarks(location.getRemarks())
                .setCity(location.getCity())
                .setPostalCode(location.getPostalCode())
                .setCommunalCode(location.getCommunalCode())
                .setRegionCode(location.getRegionCode())
                .setCantonCode(location.getCantonCode())
                .setCountryIsoCode(location.getCountryIsoCode())
                .setCoordinates(convertCoordinates(location.getCoordinates()));
    }

    private ApiGeoPointDto convertCoordinates(GeoPoint coordinates) {
        if (coordinates == null){
            return null;
        }
        return new ApiGeoPointDto().setLatitude(coordinates.getLat()).setLongitude(coordinates.getLon());
    }

    private List<ApiOccupationDto> convertOccupations(List<OccupationDto> occupations) {
        if (occupations == null) {
            return null;
        }
        return occupations.stream()
                .map(occupation -> new ApiOccupationDto()
                        .setAvamOccupationCode(occupation.getAvamOccupationCode())
                        .setWorkExperience(occupation.getWorkExperience())
                        .setEducationCode(occupation.getEducationCode())
                )
                .collect(Collectors.toList());
    }

    private List<ApiLanguageSkillDto> convertLanguageSkills(List<LanguageSkillDto> languageSkills) {
        if (languageSkills == null) {
            return null;
        }
        return languageSkills.stream()
                .map(languageSkill -> new ApiLanguageSkillDto()
                        .setLanguageIsoCode(languageSkill.getLanguageIsoCode())
                        .setSpokenLevel(languageSkill.getSpokenLevel())
                        .setWrittenLevel(languageSkill.getWrittenLevel())
                )
                .collect(Collectors.toList());
    }

    private ApiApplyChannelDto convertApplyChannel(ApplyChannelDto applyChannel) {
        if (applyChannel == null) {
            return null;
        }
        return new ApiApplyChannelDto()
                .setMailAddress(applyChannel.getRawPostAddress())
                .setEmailAddress(applyChannel.getEmailAddress())
                .setPhoneNumber(applyChannel.getPhoneNumber())
                .setFormUrl(applyChannel.getFormUrl())
                .setAdditionalInfo(applyChannel.getAdditionalInfo());
    }

    private ApiPublicContactDto convertPublicContact(PublicContactDto apiPublicContact) {
        if (apiPublicContact == null) {
            return null;
        }
        return new ApiPublicContactDto()
                .setSalutation(apiPublicContact.getSalutation())
                .setFirstName(apiPublicContact.getFirstName())
                .setLastName(apiPublicContact.getLastName())
                .setPhone(apiPublicContact.getPhone())
                .setEmail(apiPublicContact.getEmail());
    }
}
