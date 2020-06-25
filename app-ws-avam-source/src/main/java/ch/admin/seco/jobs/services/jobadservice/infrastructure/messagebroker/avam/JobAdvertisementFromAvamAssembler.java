package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.AddressDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ApplyChannelDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.CompanyDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LanguageSkillDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.OccupationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.AvamCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.ApprovalDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.RejectionDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.UpdateJobAdvertisementFromAvamDto;
import ch.admin.seco.jobs.services.jobadservice.core.utils.MappingBuilder;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Salutation;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkForm;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.WorkingTimePercentage;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.ws.avam.source.WSArbeitsformArray;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.ws.avam.source.WSOsteEgov;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.AvamCodeResolver.*;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.AvamDateTimeFormatter.parseToLocalDate;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.util.StringUtils.trimAllWhitespace;

public class JobAdvertisementFromAvamAssembler {

	private static final Logger LOG = LoggerFactory.getLogger(JobAdvertisementFromAvamAssembler.class);
	private static final EmailValidator emailValidator = new EmailValidator();
	private static final Set<String> LANGUAGE_CODES_TO_IGNORE = new HashSet<>(Arrays.asList("99", "98"));

	private static boolean safeBoolean(Boolean value, boolean defaultValue) {
		return (value != null) ? value.booleanValue() : defaultValue;
	}

	private static String safeTrimOrNull(String value) {
		return (hasText(value)) ? value.trim() : null;
	}

	private static String safeToStringOrNull(BigInteger value) {
		return Objects.toString(value, null);
	}

	AvamCreateJobAdvertisementDto createCreateJobAdvertisementAvamDto(WSOsteEgov avamJobAdvertisement) {
		return new AvamCreateJobAdvertisementDto(
				safeTrimOrNull(avamJobAdvertisement.getStellennummerAvam()),
				safeTrimOrNull(avamJobAdvertisement.getBezeichnung()),
				safeTrimOrNull(avamJobAdvertisement.getBeschreibung()),
				"de", // Not defined in this AVAM version
				safeTrimOrNull(avamJobAdvertisement.getGleicheOste()),
				avamJobAdvertisement.isMeldepflicht(),
				parseToLocalDate(avamJobAdvertisement.getSperrfrist()),
				safeTrimOrNull(avamJobAdvertisement.getArbeitsamtBereich()),
				safeToStringOrNull(avamJobAdvertisement.getBenutzerDetailId()),
				parseToLocalDate(avamJobAdvertisement.getAnmeldeDatum()),
				createEmploymentDto(avamJobAdvertisement),
				createApplyChannelDto(avamJobAdvertisement),
				createCompanyDto(avamJobAdvertisement),
				createContactDto(avamJobAdvertisement),
				createCreateLocationDto(avamJobAdvertisement),
				createOccupationDtos(avamJobAdvertisement),
				createLanguageSkillDtos(avamJobAdvertisement),
				createPublicationDto(avamJobAdvertisement),
				createPublicContactDto(avamJobAdvertisement)
		);
	}

	ApprovalDto createApprovalDto(WSOsteEgov avamJobAdvertisement) {
		return new ApprovalDto(
				safeTrimOrNull(avamJobAdvertisement.getStellennummerEgov()),
				safeTrimOrNull(avamJobAdvertisement.getStellennummerAvam()),
				parseToLocalDate(avamJobAdvertisement.getAnmeldeDatum()),
				avamJobAdvertisement.isMeldepflicht(),
				parseToLocalDate(avamJobAdvertisement.getSperrfrist()),
				safeTrimOrNull(avamJobAdvertisement.getArbeitsamtBereich()),
				safeToStringOrNull(avamJobAdvertisement.getBenutzerDetailId()));
	}

	UpdateJobAdvertisementFromAvamDto createUpdateDto(WSOsteEgov avamJobAdvertisement) {
		return new UpdateJobAdvertisementFromAvamDto(
				safeTrimOrNull(avamJobAdvertisement.getStellennummerAvam()),
				safeTrimOrNull(avamJobAdvertisement.getBezeichnung()),
				safeTrimOrNull(avamJobAdvertisement.getBeschreibung()),
				"de", // Not defined in this AVAM version
				safeTrimOrNull(avamJobAdvertisement.getGleicheOste()),
				avamJobAdvertisement.isMeldepflicht(),
				parseToLocalDate(avamJobAdvertisement.getSperrfrist()),
				safeTrimOrNull(avamJobAdvertisement.getArbeitsamtBereich()),
				safeToStringOrNull(avamJobAdvertisement.getBenutzerDetailId()),
				parseToLocalDate(avamJobAdvertisement.getAnmeldeDatum()),
				createEmploymentDto(avamJobAdvertisement),
				createApplyChannelDto(avamJobAdvertisement),
				createCompanyDto(avamJobAdvertisement),
				createContactDto(avamJobAdvertisement),
				createCreateLocationDto(avamJobAdvertisement),
				createOccupationDtos(avamJobAdvertisement),
				createLanguageSkillDtos(avamJobAdvertisement),
				createPublicationDto(avamJobAdvertisement),
				createPublicContactDto(avamJobAdvertisement));
	}

