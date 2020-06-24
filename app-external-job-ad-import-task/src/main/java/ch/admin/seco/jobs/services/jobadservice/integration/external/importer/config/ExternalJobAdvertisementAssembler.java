package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.LanguageLevel;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Qualification;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Salutation;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkExperience;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.WorkingTimePercentage;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.AvamCodeResolver;
import ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config.dto.ExternalCompanyDto;
import ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config.dto.ExternalContactDto;
import ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config.dto.ExternalCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config.dto.ExternalLanguageSkillDto;
import ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config.dto.ExternalLocationDto;
import ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config.dto.ExternalOccupationDto;
import ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config.dto.ExternalPublicationDto;
import ch.admin.seco.jobs.services.jobadservice.integration.external.jobadimport.Oste;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.commons.lang3.EnumUtils;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.AvamCodeResolver.LANGUAGES;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.AvamCodeResolver.LANGUAGE_LEVEL;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.util.StringUtils.isEmpty;
import static org.springframework.util.StringUtils.trimAllWhitespace;

class ExternalJobAdvertisementAssembler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExternalJobAdvertisementAssembler.class);

	private static final Pattern COUNTRY_ZIPCODE_CITY_PATTERN = Pattern.compile("\\b(([A-Z]{2,3})?[ -]+)?(\\d{4,5})? ?([\\wäöüéèàâôÄÖÜÉÈÀ /.-]+)");
	private static final Pattern CITY_CANTON_PATTERN = Pattern.compile("(.* |^)\\(?([A-Z]{2})\\)?( \\d)?$");
	private static final String LICHTENSTEIN_ISO_CODE = "LI";
	private static final String ORACLE_DATE_FORMAT = "yyyy-MM-dd-HH.mm.ss.SSSSSS";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(ORACLE_DATE_FORMAT);
	private static final EmailValidator EMAIL_VALIDATOR = new EmailValidator();
	private static final String DEFAULT_AVAM_OCCUPATION_CODE = "99999";
	private static final String COUNTRY_ISO_CODE_SWITZERLAND = "CH";

	ExternalJobAdvertisementAssembler() {
	}

	ExternalCreateJobAdvertisementDto createJobAdvertisementFromExternalDto(Oste externalJobAdvertisement) {
		return new ExternalCreateJobAdvertisementDto()
				.setStellennummerEgov(externalJobAdvertisement.getStellennummerEGov())
				.setStellennummerAvam(externalJobAdvertisement.getStellennummerAvam())
				.setTitle(sanitize(externalJobAdvertisement.getBezeichnung()))
				.setDescription(sanitize(externalJobAdvertisement.getBeschreibung()))
				.setNumberOfJobs(externalJobAdvertisement.getGleicheOste())
				.setFingerprint(externalJobAdvertisement.getFingerprint())
				.setExternalUrl(externalJobAdvertisement.getUrl())
				.setJobCenterCode(externalJobAdvertisement.getArbeitsamtbereich())
				.setContact(createContact(externalJobAdvertisement))
				.setEmployment(createEmployment(externalJobAdvertisement))
				.setCompany(createCompany(externalJobAdvertisement))
				.setLocation(createLocation(externalJobAdvertisement))
				.setOccupations(createOccupations(externalJobAdvertisement))
				.setProfessionCodes(createProfessionCodes(externalJobAdvertisement))
				.setLanguageSkills(createLanguageSkills(externalJobAdvertisement))
				.setPublicationDto(createPublication(externalJobAdvertisement));
	}

	private LocalDate determineStartDate(LocalDate startDate, LocalDate endDate) {
		if (startDate == null) {
			return null;
		}
		if (endDate == null) {
			return startDate;
		}
		if (startDate.isAfter(endDate)) {
			return endDate;
		}
		return startDate;
	}

	private LocalDate determineEndDate(LocalDate startDate, LocalDate endDate) {
		if (endDate == null) {
			return null;
		}
		if (startDate == null) {
			return endDate;
		}
		if (endDate.isBefore(startDate)) {
			return startDate;
		}
		return endDate;
	}

	private List<ExternalLanguageSkillDto> createLanguageSkills(Oste externalJobAdvertisement) {
		return Stream
				.of(
						createLanguageSkillDto(externalJobAdvertisement.getSk1SpracheCode(), externalJobAdvertisement.getSk1MuendlichCode(), externalJobAdvertisement.getSk1SchriftlichCode()),
						createLanguageSkillDto(externalJobAdvertisement.getSk2SpracheCode(), externalJobAdvertisement.getSk2MuendlichCode(), externalJobAdvertisement.getSk2SchriftlichCode()),
						createLanguageSkillDto(externalJobAdvertisement.getSk3SpracheCode(), externalJobAdvertisement.getSk3MuendlichCode(), externalJobAdvertisement.getSk3SchriftlichCode()),
						createLanguageSkillDto(externalJobAdvertisement.getSk4SpracheCode(), externalJobAdvertisement.getSk4MuendlichCode(), externalJobAdvertisement.getSk4SchriftlichCode()),
						createLanguageSkillDto(externalJobAdvertisement.getSk5SpracheCode(), externalJobAdvertisement.getSk5MuendlichCode(), externalJobAdvertisement.getSk5SchriftlichCode())
				)
				.filter(Objects::nonNull)
				.collect(toList());
	}

	private ExternalLanguageSkillDto createLanguageSkillDto(String spracheCode, String muendlichCode, String schriftlichCode) {
		if (hasText(spracheCode)) {
			final String languageIsoCode;
			final LanguageLevel spokenLevel;
			final LanguageLevel writtenLevel;

			if (LANGUAGES.getRight(spracheCode) != null) {
				languageIsoCode = LANGUAGES.getRight(spracheCode);
				spokenLevel = LANGUAGE_LEVEL.getRight(muendlichCode);
				writtenLevel = LANGUAGE_LEVEL.getRight(schriftlichCode);

			} else if (LANGUAGES.getLeft(spracheCode) != null) {
				languageIsoCode = spracheCode;
				spokenLevel = EnumUtils.getEnum(LanguageLevel.class, muendlichCode);
				writtenLevel = EnumUtils.getEnum(LanguageLevel.class, schriftlichCode);

			} else {
				languageIsoCode = null;
				spokenLevel = null;
				writtenLevel = null;
			}

			if (hasText(languageIsoCode)) {
				return new ExternalLanguageSkillDto()
						.setLanguageIsoCode(languageIsoCode)
						.setSpokenLevel(spokenLevel)
						.setWrittenLevel(writtenLevel);
			} else {
				LOGGER.warn("No languageIsoCode was found for: {} ", spracheCode);
			}
		}
		return null;
	}

	private String createProfessionCodes(Oste externalJobAdvertisement) {
		List<Integer> berufsBezeichnungen = externalJobAdvertisement.getBerufsBezeichnungen();
		if ((berufsBezeichnungen != null) && !berufsBezeichnungen.isEmpty()) {
			return berufsBezeichnungen.stream()
					.map(String::valueOf)
					.collect(Collectors.joining(","));
		}
		return null;
	}

	private List<ExternalOccupationDto> createOccupations(Oste externalJobAdvertisement) {
		List<ExternalOccupationDto> occupations = new ArrayList<>();
		if (hasText(externalJobAdvertisement.getBq1AvamBerufNrNew())) {
			occupations.add(new ExternalOccupationDto()
					.setAvamOccupationCode(fallbackAwareAvamOccuptionCode(externalJobAdvertisement.getBq1AvamBerufNrNew()))
					.setWorkExperience(resolveExperience(externalJobAdvertisement.getBq1ErfahrungCode()))
					.setEducationCode(externalJobAdvertisement.getBq1AusbildungCode())
					.setQualificationCode(resolveQualification(externalJobAdvertisement.getBq1QualifikationCode()))
			);
		}
		if (hasText(externalJobAdvertisement.getBq2AvamBerufNrNew())) {
			occupations.add(new ExternalOccupationDto()
					.setAvamOccupationCode(fallbackAwareAvamOccuptionCode(externalJobAdvertisement.getBq2AvamBerufNrNew()))
					.setWorkExperience(resolveExperience(externalJobAdvertisement.getBq2ErfahrungCode()))
					.setEducationCode(externalJobAdvertisement.getBq2AusbildungCode())
					.setQualificationCode(resolveQualification(externalJobAdvertisement.getBq2QualifikationCode()))
			);
		}
		if (hasText(externalJobAdvertisement.getBq3AvamBerufNrNew())) {
			occupations.add(new ExternalOccupationDto()
					.setAvamOccupationCode(fallbackAwareAvamOccuptionCode(externalJobAdvertisement.getBq3AvamBerufNrNew()))
					.setWorkExperience(resolveExperience(externalJobAdvertisement.getBq3ErfahrungCode()))
					.setEducationCode(externalJobAdvertisement.getBq3AusbildungCode())
					.setQualificationCode(resolveQualification(externalJobAdvertisement.getBq3QualifikationCode()))
			);
		}
		if (occupations.isEmpty()) {
			occupations.add(new ExternalOccupationDto()
					.setAvamOccupationCode(DEFAULT_AVAM_OCCUPATION_CODE)
					.setWorkExperience(null)
					.setEducationCode(null)
					.setQualificationCode(null));
		}
		return occupations;
	}

	private ExternalLocationDto createLocation(Oste externalJobAdvertisement) {
		ExternalLocationDto createLocationDto = extractLocation(externalJobAdvertisement.getArbeitsortText());

		if (createLocationDto == null) {
			return null;
		}

		if (externalJobAdvertisement.getArbeitsortPlz() != null) {
			createLocationDto.setPostalCode(externalJobAdvertisement.getArbeitsortPlz());
		}

		if (createLocationDto.getCountryIsoCode() == null) {
			if (LICHTENSTEIN_ISO_CODE.equals(externalJobAdvertisement.getArbeitsortKanton())) {
				createLocationDto.setCountryIsoCode(LICHTENSTEIN_ISO_CODE);
			} else if (externalJobAdvertisement.getArbeitsortAusland() != null) {
				createLocationDto.setCountryIsoCode(externalJobAdvertisement.getArbeitsortAusland());
			} else {
				createLocationDto.setCountryIsoCode(COUNTRY_ISO_CODE_SWITZERLAND);
			}
		}

		return createLocationDto;
	}

	private ExternalCompanyDto createCompany(Oste externalJobAdvertisement) {
		return new ExternalCompanyDto()
				.setName(externalJobAdvertisement.getUntName())
				.setStreet(externalJobAdvertisement.getUntStrasse())
				.setHouseNumber(externalJobAdvertisement.getUntHausNr())
				.setPostalCode(externalJobAdvertisement.getUntPlz())
				.setCity(externalJobAdvertisement.getUntOrt())
				.setCountryIsoCode(externalJobAdvertisement.getUntLand())
				.setPostOfficeBoxNumber(externalJobAdvertisement.getUntPostfach())
				.setPostOfficeBoxPostalCode(externalJobAdvertisement.getUntPostfachPlz())
				.setPostOfficeBoxCity(externalJobAdvertisement.getUntPostfachOrt())
				.setPhone(sanitizePhoneNumber(externalJobAdvertisement.getUntTelefon(), externalJobAdvertisement.getFingerprint()))
				.setEmail(externalJobAdvertisement.getUntEMail())
				.setWebsite(externalJobAdvertisement.getUntUrl())
				.setSurrogate(false);
	}

	private ExternalContactDto createContact(Oste externalJobAdvertisement) {
		if (hasText(externalJobAdvertisement.getKpAnredeCode()) ||
				hasText(externalJobAdvertisement.getKpVorname()) ||
				hasText(externalJobAdvertisement.getKpName()) ||
				hasText(externalJobAdvertisement.getKpTelefonNr()) ||
				hasText(externalJobAdvertisement.getKpEMail())) {
			return new ExternalContactDto()
					.setSalutation(resolveSalutation(externalJobAdvertisement.getKpAnredeCode()))
					.setFirstName(externalJobAdvertisement.getKpVorname())
					.setLastName(externalJobAdvertisement.getKpName())
					.setPhone(sanitizePhoneNumber(externalJobAdvertisement.getKpTelefonNr(), externalJobAdvertisement.getFingerprint()))
					.setEmail(sanitizeEmail(externalJobAdvertisement.getKpEMail(), externalJobAdvertisement.getFingerprint()))
					.setLanguageIsoCode("de");   // Not defined in this AVAM version
		}
		return null;
	}

	private EmploymentDto createEmployment(Oste externalJobAdvertisement) {
		LocalDate startDate = parseDate(externalJobAdvertisement.getStellenantritt());
		LocalDate endDate = parseDate(externalJobAdvertisement.getVertragsdauer());
		WorkingTimePercentage workingTimePercentage = WorkingTimePercentage.evaluate(externalJobAdvertisement.getPensumVon(), externalJobAdvertisement.getPensumBis());
		return new EmploymentDto()
				.setStartDate(startDate)
				.setEndDate(endDate)
				.setShortEmployment(false)
				.setImmediately(fallbackAwareBoolean(externalJobAdvertisement.isAbSofort(), false))
				.setPermanent(fallbackAwareBoolean(externalJobAdvertisement.isUnbefristet(), (endDate != null)))
				.setWorkloadPercentageMin(workingTimePercentage.getMin())
				.setWorkloadPercentageMax(workingTimePercentage.getMax())
				.setWorkForms(null);
	}

	private ExternalPublicationDto createPublication(Oste externalJobAdvertisement) {
		LocalDate publicationStartDate = parseDate(externalJobAdvertisement.getAnmeldeDatum());
		LocalDate publicationEndDate = parseDate(externalJobAdvertisement.getGueltigkeit());
		return new ExternalPublicationDto()
				.setCompanyAnonymous(fallbackAwareBoolean(externalJobAdvertisement.isWwwAnonym(), false))
				.setPublicationStartDate(determineStartDate(publicationStartDate, publicationEndDate))
				.setPublicationEndDate(determineEndDate(publicationStartDate, publicationEndDate));
	}

	private Salutation resolveSalutation(String kpAnredeCode) {
		final Salutation resolvedSalutation;

		if (hasText(kpAnredeCode)) {
			if (AvamCodeResolver.SALUTATIONS.getRight(kpAnredeCode) != null) {
				resolvedSalutation = AvamCodeResolver.SALUTATIONS.getRight(kpAnredeCode);
			} else {
				resolvedSalutation = EnumUtils.getEnum(Salutation.class, kpAnredeCode);
			}
		} else {
			resolvedSalutation = null;
		}

		return resolvedSalutation != null ? resolvedSalutation : Salutation.MR;
	}

	private WorkExperience resolveExperience(String experienceCode) {
		final WorkExperience resolvedWorkExperience;

		if (AvamCodeResolver.EXPERIENCES.getRight(experienceCode) != null) {
			resolvedWorkExperience = AvamCodeResolver.EXPERIENCES.getRight(experienceCode);
		} else {
			resolvedWorkExperience = EnumUtils.getEnum(WorkExperience.class, experienceCode);
		}

		return resolvedWorkExperience;
	}

	private Qualification resolveQualification(String qualificationCode) {
		final Qualification resolvedQualification;

		if (AvamCodeResolver.QUALIFICATION_CODE.getRight(qualificationCode) != null) {
			resolvedQualification = AvamCodeResolver.QUALIFICATION_CODE.getRight(qualificationCode);
		} else {
			resolvedQualification = EnumUtils.getEnum(Qualification.class, qualificationCode);
		}
		return resolvedQualification;
	}

	private String sanitize(String text) {
		if (hasText(text)) {
			// remove javascript injection and css styles
			String sanitizedText = Jsoup.clean(text, "", Whitelist.basic(), new Document.OutputSettings().prettyPrint(false));

			// replace exotic bullet points with proper dash character
			return sanitizedText.replaceAll("[^\\p{InBasic_Latin}\\p{InLatin-1Supplement}]", "-");
		}
		return text;
	}

	/*
	 * Check for a valid phone number and remove remarks.
	 */
	private String sanitizePhoneNumber(String phone, String fingerPrint) {
		if (hasText(phone)) {
			try {
				Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(phone, "CH");
				if (PhoneNumberUtil.getInstance().isValidNumber(phoneNumber)) {
					return PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
				}
			} catch (NumberParseException e) {
				LOGGER.warn("JobAd fingerprint: {} has invalid phone number: {}", fingerPrint, phone);
				String[] phoneParts = phone.split("[^\\d\\(\\)\\+ ]");
				if (phoneParts.length > 1) {
					return sanitizePhoneNumber(phoneParts[0], fingerPrint);
				}
			}
		}
		return null;
	}

	private String sanitizeEmail(String testObject, String fingerPrint) {
		if (hasText(testObject)) {
			String email = trimAllWhitespace(testObject).replace("'", "");
			if (EMAIL_VALIDATOR.isValid(email, null)) {
				return email;
			} else {
				LOGGER.warn("JobAd fingerprint: {} has invalid email: {}", fingerPrint, testObject);
			}
		}
		return null;
	}

	private ExternalLocationDto extractLocation(String localityText) {
		if (hasText(localityText)) {
			Matcher countryZipCodeCityMatcher = COUNTRY_ZIPCODE_CITY_PATTERN.matcher(localityText);
			if (countryZipCodeCityMatcher.find()) {
				String countryIsoCode = countryZipCodeCityMatcher.group(2);
				if (hasText(countryIsoCode)) {
					countryIsoCode = countryIsoCode.substring(0, 2);
					if ("FL".equals(countryIsoCode)) {
						countryIsoCode = LICHTENSTEIN_ISO_CODE;
					}
				}
				String postalCode = StringUtils.trimWhitespace(countryZipCodeCityMatcher.group(3));
				String city = StringUtils.trimWhitespace(countryZipCodeCityMatcher.group(4));
				if (hasText(city)) {
					Matcher cityCantonMatcher = CITY_CANTON_PATTERN.matcher(city);
					if (cityCantonMatcher.find()) {
						city = StringUtils.trimWhitespace(cityCantonMatcher.group(1));
					}
				}
				return new ExternalLocationDto()
						.setRemarks(null)
						.setCity(city)
						.setPostalCode(postalCode)
						.setCountryIsoCode(countryIsoCode);
			}
		}
		return null;
	}

	private LocalDate parseDate(String startDate) {
		if (isEmpty(startDate)) {
			return null;
		}
		return LocalDate.parse(startDate, DATE_FORMATTER);
	}

	private boolean fallbackAwareBoolean(Boolean value, boolean defaultValue) {
		return (value != null) ? value : defaultValue;
	}

	private String fallbackAwareAvamOccuptionCode(String avamOccupationCode) {
		return hasText(avamOccupationCode) ? avamOccupationCode : DEFAULT_AVAM_OCCUPATION_CODE;
	}

}
