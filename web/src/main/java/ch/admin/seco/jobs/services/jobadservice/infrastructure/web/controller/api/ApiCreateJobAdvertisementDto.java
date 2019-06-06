package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class ApiCreateJobAdvertisementDto {

    private boolean reportToAvam;

    @Size(max = 1024)
    private String externalUrl;

    @Size(max = 255)
    private String externalReference;

    @Valid
    @NotNull
    private ApiContactDto contact;

    @Valid
    @NotNull
    private ApiPublicationDto publication;

    @Size(max = 3)
    @Pattern(regexp = "[0-9]*")
    private String numberOfJobs;

    @Valid
    @NotEmpty
    private List<ApiJobDescriptionDto> jobDescriptions;

    @Valid
    @NotNull
    private ApiCompanyDto company;

    @Valid
    private ApiEmployerDto employer;

    @Valid
    @NotNull
    private ApiEmploymentDto employment;

    @Valid
    @NotNull
    private ApiCreateLocationDto location;

    @Valid
    @NotNull
    private ApiOccupationDto occupation;

    @Valid
    private List<ApiLanguageSkillDto> languageSkills;

    @Valid
    @NotNull
    private ApiApplyChannelDto applyChannel;

    @Valid
    private ApiPublicContactDto publicContact;

    public boolean isReportToAvam() {
        return reportToAvam;
    }

    public ApiCreateJobAdvertisementDto setReportToAvam(boolean reportToAvam) {
        this.reportToAvam = reportToAvam;
        return this;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public ApiCreateJobAdvertisementDto setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
        return this;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public ApiCreateJobAdvertisementDto setExternalReference(String externalReference) {
        this.externalReference = externalReference;
        return this;
    }

    public ApiContactDto getContact() {
        return contact;
    }

    public ApiCreateJobAdvertisementDto setContact(ApiContactDto contact) {
        this.contact = contact;
        return this;
    }

    public ApiPublicationDto getPublication() {
        return publication;
    }

    public ApiCreateJobAdvertisementDto setPublication(ApiPublicationDto publication) {
        this.publication = publication;
        return this;
    }

    public String getNumberOfJobs() {
        return numberOfJobs;
    }

    public ApiCreateJobAdvertisementDto setNumberOfJobs(String numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
        return this;
    }

    public List<ApiJobDescriptionDto> getJobDescriptions() {
        return jobDescriptions;
    }

    public ApiCreateJobAdvertisementDto setJobDescriptions(List<ApiJobDescriptionDto> jobDescriptions) {
        this.jobDescriptions = jobDescriptions;
        return this;
    }

    public ApiCompanyDto getCompany() {
        return company;
    }

    public ApiCreateJobAdvertisementDto setCompany(ApiCompanyDto company) {
        this.company = company;
        return this;
    }

    public ApiEmployerDto getEmployer() {
        return employer;
    }

    public ApiCreateJobAdvertisementDto setEmployer(ApiEmployerDto employer) {
        this.employer = employer;
        return this;
    }

    public ApiEmploymentDto getEmployment() {
        return employment;
    }

    public ApiCreateJobAdvertisementDto setEmployment(ApiEmploymentDto employment) {
        this.employment = employment;
        return this;
    }

    public ApiCreateLocationDto getLocation() {
        return location;
    }

    public ApiCreateJobAdvertisementDto setLocation(ApiCreateLocationDto location) {
        this.location = location;
        return this;
    }

    public ApiOccupationDto getOccupation() {
        return occupation;
    }

    public ApiCreateJobAdvertisementDto setOccupation(ApiOccupationDto occupation) {
        this.occupation = occupation;
        return this;
    }

    public List<ApiLanguageSkillDto> getLanguageSkills() {
        return languageSkills;
    }

    public ApiCreateJobAdvertisementDto setLanguageSkills(List<ApiLanguageSkillDto> languageSkills) {
        this.languageSkills = languageSkills;
        return this;
    }

    public ApiApplyChannelDto getApplyChannel() {
        return applyChannel;
    }

    public ApiCreateJobAdvertisementDto setApplyChannel(ApiApplyChannelDto applyChannel) {
        this.applyChannel = applyChannel;
        return this;
    }

    public ApiPublicContactDto getPublicContact() {
        return publicContact;
    }

    public ApiCreateJobAdvertisementDto setPublicContact(ApiPublicContactDto publicContact) {
        this.publicContact = publicContact;
        return this;
    }

    @AssertTrue
    private boolean isEmployerValidationRequired() {
        if (this.getCompany().isSurrogate() && (this.getEmployer() == null)) {
                return false;
        }
        return true;
    }
}