	RejectionDto createRejectionDto(WSOsteEgov avamJobAdvertisement) {
		return new RejectionDto(
				safeTrimOrNull(avamJobAdvertisement.getStellennummerEgov()),
				safeTrimOrNull(avamJobAdvertisement.getStellennummerAvam()),
				parseToLocalDate(avamJobAdvertisement.getAblehnungDatum()),
				safeTrimOrNull(avamJobAdvertisement.getAblehnungGrundCode()),
				safeTrimOrNull(avamJobAdvertisement.getAblehnungGrund()),
				safeTrimOrNull(avamJobAdvertisement.getArbeitsamtBereich()),
				safeToStringOrNull(avamJobAdvertisement.getBenutzerDetailId())
		);
	}

	AvamCancellationDto createCancellationDto(WSOsteEgov avamJobAdvertisement) {
		return new AvamCancellationDto()
				.setStellennummerEgov(safeTrimOrNull(avamJobAdvertisement.getStellennummerEgov()))
				.setStellennummerAvam(safeTrimOrNull(avamJobAdvertisement.getStellennummerAvam()))
				.setCancellationDate(parseToLocalDate(avamJobAdvertisement.getAbmeldeDatum()))
				.setCancellationCode(resolveMapping(CANCELLATION_CODE, avamJobAdvertisement.getAbmeldeGrundCode(), "CANCELLATION_CODE"))
				.setSourceSystem(resolveMapping(SOURCE_SYSTEM, avamJobAdvertisement.getQuelleCode(), "SOURCE_SYSTEM"))
				.setJobDescriptionTitle(safeTrimOrNull(avamJobAdvertisement.getBezeichnung()))
				.setContactEmail(safeTrimOrNull(avamJobAdvertisement.getKpEmail()))
				.setJobCenterCode(safeTrimOrNull(avamJobAdvertisement.getArbeitsamtBereich()))
				.setJobCenterUserId(safeToStringOrNull(avamJobAdvertisement.getBenutzerDetailId()))
				.setCancelledBy(SourceSystem.RAV);
	}

	private ContactDto createContactDto(WSOsteEgov avamJobAdvertisement) {
		if (hasText(avamJobAdvertisement.getKpAnredeCode()) ||
				hasText(avamJobAdvertisement.getKpVorname()) ||
				hasText(avamJobAdvertisement.getKpName()) ||
				hasText(avamJobAdvertisement.getKpTelefonNr()) ||
				hasText(avamJobAdvertisement.getKpEmail())) {

			return new ContactDto()
					.setSalutation(hasText(avamJobAdvertisement.getKpAnredeCode()) ? SALUTATIONS.getRight(avamJobAdvertisement.getKpAnredeCode()) : Salutation.MR)
					.setFirstName(safeTrimOrNull(avamJobAdvertisement.getKpVorname()))
					.setLastName(safeTrimOrNull(avamJobAdvertisement.getKpName()))
					.setPhone(sanitizePhoneNumber(safeTrimOrNull(avamJobAdvertisement.getKpTelefonNr()), avamJobAdvertisement))
					.setEmail(sanitizeEmail(safeTrimOrNull(avamJobAdvertisement.getKpEmail()), avamJobAdvertisement))
					.setLanguageIsoCode(""); // Not defined in this AVAM version
		}

		return null;
	}

	private PublicContactDto createPublicContactDto(WSOsteEgov avamJobAdvertisement) {
		if (hasText(avamJobAdvertisement.getKpFragenAnredeCode()) ||
				hasText(avamJobAdvertisement.getKpFragenVorname()) ||
				hasText(avamJobAdvertisement.getKpFragenName()) ||
				hasText(avamJobAdvertisement.getKpFragenTelefonNr()) ||
				hasText(avamJobAdvertisement.getKpFragenEmail())) {

			return new PublicContactDto()
					.setSalutation(hasText(avamJobAdvertisement.getKpFragenAnredeCode()) ? SALUTATIONS.getRight(avamJobAdvertisement.getKpFragenAnredeCode()) : Salutation.MR)
					.setFirstName(safeTrimOrNull(avamJobAdvertisement.getKpFragenVorname()))
					.setLastName(safeTrimOrNull(avamJobAdvertisement.getKpFragenName()))
					.setPhone(sanitizePhoneNumber(safeTrimOrNull(avamJobAdvertisement.getKpFragenTelefonNr()), avamJobAdvertisement))
					.setEmail(sanitizeEmail(safeTrimOrNull(avamJobAdvertisement.getKpFragenEmail()), avamJobAdvertisement));
		}

		return null;
	}

