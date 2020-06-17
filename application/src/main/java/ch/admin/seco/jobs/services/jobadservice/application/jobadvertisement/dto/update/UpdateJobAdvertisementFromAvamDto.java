package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.*;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.LanguageIsoCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class UpdateJobAdvertisementFromAvamDto {

    @NotEmpty
    private String stellennummerAvam;

    @NotEmpty
    private String title;

    private String description;

    @NotBlank
    @LanguageIsoCode
    private String languageIsoCode;

    private String numberOfJobs;

    private boolean reportingObligation;

    private LocalDate reportingObligationEndDate;

    private String jobCenterCode;

    private String jobCenterUserId;

    @NotNull
    private LocalDate approvalDate;

    @NotNull
    private EmploymentDto employment;

    private ApplyChannelDto applyChannel;

    @NotNull
    private CompanyDto company;

    @NotNull
    private ContactDto contact;

    private PublicContactDto publicContact;

    @NotNull
    private CreateLocationDto location;

    @NotEmpty
    private List<OccupationDto> occupations;

    private List<LanguageSkillDto> languageSkills;

    @NotNull
    private PublicationDto publication;

    public String getStellennummerAvam() {
        return stellennummerAvam;
    }

    public UpdateJobAdvertisementFromAvamDto setStellennummerAvam(String stellennummerAvam) {
        this.stellennummerAvam = stellennummerAvam;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public UpdateJobAdvertisementFromAvamDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public UpdateJobAdvertisementFromAvamDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getLanguageIsoCode() {
        return languageIsoCode;
    }

    public UpdateJobAdvertisementFromAvamDto setLanguageIsoCode(String languageIsoCode) {
        this.languageIsoCode = languageIsoCode;
        return this;
    }

    public String getNumberOfJobs() {
        return numberOfJobs;
    }

    public UpdateJobAdvertisementFromAvamDto setNumberOfJobs(String numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
        return this;
    }

    public boolean isReportingObligation() {
        return reportingObligation;
    }

    public UpdateJobAdvertisementFromAvamDto setReportingObligation(boolean reportingObligation) {
        this.reportingObligation = reportingObligation;
        return this;
    }

    public LocalDate getReportingObligationEndDate() {
        return reportingObligationEndDate;
    }

    public UpdateJobAdvertisementFromAvamDto setReportingObligationEndDate(LocalDate reportingObligationEndDate) {
        this.reportingObligationEndDate = reportingObligationEndDate;
        return this;
    }

    public String getJobCenterCode() {
        return jobCenterCode;
    }

    public UpdateJobAdvertisementFromAvamDto setJobCenterCode(String jobCenterCode) {
        this.jobCenterCode = jobCenterCode;
        return this;
    }

    public String getJobCenterUserId() {
        return jobCenterUserId;
    }

    public UpdateJobAdvertisementFromAvamDto setJobCenterUserId(String jobCenterUserId) {
        this.jobCenterUserId = jobCenterUserId;
        return this;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public UpdateJobAdvertisementFromAvamDto setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
        return this;
    }

    public EmploymentDto getEmployment() {
        return employment;
    }

    public UpdateJobAdvertisementFromAvamDto setEmployment(EmploymentDto employment) {
        this.employment = employment;
        return this;
    }

    public ApplyChannelDto getApplyChannel() {
        return applyChannel;
    }

    public UpdateJobAdvertisementFromAvamDto setApplyChannel(ApplyChannelDto applyChannel) {
        this.applyChannel = applyChannel;
        return this;
    }

    public CompanyDto getCompany() {
        return company;
    }

    public UpdateJobAdvertisementFromAvamDto setCompany(CompanyDto company) {
        this.company = company;
        return this;
    }

    public ContactDto getContact() {
        return contact;
    }

    public UpdateJobAdvertisementFromAvamDto setContact(ContactDto contact) {
        this.contact = contact;
        return this;
    }

    public PublicContactDto getPublicContact() {
        return publicContact;
    }

    public UpdateJobAdvertisementFromAvamDto setPublicContact(PublicContactDto publicContact) {
        this.publicContact = publicContact;
        return this;
    }

    public CreateLocationDto getLocation() {
        return location;
    }

    public UpdateJobAdvertisementFromAvamDto setLocation(CreateLocationDto location) {
        this.location = location;
        return this;
    }

    public List<OccupationDto> getOccupations() {
        return occupations;
    }

    public UpdateJobAdvertisementFromAvamDto setOccupations(List<OccupationDto> occupations) {
        this.occupations = occupations;
        return this;
    }

    public List<LanguageSkillDto> getLanguageSkills() {
        return languageSkills;
    }

    public UpdateJobAdvertisementFromAvamDto setLanguageSkills(List<LanguageSkillDto> languageSkills) {
        this.languageSkills = languageSkills;
        return this;
    }

    public PublicationDto getPublication() {
        return publication;
    }

    public UpdateJobAdvertisementFromAvamDto setPublication(PublicationDto publication) {
        this.publication = publication;
        return this;
    }
}
