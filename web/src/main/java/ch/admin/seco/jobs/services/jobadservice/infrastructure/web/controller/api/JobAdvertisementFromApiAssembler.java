package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.HtmlToMarkdownConverter;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.*;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.CancellationDto;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.CancellationResource;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Component
public class JobAdvertisementFromApiAssembler {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobAdvertisementFromApiAssembler.class);

	private final HtmlToMarkdownConverter htmlToMarkdownConverter;

	JobAdvertisementFromApiAssembler(HtmlToMarkdownConverter htmlToMarkdownConverter) {
		this.htmlToMarkdownConverter = htmlToMarkdownConverter;
	}

	CreateJobAdvertisementDto convert(ApiCreateJobAdvertisementDto apiCreateDto) {
		return new CreateJobAdvertisementDto()
				.setReportToAvam(apiCreateDto.isReportToAvam())
				.setExternalUrl(trimOrNull(apiCreateDto.getExternalUrl()))
				.setExternalReference(trimOrNull(apiCreateDto.getExternalReference()))
				.setContact(convertContact(apiCreateDto.getContact()))
				.setPublication(convertPublication(apiCreateDto.getPublication()))
				.setNumberOfJobs(trimOrNull(apiCreateDto.getNumberOfJobs()))
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

	CancellationDto convert(CancellationResource cancellation) {
		return new CancellationDto()
				.setCancellationCode(cancellation.getCode())
				.setCancellationDate(TimeMachine.now().toLocalDate())
				.setCancelledBy(SourceSystem.API);
	}

	private ContactDto convertContact(ApiContactDto apiContact) {
		if (apiContact == null) {
			return null;
		}
		return new ContactDto()
				.setSalutation(apiContact.getSalutation())
				.setFirstName(trimOrNull(apiContact.getFirstName()))
				.setLastName(trimOrNull(apiContact.getLastName()))
				.setPhone(sanitizePhoneNumber(apiContact.getPhone()))
				.setEmail(trimOrNull(apiContact.getEmail()))
				.setLanguageIsoCode(trimOrNull(apiContact.getLanguageIsoCode()));
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
						.setLanguageIsoCode(trimOrNull(apiJobDescription.getLanguageIsoCode()))
						.setTitle(trimOrNull(apiJobDescription.getTitle()))
						.setDescription(htmlToMarkdownConverter.convert(trimOrNull(apiJobDescription.getDescription()))
						))
				.collect(Collectors.toList());
	}


	private CompanyDto convertCompany(ApiCompanyDto apiCompany) {
		if (apiCompany == null) {
			return null;
		}
		return new CompanyDto()
				.setName(trimOrNull(apiCompany.getName()))
				.setStreet(trimOrNull(apiCompany.getStreet()))
				.setHouseNumber(trimOrNull(apiCompany.getHouseNumber()))
				.setPostalCode(trimOrNull(apiCompany.getPostalCode()))
				.setCity(trimOrNull(apiCompany.getCity()))
				.setCountryIsoCode(trimOrNull(apiCompany.getCountryIsoCode()))
				.setPostOfficeBoxNumber(trimOrNull(apiCompany.getPostOfficeBoxNumber()))
				.setPostOfficeBoxPostalCode(trimOrNull(apiCompany.getPostOfficeBoxPostalCode()))
				.setPostOfficeBoxCity(trimOrNull(apiCompany.getPostOfficeBoxCity()))
				.setPhone(sanitizePhoneNumber(trimOrNull(apiCompany.getPhone())))
				.setEmail(trimOrNull(apiCompany.getEmail()))
				.setWebsite(trimOrNull(apiCompany.getWebsite()))
				.setSurrogate(apiCompany.isSurrogate());
	}

	private EmployerDto convertEmployer(ApiEmployerDto apiEmployer) {
		if (apiEmployer == null) {
			return null;
		}
		return new EmployerDto()
				.setName(trimOrNull(apiEmployer.getName()))
				.setPostalCode(trimOrNull(apiEmployer.getPostalCode()))
				.setCity(trimOrNull(apiEmployer.getCity()))
				.setCountryIsoCode(trimOrNull(apiEmployer.getCountryIsoCode()));
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
				.setRemarks(trimOrNull(apiLocation.getRemarks()))
				.setCity(trimOrNull(apiLocation.getCity()))
				.setPostalCode(trimOrNull(apiLocation.getPostalCode()))
				.setCountryIsoCode(trimOrNull(apiLocation.getCountryIsoCode()));
	}

	private OccupationDto convertOccupation(ApiOccupationDto apiOccupation) {
		if (apiOccupation == null) {
			return null;
		}
		return new OccupationDto()
				.setAvamOccupationCode(trimOrNull(apiOccupation.getAvamOccupationCode()))
				.setWorkExperience(apiOccupation.getWorkExperience())
				.setEducationCode(trimOrNull(apiOccupation.getEducationCode()));
	}

	private List<LanguageSkillDto> convertLanguageSkills(List<ApiLanguageSkillDto> apiLanguageSkills) {
		if (apiLanguageSkills == null) {
			return null;
		}
		return apiLanguageSkills.stream()
				.map(apiLanguageSkill -> new LanguageSkillDto()
						.setLanguageIsoCode(trimOrNull(apiLanguageSkill.getLanguageIsoCode()))
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
				.setRawPostAddress(trimOrNull(apiApplyChannel.getMailAddress()))
				.setPostAddress(AddressParser.parse(trimOrNull(apiApplyChannel.getMailAddress()), trimOrNull(apiCreateDto.getCompany().getName())))
				.setEmailAddress(trimOrNull(apiApplyChannel.getEmailAddress()))
				.setPhoneNumber(sanitizePhoneNumber(trimOrNull(apiApplyChannel.getPhoneNumber())))
				.setFormUrl(trimOrNull(apiApplyChannel.getFormUrl()))
				.setAdditionalInfo(trimOrNull(apiApplyChannel.getAdditionalInfo()));
	}

	private PublicContactDto convertPublicContact(ApiPublicContactDto apiPublicContact) {
		if (apiPublicContact == null) {
			return null;
		}
		return new PublicContactDto()
				.setSalutation(apiPublicContact.getSalutation())
				.setFirstName(trimOrNull(apiPublicContact.getFirstName()))
				.setLastName(trimOrNull(apiPublicContact.getLastName()))
				.setPhone(sanitizePhoneNumber(trimOrNull(apiPublicContact.getPhone())))
				.setEmail(trimOrNull(apiPublicContact.getEmail()));
	}

    /*
     * Check for a valid phone number and format as international number (with spaces)
     * example: +41 79 555 12 34
     */
    String sanitizePhoneNumber(String phone) {
        if (hasText(phone)) {
            try {
                Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(phone, "CH");
                validatePhoneNumber(phone, phoneNumber);
                return PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            } catch (NumberParseException e) {
                validatePhoneNumber(phone, null);
            }
        }
        return null;
    }

    private void validatePhoneNumber(String phone, Phonenumber.PhoneNumber phoneNumber) {
        String validationMessage = String.format("Phone number %s is not valid and can't be formatted.", phone);

        Condition.notNull(phoneNumber, validationMessage);
        Condition.isTrue(PhoneNumberUtil.getInstance().isValidNumber(phoneNumber), validationMessage);
    }

    private static String trimOrNull(String value) {
        return (hasText(value)) ? value.trim() : null;
    }
}
