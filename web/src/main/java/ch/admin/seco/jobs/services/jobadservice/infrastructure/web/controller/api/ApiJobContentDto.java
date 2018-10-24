package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import javax.validation.Valid;
import java.util.List;

public class ApiJobContentDto {

    private String externalUrl;

    private String numberOfJobs;

    @Valid
    private List<ApiJobDescriptionDto> jobDescriptions;

    @Valid
    private ApiCompanyDto company;

    @Valid
    private ApiEmploymentDto employment;

    @Valid
    private ApiLocationDto location;

    @Valid
    private List<ApiOccupationDto> occupations;

    @Valid
    private List<ApiLanguageSkillDto> languageSkills;

    @Valid
    private ApiApplyChannelDto applyChannel;

    @Valid
    private ApiPublicContactDto publicContact;

    public String getExternalUrl() {
        return externalUrl;
    }

    public ApiJobContentDto setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
        return this;
    }

    public String getNumberOfJobs() {
        return numberOfJobs;
    }

    public ApiJobContentDto setNumberOfJobs(String numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
        return this;
    }

    public List<ApiJobDescriptionDto> getJobDescriptions() {
        return jobDescriptions;
    }

    public ApiJobContentDto setJobDescriptions(List<ApiJobDescriptionDto> jobDescriptions) {
        this.jobDescriptions = jobDescriptions;
        return this;
    }

    public ApiCompanyDto getCompany() {
        return company;
    }

    public ApiJobContentDto setCompany(ApiCompanyDto company) {
        this.company = company;
        return this;
    }

    public ApiEmploymentDto getEmployment() {
        return employment;
    }

    public ApiJobContentDto setEmployment(ApiEmploymentDto employment) {
        this.employment = employment;
        return this;
    }

    public ApiLocationDto getLocation() {
        return location;
    }

    public ApiJobContentDto setLocation(ApiLocationDto location) {
        this.location = location;
        return this;
    }

    public List<ApiOccupationDto> getOccupations() {
        return occupations;
    }

    public ApiJobContentDto setOccupations(List<ApiOccupationDto> occupations) {
        this.occupations = occupations;
        return this;
    }

    public List<ApiLanguageSkillDto> getLanguageSkills() {
        return languageSkills;
    }

    public ApiJobContentDto setLanguageSkills(List<ApiLanguageSkillDto> languageSkills) {
        this.languageSkills = languageSkills;
        return this;
    }

    public ApiApplyChannelDto getApplyChannel() {
        return applyChannel;
    }

    public ApiJobContentDto setApplyChannel(ApiApplyChannelDto applyChannel) {
        this.applyChannel = applyChannel;
        return this;
    }

    public ApiPublicContactDto getPublicContact() {
        return publicContact;
    }

    public ApiJobContentDto setPublicContact(ApiPublicContactDto publicContact) {
        this.publicContact = publicContact;
        return this;
    }
}