	private EmploymentDto createEmploymentDto(WSOsteEgov avamJobAdvertisement) {
		WorkingTimePercentage workingTimePercentage = WorkingTimePercentage.evaluate(avamJobAdvertisement.getPensumVon(), avamJobAdvertisement.getPensumBis());
		return new EmploymentDto()
				.setStartDate(parseToLocalDate(avamJobAdvertisement.getStellenantritt()))
				.setEndDate(getEmploymentEndDate(avamJobAdvertisement))
				.setShortEmployment(avamJobAdvertisement.getFristTyp().equals(AvamCodeResolver.EMPLOYMENT_TERM_TYPE.getLeft(EmploymentTermType.SHORT_TERM)))
				.setImmediately(safeBoolean(avamJobAdvertisement.isAbSofort(), avamJobAdvertisement.getStellenantritt() == null))
				.setPermanent(avamJobAdvertisement.getFristTyp().equals(AvamCodeResolver.EMPLOYMENT_TERM_TYPE.getLeft(EmploymentTermType.PERMANENT)))
				.setWorkloadPercentageMin(workingTimePercentage.getMin())
				.setWorkloadPercentageMax(workingTimePercentage.getMax())
				.setWorkForms(createWorkForms(avamJobAdvertisement));
	}

	private LocalDate getEmploymentEndDate(WSOsteEgov avamJobAdvertisement) {
		return (avamJobAdvertisement.getFristTyp().equals(AvamCodeResolver.EMPLOYMENT_TERM_TYPE.getLeft(EmploymentTermType.FIXED_TERM))) ? parseToLocalDate(avamJobAdvertisement.getVertragsdauer()) : null;
	}

	private CreateLocationDto createCreateLocationDto(WSOsteEgov avamJobAdvertisement) {
		return new CreateLocationDto()
				.setRemarks(safeTrimOrNull(avamJobAdvertisement.getArbeitsOrtText()))
				.setCity(safeTrimOrNull(avamJobAdvertisement.getArbeitsOrtOrt()))
				.setPostalCode(safeTrimOrNull(avamJobAdvertisement.getArbeitsOrtPlz()))
				.setCountryIsoCode(safeTrimOrNull(avamJobAdvertisement.getArbeitsOrtLand()));
	}

	private CompanyDto createCompanyDto(WSOsteEgov avamJobAdvertisement) {
		// This fields are also used for ApplChannel from AVAM
		return new CompanyDto()
				.setName(safeTrimOrNull(avamJobAdvertisement.getUntName()))
				.setStreet(safeTrimOrNull(avamJobAdvertisement.getUntStrasse()))
				.setHouseNumber(safeTrimOrNull(avamJobAdvertisement.getUntHausNr()))
				.setPostalCode(safeTrimOrNull(avamJobAdvertisement.getUntPlz()))
				.setCity(safeTrimOrNull(avamJobAdvertisement.getUntOrt()))
				.setCountryIsoCode(safeTrimOrNull(avamJobAdvertisement.getUntLand()))
				.setPostOfficeBoxNumber(safeTrimOrNull(avamJobAdvertisement.getUntPostfach()))
				.setPostOfficeBoxPostalCode(safeTrimOrNull(avamJobAdvertisement.getUntPostfachPlz()))
				.setPostOfficeBoxCity(safeTrimOrNull(avamJobAdvertisement.getUntPostfachOrt()))
				.setPhone(null) // This is only used for ApplyChannel from AVAM
				.setEmail(null)// This is only used for ApplyChannel from AVAM
				.setWebsite(null)// This is only used for ApplyChannel from AVAM
				.setSurrogate(false);
	}

