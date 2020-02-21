package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.webform;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.*;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.util.PhonNumberUtil.sanitizePhoneNumber;

public class WebformCreateJobAdvertisementDto {

    private boolean reportToAvam;

    private String externalUrl;

    private String externalReference;

    @Valid
    @NotNull
    private ContactDto contact;

    @Valid
    @NotNull
    private PublicationDto publication;

    private String numberOfJobs;

    @Valid
    @NotNull
    @NotEmpty
    private List<JobDescriptionDto> jobDescriptions;

    @Valid
    @NotNull
    private WebformCreateCompanyDto company;

    @Valid
    private EmployerDto employer;

    @Valid
    @NotNull
    private EmploymentDto employment;

    @Valid
    @NotNull
    private CreateLocationDto location;

    @Valid
    @NotNull
    private OccupationDto occupation;

    @Valid
    private List<LanguageSkillDto> languageSkills;

    @Valid
    @NotNull
    private WebformCreateApplyChannelDto applyChannel;

    @Valid
    @NotNull
    private PublicContactDto publicContact;

    public boolean isReportToAvam() {
        return reportToAvam;
    }

    public WebformCreateJobAdvertisementDto setReportToAvam(boolean reportToAvam) {
        this.reportToAvam = reportToAvam;
        return this;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public WebformCreateJobAdvertisementDto setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
        return this;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public WebformCreateJobAdvertisementDto setExternalReference(String externalReference) {
        this.externalReference = externalReference;
        return this;
    }

    public ContactDto getContact() {
        return contact;
    }

    public WebformCreateJobAdvertisementDto setContact(ContactDto contact) {
        contact.setPhone(sanitizePhoneNumber(contact.getPhone(), PhoneNumberUtil.PhoneNumberFormat.E164));
        this.contact = contact;
        return this;
    }

    public PublicationDto getPublication() {
        return publication;
    }

    public WebformCreateJobAdvertisementDto setPublication(PublicationDto publication) {
        this.publication = publication;
        return this;
    }

    public String getNumberOfJobs() {
        return numberOfJobs;
    }

    public WebformCreateJobAdvertisementDto setNumberOfJobs(String numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
        return this;
    }

    public List<JobDescriptionDto> getJobDescriptions() {
        return jobDescriptions;
    }

    public WebformCreateJobAdvertisementDto setJobDescriptions(List<JobDescriptionDto> jobDescriptions) {
        this.jobDescriptions = jobDescriptions;
        return this;
    }

    public WebformCreateCompanyDto getCompany() {
        return company;
    }

    public WebformCreateJobAdvertisementDto setCompany(WebformCreateCompanyDto company) {
        this.company = company;
        return this;
    }

    public EmployerDto getEmployer() {
        return employer;
    }

    public WebformCreateJobAdvertisementDto setEmployer(EmployerDto employer) {
        this.employer = employer;
        return this;
    }

    public EmploymentDto getEmployment() {
        return employment;
    }

    public WebformCreateJobAdvertisementDto setEmployment(EmploymentDto employment) {
        this.employment = employment;
        return this;
    }

    public CreateLocationDto getLocation() {
        return location;
    }

    public WebformCreateJobAdvertisementDto setLocation(CreateLocationDto location) {
        this.location = location;
        return this;
    }

    public OccupationDto getOccupation() {
        return occupation;
    }

    public WebformCreateJobAdvertisementDto setOccupation(OccupationDto occupation) {
        this.occupation = occupation;
        return this;
    }

    public List<LanguageSkillDto> getLanguageSkills() {
        return languageSkills;
    }

    public WebformCreateJobAdvertisementDto setLanguageSkills(List<LanguageSkillDto> languageSkills) {
        this.languageSkills = languageSkills;
        return this;
    }

    public WebformCreateApplyChannelDto getApplyChannel() {
        return applyChannel;
    }

    public WebformCreateJobAdvertisementDto setApplyChannel(WebformCreateApplyChannelDto applyChannel) {
        applyChannel.setPhoneNumber(sanitizePhoneNumber(applyChannel.getPhoneNumber(), PhoneNumberUtil.PhoneNumberFormat.E164));
        this.applyChannel = applyChannel;
        return this;
    }

    public PublicContactDto getPublicContact() {
        return publicContact;
    }

    public WebformCreateJobAdvertisementDto setPublicContact(PublicContactDto publicContact) {
        publicContact.setPhone(sanitizePhoneNumber(publicContact.getPhone(), PhoneNumberUtil.PhoneNumberFormat.E164));
        this.publicContact = publicContact;
        return this;
    }
}
