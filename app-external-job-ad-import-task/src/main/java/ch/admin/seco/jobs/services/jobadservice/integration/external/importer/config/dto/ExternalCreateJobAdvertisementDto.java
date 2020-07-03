package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config.dto;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.CompanyDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobDescriptionDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LanguageSkillDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.OccupationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class ExternalCreateJobAdvertisementDto {

	@Size(max = 11)
	private String stellennummerEgov;

	@Size(max = 11)
	private String stellennummerAvam;

	@NotBlank
	@Size(max = 255)
	private String title;

	@NotBlank
	@Size(max = 10000)
	private String description;

	private String numberOfJobs;

	@NotBlank
	@Size(max = 100)
	private String fingerprint;

	@Size(max = 1024)
	private String externalUrl;

	@Size(max = 5)
	private String jobCenterCode;

	@Valid
	private ExternalContactDto contact; // can be null in the domain

	@Valid
	@NotNull
	private EmploymentDto employment; // can not be null in the domain

	@Valid
	@NotNull
	private ExternalCompanyDto company; // can not be null in the domain

	@Valid
	private ExternalLocationDto location;  // can be null in the domain

	@Valid
	@NotEmpty
	private List<ExternalOccupationDto> occupations; // can not be null or empty in the domain

	private String professionCodes;

	@Valid
	private List<ExternalLanguageSkillDto> languageSkills; // can be null or empty in the domain

	private ExternalPublicationDto publicationDto;

	private List<LanguageSkillDto> toLanguageSkillDtos() {
		if (this.languageSkills == null) {
			return Collections.emptyList();
		}
		return mapLanguageSkills(this.languageSkills);
	}

	private List<JobDescriptionDto> toJobDescriptionDto() {
		if (this.description == null) {
			return Collections.emptyList();

		}
		return
		singletonList(
				new JobDescriptionDto()
						.setDescription(description)
						.setTitle(title)
		);
	}

	private ContactDto toContactDto() {
		if (contact == null) {
			return null;
		}
		return contact.toContactDto();
	}

	private PublicContactDto toPublicContactDto() {
		if (contact == null) {
			return null;
		}
		return contact.toPublicContactDto();
	}

	private CompanyDto toCompanyDto() {
		return company.toCompanyDto();
	}

	public CreateLocationDto toCreateLocationDto() {
		if (location == null) {
			return null;
		}

		return location.toCreateLocationDto();
	}

	public List<OccupationDto> toOccupationDtos() {
		if (occupations == null) {
			return Collections.emptyList();
		}

		return mapOccupations(this.occupations);
	}

	public String getStellennummerEgov() {
		return stellennummerEgov;
	}

	public ExternalCreateJobAdvertisementDto setStellennummerEgov(String stellennummerEgov) {
		this.stellennummerEgov = stellennummerEgov;
		return this;
	}

	public String getStellennummerAvam() {
		return stellennummerAvam;
	}

	public ExternalCreateJobAdvertisementDto setStellennummerAvam(String stellennummerAvam) {
		this.stellennummerAvam = stellennummerAvam;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public ExternalCreateJobAdvertisementDto setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ExternalCreateJobAdvertisementDto setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getNumberOfJobs() {
		return numberOfJobs;
	}

	public ExternalCreateJobAdvertisementDto setNumberOfJobs(String numberOfJobs) {
		this.numberOfJobs = numberOfJobs;
		return this;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public ExternalCreateJobAdvertisementDto setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
		return this;
	}

	public String getExternalUrl() {
		return externalUrl;
	}

	public ExternalCreateJobAdvertisementDto setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
		return this;
	}

	public String getJobCenterCode() {
		return jobCenterCode;
	}

	public ExternalCreateJobAdvertisementDto setJobCenterCode(String jobCenterCode) {
		this.jobCenterCode = jobCenterCode;
		return this;
	}

	public ExternalContactDto getContact() {
		return contact;
	}

	public ExternalCreateJobAdvertisementDto setContact(ExternalContactDto contact) {
		this.contact = contact;
		return this;
	}

	public EmploymentDto getEmployment() {
		return employment;
	}

	public ExternalCreateJobAdvertisementDto setEmployment(EmploymentDto employment) {
		this.employment = employment;
		return this;
	}

	public ExternalCompanyDto getCompany() {
		return company;
	}

	public ExternalCreateJobAdvertisementDto setCompany(ExternalCompanyDto company) {
		this.company = company;
		return this;
	}

	public ExternalLocationDto getLocation() {
		return location;
	}

	public ExternalCreateJobAdvertisementDto setLocation(ExternalLocationDto location) {
		this.location = location;
		return this;
	}

	public List<ExternalOccupationDto> getOccupations() {
		return occupations;
	}

	public ExternalCreateJobAdvertisementDto setOccupations(List<ExternalOccupationDto> occupations) {
		this.occupations = occupations;
		return this;
	}

	public String getProfessionCodes() {
		return professionCodes;
	}

	public ExternalCreateJobAdvertisementDto setProfessionCodes(String professionCodes) {
		this.professionCodes = professionCodes;
		return this;
	}

	public List<ExternalLanguageSkillDto> getLanguageSkills() {
		return languageSkills;
	}

	public ExternalCreateJobAdvertisementDto setLanguageSkills(List<ExternalLanguageSkillDto> languageSkills) {
		this.languageSkills = languageSkills;
		return this;
	}

	public ExternalPublicationDto getPublicationDto() {
		return publicationDto;
	}

	public ExternalCreateJobAdvertisementDto setPublicationDto(ExternalPublicationDto publicationDto) {
		this.publicationDto = publicationDto;
		return this;
	}

	public  CreateJobAdvertisementDto toDto(ExternalCreateJobAdvertisementDto externalCreateJobAdvertisementDto) {
		return new CreateJobAdvertisementDto()
				.setStellennummerAvam(externalCreateJobAdvertisementDto.getStellennummerAvam())
				.setStellennummerAvam(externalCreateJobAdvertisementDto.getStellennummerEgov())
				.setFingerprint(externalCreateJobAdvertisementDto.getFingerprint())
				.setJobCenterCode(externalCreateJobAdvertisementDto.getJobCenterCode())
				.setContact(externalCreateJobAdvertisementDto.toContactDto())
				.setNumberOfJobs(externalCreateJobAdvertisementDto.getNumberOfJobs())
				.setJobDescriptions(externalCreateJobAdvertisementDto.toJobDescriptionDto())
				.setPublicContact(externalCreateJobAdvertisementDto.toPublicContactDto())
				.setCompany(externalCreateJobAdvertisementDto.toCompanyDto())
				.setEmployment(externalCreateJobAdvertisementDto.getEmployment())
				.setLocation(externalCreateJobAdvertisementDto.toCreateLocationDto())
				.setOccupations(externalCreateJobAdvertisementDto.toOccupationDtos())
				.setPublication(externalCreateJobAdvertisementDto.getPublicationDto().toCreatePublicationDto())
				.setLanguageSkills(externalCreateJobAdvertisementDto.toLanguageSkillDtos());
	}

	private static List<LanguageSkillDto> mapLanguageSkills(List<ExternalLanguageSkillDto> languageSkills) {
		return languageSkills.stream()
				.map(ExternalLanguageSkillDto::toLanguageSkillDto)
				.collect(Collectors.toList());
	}

	private static List<OccupationDto> mapOccupations(List<ExternalOccupationDto> occupations) {
		return occupations.stream()
				.map(ExternalOccupationDto::toOccupationDto)
				.collect(Collectors.toList());
	}
}
