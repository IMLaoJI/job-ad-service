package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.*;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.CompanyDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LanguageSkillDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.OccupationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;

public class ExternalJobAdvertisementDto {

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

    private LocalDate publicationStartDate;

    private LocalDate publicationEndDate;

    private boolean companyAnonymous;

    public List<LanguageSkillDto> toLanguageSkillDtos() {
        if (this.languageSkills == null) {
            return Collections.emptyList();
        }
        return this.languageSkills.stream()
                .map(ExternalLanguageSkillDto::toLanguageSkillDto)
                .collect(Collectors.toList());
    }

    public ContactDto toContactDto() {
        if (contact == null) {
            return null;
        }
        return contact.toContactDto();
    }

    public PublicContactDto toPublicContactDto() {
        if (contact == null) {
            return null;
        }
        return contact.toPublicContactDto();
    }

    public CompanyDto toCompanyDto() {
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

        return occupations
                .stream()
                .map(ExternalOccupationDto::toOccupationDto)
                .collect(Collectors.toList());
    }

    public String getStellennummerEgov() {
        return stellennummerEgov;
    }

    public ExternalJobAdvertisementDto setStellennummerEgov(String stellennummerEgov) {
        this.stellennummerEgov = stellennummerEgov;
        return this;
    }

    public String getStellennummerAvam() {
        return stellennummerAvam;
    }

    public ExternalJobAdvertisementDto setStellennummerAvam(String stellennummerAvam) {
        this.stellennummerAvam = stellennummerAvam;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ExternalJobAdvertisementDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ExternalJobAdvertisementDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getNumberOfJobs() {
        return numberOfJobs;
    }

    public ExternalJobAdvertisementDto setNumberOfJobs(String numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
        return this;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public ExternalJobAdvertisementDto setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
        return this;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public ExternalJobAdvertisementDto setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
        return this;
    }

    public String getJobCenterCode() {
        return jobCenterCode;
    }

    public ExternalJobAdvertisementDto setJobCenterCode(String jobCenterCode) {
        this.jobCenterCode = jobCenterCode;
        return this;
    }

    public ExternalContactDto getContact() {
        return contact;
    }

    public ExternalJobAdvertisementDto setContact(ExternalContactDto contact) {
        this.contact = contact;
        return this;
    }

    public EmploymentDto getEmployment() {
        return employment;
    }

    public ExternalJobAdvertisementDto setEmployment(EmploymentDto employment) {
        this.employment = employment;
        return this;
    }

    public ExternalCompanyDto getCompany() {
        return company;
    }

    public ExternalJobAdvertisementDto setCompany(ExternalCompanyDto company) {
        this.company = company;
        return this;
    }

    public ExternalLocationDto getLocation() {
        return location;
    }

    public ExternalJobAdvertisementDto setLocation(ExternalLocationDto location) {
        this.location = location;
        return this;
    }

    public List<ExternalOccupationDto> getOccupations() {
        return occupations;
    }

    public ExternalJobAdvertisementDto setOccupations(List<ExternalOccupationDto> occupations) {
        this.occupations = occupations;
        return this;
    }

    public String getProfessionCodes() {
        return professionCodes;
    }

    public ExternalJobAdvertisementDto setProfessionCodes(String professionCodes) {
        this.professionCodes = professionCodes;
        return this;
    }

    public List<ExternalLanguageSkillDto> getLanguageSkills() {
        return languageSkills;
    }

    public ExternalJobAdvertisementDto setLanguageSkills(List<ExternalLanguageSkillDto> languageSkills) {
        this.languageSkills = languageSkills;
        return this;
    }

    public LocalDate getPublicationStartDate() {
        return publicationStartDate;
    }

    public ExternalJobAdvertisementDto setPublicationStartDate(LocalDate publicationStartDate) {
        this.publicationStartDate = publicationStartDate;
        return this;
    }

    public LocalDate getPublicationEndDate() {
        return publicationEndDate;
    }

    public ExternalJobAdvertisementDto setPublicationEndDate(LocalDate publicationEndDate) {
        this.publicationEndDate = publicationEndDate;
        return this;
    }

    public boolean isCompanyAnonymous() {
        return companyAnonymous;
    }

    public ExternalJobAdvertisementDto setCompanyAnonymous(boolean companyAnonymous) {
        this.companyAnonymous = companyAnonymous;
        return this;
    }
}