	private ApplyChannelDto createApplyChannelDto(WSOsteEgov avamJobAdvertisement) {
		return new ApplyChannelDto()
				.setPostAddress(avamJobAdvertisement.isBewerSchriftlich() ? createApplyChannelPostAddress(avamJobAdvertisement) : null)
				.setEmailAddress(avamJobAdvertisement.isBewerElektronisch() ? sanitizeEmail(safeTrimOrNull(avamJobAdvertisement.getBewerUntEmail()), avamJobAdvertisement) : null)
				.setPhoneNumber(avamJobAdvertisement.isBewerTelefonisch() ? sanitizePhoneNumber(safeTrimOrNull(avamJobAdvertisement.getBewerUntTelefon()), avamJobAdvertisement) : null)
				.setFormUrl(avamJobAdvertisement.isBewerElektronisch() ? sanitizeUrl(safeTrimOrNull(avamJobAdvertisement.getBewerUntUrl()), avamJobAdvertisement) : null)
				.setAdditionalInfo(safeTrimOrNull(avamJobAdvertisement.getBewerAngaben()));
	}

	private List<OccupationDto> createOccupationDtos(WSOsteEgov avamJobAdvertisement) {
		return Stream
				.of(
						createOccupationDto(avamJobAdvertisement.getBq1AvamBerufNr(), avamJobAdvertisement.getBq1ErfahrungCode(), avamJobAdvertisement.getBq1AusbildungCode(), avamJobAdvertisement.getBq1QualifikationCode()),
						createOccupationDto(avamJobAdvertisement.getBq2AvamBerufNr(), avamJobAdvertisement.getBq2ErfahrungCode(), avamJobAdvertisement.getBq2AusbildungCode(), avamJobAdvertisement.getBq2QualifikationCode()),
						createOccupationDto(avamJobAdvertisement.getBq3AvamBerufNr(), avamJobAdvertisement.getBq3ErfahrungCode(), avamJobAdvertisement.getBq3AusbildungCode(), avamJobAdvertisement.getBq3QualifikationCode())
				)
				.filter(Objects::nonNull)
				.collect(toList());
	}

	private OccupationDto createOccupationDto(BigInteger avamBerufNr, String erfahrungCode, String ausbildungCode, String qualifikationCode) {
		if (avamBerufNr == null) {
			return null;
		}
		return new OccupationDto()
				.setAvamOccupationCode(safeTrimOrNull(avamBerufNr.toString()))
				.setWorkExperience(resolveMapping(EXPERIENCES, erfahrungCode, "EXPERIENCES"))
				.setEducationCode(safeTrimOrNull(ausbildungCode))
				.setQualificationCode(resolveMapping(QUALIFICATION_CODE, qualifikationCode, "QUALIFICATION_CODE"));
	}

	private List<LanguageSkillDto> createLanguageSkillDtos(WSOsteEgov avamJobAdvertisement) {
		return Stream
				.of(
						createLanguageSkillDto(avamJobAdvertisement.getSk1SpracheCode(), avamJobAdvertisement.getSk1MuendlichCode(), avamJobAdvertisement.getSk1SchriftlichCode()),
						createLanguageSkillDto(avamJobAdvertisement.getSk2SpracheCode(), avamJobAdvertisement.getSk2MuendlichCode(), avamJobAdvertisement.getSk2SchriftlichCode()),
						createLanguageSkillDto(avamJobAdvertisement.getSk3SpracheCode(), avamJobAdvertisement.getSk3MuendlichCode(), avamJobAdvertisement.getSk3SchriftlichCode()),
						createLanguageSkillDto(avamJobAdvertisement.getSk4SpracheCode(), avamJobAdvertisement.getSk4MuendlichCode(), avamJobAdvertisement.getSk4SchriftlichCode()),
						createLanguageSkillDto(avamJobAdvertisement.getSk5SpracheCode(), avamJobAdvertisement.getSk5MuendlichCode(), avamJobAdvertisement.getSk5SchriftlichCode())
				)
				.filter(Objects::nonNull)
				.collect(toList());
	}

	private LanguageSkillDto createLanguageSkillDto(String spracheCode, String muendlichCode, String schriftlichCode) {
		if (LANGUAGE_CODES_TO_IGNORE.contains(safeTrimOrNull(spracheCode))) {
			return null;
		}

		final String resolvedLanguageCode = resolveMapping(LANGUAGES, spracheCode, "LANGUAGES");
		if (resolvedLanguageCode == null) {
			return null;
		}

		return new LanguageSkillDto()
				.setLanguageIsoCode(resolvedLanguageCode)
				.setSpokenLevel(resolveMapping(LANGUAGE_LEVEL, muendlichCode, "LANGUAGE_LEVEL"))
				.setWrittenLevel(resolveMapping(LANGUAGE_LEVEL, schriftlichCode, "LANGUAGE_LEVEL"));

	}

