package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.HtmlToMarkdownConverter;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.*;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

import org.springframework.stereotype.Component;

@Component
public class JobAdvertisementFromApiAssembler {

	private final HtmlToMarkdownConverter htmlToMarkdownConverter;

	JobAdvertisementFromApiAssembler(HtmlToMarkdownConverter htmlToMarkdownConverter) {
		this.htmlToMarkdownConverter = htmlToMarkdownConverter;
	}

	CreateJobAdvertisementDto convert(ApiCreateJobAdvertisementDto apiCreateDto) {
		return new CreateJobAdvertisementDto()
				.setReportToAvam(apiCreateDto.isReportToAvam())
				.setExternalUrl(safeTrimOrNull(apiCreateDto.getExternalUrl()))
				.setExternalReference(safeTrimOrNull(apiCreateDto.getExternalReference()))
				.setContact(convertContact(apiCreateDto.getContact()))
				.setPublication(convertPublication(apiCreateDto.getPublication()))
				.setNumberOfJobs(safeTrimOrNull(apiCreateDto.getNumberOfJobs()))
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
				.setFirstName(safeTrimOrNull(apiContact.getFirstName()))
				.setLastName(safeTrimOrNull(apiContact.getLastName()))
				.setPhone(safeTrimOrNull(apiContact.getPhone()))
				.setEmail(safeTrimOrNull(apiContact.getEmail()))
				.setLanguageIsoCode(safeTrimOrNull(apiContact.getLanguageIsoCode()));
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
						.setLanguageIsoCode(safeTrimOrNull(apiJobDescription.getLanguageIsoCode()))
						.setTitle(safeTrimOrNull(apiJobDescription.getTitle()))
						.setDescription(htmlToMarkdownConverter.convert(safeTrimOrNull(apiJobDescription.getDescription()))
						))
				.collect(Collectors.toList());
	}


	private CompanyDto convertCompany(ApiCompanyDto apiCompany) {
		if (apiCompany == null) {
			return null;
		}
		return new CompanyDto()
				.setName(safeTrimOrNull(apiCompany.getName()))
				.setStreet(safeTrimOrNull(apiCompany.getStreet()))
				.setHouseNumber(safeTrimOrNull(apiCompany.getHouseNumber()))
				.setPostalCode(safeTrimOrNull(apiCompany.getPostalCode()))
				.setCity(safeTrimOrNull(apiCompany.getCity()))
				.setCountryIsoCode(safeTrimOrNull(apiCompany.getCountryIsoCode()))
				.setPostOfficeBoxNumber(safeTrimOrNull(apiCompany.getPostOfficeBoxNumber()))
				.setPostOfficeBoxPostalCode(safeTrimOrNull(apiCompany.getPostOfficeBoxPostalCode()))
				.setPostOfficeBoxCity(safeTrimOrNull(apiCompany.getPostOfficeBoxCity()))
				.setPhone(safeTrimOrNull(apiCompany.getPhone()))
				.setEmail(safeTrimOrNull(apiCompany.getEmail()))
				.setWebsite(safeTrimOrNull(apiCompany.getWebsite()))
				.setSurrogate(apiCompany.isSurrogate());
	}

	private EmployerDto convertEmployer(ApiEmployerDto apiEmployer) {
		if (apiEmployer == null) {
			return null;
		}
		return new EmployerDto()
				.setName(safeTrimOrNull(apiEmployer.getName()))
				.setPostalCode(safeTrimOrNull(apiEmployer.getPostalCode()))
				.setCity(safeTrimOrNull(apiEmployer.getCity()))
				.setCountryIsoCode(safeTrimOrNull(apiEmployer.getCountryIsoCode()));
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
				.setRemarks(safeTrimOrNull(apiLocation.getRemarks()))
				.setCity(safeTrimOrNull(apiLocation.getCity()))
				.setPostalCode(safeTrimOrNull(apiLocation.getPostalCode()))
				.setCountryIsoCode(safeTrimOrNull(apiLocation.getCountryIsoCode()));
	}

	private OccupationDto convertOccupation(ApiOccupationDto apiOccupation) {
		if (apiOccupation == null) {
			return null;
		}
		return new OccupationDto()
				.setAvamOccupationCode(safeTrimOrNull(apiOccupation.getAvamOccupationCode()))
				.setWorkExperience(apiOccupation.getWorkExperience())
				.setEducationCode(safeTrimOrNull(apiOccupation.getEducationCode()));
	}

	private List<LanguageSkillDto> convertLanguageSkills(List<ApiLanguageSkillDto> apiLanguageSkills) {
		if (apiLanguageSkills == null) {
			return null;
		}
		return apiLanguageSkills.stream()
				.map(apiLanguageSkill -> new LanguageSkillDto()
						.setLanguageIsoCode(safeTrimOrNull(apiLanguageSkill.getLanguageIsoCode()))
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
				.setRawPostAddress(safeTrimOrNull(apiApplyChannel.getMailAddress()))
				.setPostAddress(AddressParser.parse(safeTrimOrNull(apiApplyChannel.getMailAddress()), safeTrimOrNull(apiCreateDto.getCompany().getName())))
				.setEmailAddress(safeTrimOrNull(apiApplyChannel.getEmailAddress()))
				.setPhoneNumber(safeTrimOrNull(apiApplyChannel.getPhoneNumber()))
				.setFormUrl(safeTrimOrNull(apiApplyChannel.getFormUrl()))
				.setAdditionalInfo(safeTrimOrNull(apiApplyChannel.getAdditionalInfo()));
	}

	private PublicContactDto convertPublicContact(ApiPublicContactDto apiPublicContact) {
		if (apiPublicContact == null) {
			return null;
		}
		return new PublicContactDto()
				.setSalutation(apiPublicContact.getSalutation())
				.setFirstName(safeTrimOrNull(apiPublicContact.getFirstName()))
				.setLastName(safeTrimOrNull(apiPublicContact.getLastName()))
				.setPhone(safeTrimOrNull(apiPublicContact.getPhone()))
				.setEmail(safeTrimOrNull(apiPublicContact.getEmail()));
	}

	private static String safeTrimOrNull(String value) {
		return (hasText(value)) ? value.trim() : null;
	}
}