	private PublicationDto createPublicationDto(WSOsteEgov avamJobAdvertisement) {
		return new PublicationDto()
				.setStartDate(parseToLocalDate(avamJobAdvertisement.getAnmeldeDatum()))
				.setEndDate(parseToLocalDate(avamJobAdvertisement.getGueltigkeit()))
				.setEuresDisplay(avamJobAdvertisement.isEures())
				.setEuresAnonymous(avamJobAdvertisement.isEuresAnonym())
				.setPublicDisplay(avamJobAdvertisement.isPublikation())
				.setRestrictedDisplay(avamJobAdvertisement.isLoginPublikation())
				.setCompanyAnonymous(avamJobAdvertisement.isAnonym() || avamJobAdvertisement.isLoginAnonym());
	}

	private Set<WorkForm> createWorkForms(WSOsteEgov avamJobAdvertisement) {
		WSArbeitsformArray arbeitsformCodeList = avamJobAdvertisement.getArbeitsformCodeList();
		if (arbeitsformCodeList != null) {
			return arbeitsformCodeList.getWSArbeitsformArrayItem()
					.stream()
					.map(item -> resolveMapping(WORK_FORMS, item.getArbeitsformCode(), "WORK_FORMS"))
					.collect(Collectors.toSet());
		}
		return Collections.emptySet();
	}

	/*
	 * Check for a valid phone number and remove remarks.
	 */
	private String sanitizePhoneNumber(String phone, WSOsteEgov avamJobAdvertisement) {
		if (hasText(phone)) {
			try {
				Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(phone, "CH");
				if (PhoneNumberUtil.getInstance().isValidNumber(phoneNumber)) {
					return PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
				}
			} catch (NumberParseException e) {
				LOG.warn("JobAd stellennummerAvam: {} has invalid phone number: {}", avamJobAdvertisement.getStellennummerAvam(), phone);
				String[] phoneParts = phone.split("[^\\d\\(\\)\\+ ]");
				if (phoneParts.length > 1) {
					return sanitizePhoneNumber(phoneParts[0], avamJobAdvertisement);
				}
			}
		}
		return null;
	}

	private String sanitizeEmail(String testObject, WSOsteEgov avamJobAdvertisement) {
		if (hasText(testObject)) {
			String email = trimAllWhitespace(testObject).replace("'", "");
			if (emailValidator.isValid(email, null)) {
				return email;
			} else {
				LOG.warn("JobAd stellennummerAvam: {} has invalid email: {}", avamJobAdvertisement.getStellennummerAvam(), testObject);
			}
		}
		return null;
	}

	private String sanitizeUrl(String testObject, WSOsteEgov avamJobAdvertisement) {
		if (hasText(testObject)) {
			try {
				URL url = new URL(testObject);
				return url.toExternalForm();
			} catch (MalformedURLException e) {
				LOG.warn("JobAd stellennummerAvam: {} has invalid URL: {}", avamJobAdvertisement.getStellennummerAvam(), testObject);
				try {
					URL url = new URL("http://" + testObject);
					return url.toExternalForm();
				} catch (MalformedURLException e1) {
					LOG.debug(e1.getMessage());
				}
			}
		}
		return null;
	}

	private AddressDto createApplyChannelPostAddress(WSOsteEgov avamJobAdvertisement) {
		return new AddressDto()
				.setName(safeTrimOrNull(avamJobAdvertisement.getBewerUntName()))
				.setStreet(safeTrimOrNull(avamJobAdvertisement.getBewerUntStrasse()))
				.setHouseNumber(safeTrimOrNull(avamJobAdvertisement.getBewerUntHausNr()))
				.setPostalCode(safeTrimOrNull(avamJobAdvertisement.getBewerUntPlz()))
				.setCity(safeTrimOrNull(avamJobAdvertisement.getBewerUntOrt()))
				.setPostOfficeBoxNumber(safeTrimOrNull(avamJobAdvertisement.getBewerUntPostfach()))
				.setPostOfficeBoxPostalCode(safeTrimOrNull(avamJobAdvertisement.getBewerUntPostfachPlz()))
				.setPostOfficeBoxCity(safeTrimOrNull(avamJobAdvertisement.getBewerUntPostfachOrt()))
				.setCountryIsoCode(safeTrimOrNull(avamJobAdvertisement.getBewerUntLand()));
	}


	private <T> T resolveMapping(MappingBuilder<String, T> mapping, String key, String mappingName) {
		final String trimmedKey = safeTrimOrNull(key);
		final T value = mapping.getRight(trimmedKey);

		if ((trimmedKey != null) && (value == null)) {
			LOG.warn("Mapping {} not found for key: {}", mappingName, trimmedKey);
		}

		return value;
	}

}
