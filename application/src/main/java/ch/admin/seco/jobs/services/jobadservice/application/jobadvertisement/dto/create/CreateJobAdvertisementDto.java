package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class CreateJobAdvertisementDto {

    private boolean reportToAvam;

    private String externalUrl;

    private String externalReference;

    private String stellennummerAvam;

    private String stellennummerEgov;

    private Boolean reportingObligation;

    private LocalDate reportingObligationEndDate;

    private String jobCenterCode;

    private String jobCenterUserId;

    private LocalDate approvalDate;

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
    private CompanyDto company;

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
    @NotEmpty
    private List<OccupationDto> occupations;

    @Valid
    private List<LanguageSkillDto> languageSkills;

    @Valid
    private ApplyChannelDto applyChannel;

    @Valid
    private PublicContactDto publicContact;

    private String professionCodes;

    private String fingerprint;

    public boolean isReportToAvam() {
        return reportToAvam;
    }

    public CreateJobAdvertisementDto setReportToAvam(boolean reportToAvam) {
        this.reportToAvam = reportToAvam;
        return this;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public CreateJobAdvertisementDto setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
        return this;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public CreateJobAdvertisementDto setExternalReference(String externalReference) {
        this.externalReference = externalReference;
        return this;
    }

    public String getStellennummerAvam() {
        return stellennummerAvam;
    }

    public CreateJobAdvertisementDto setStellennummerAvam(String stellennummerAvam) {
        this.stellennummerAvam = stellennummerAvam;
        return this;
    }

    public Boolean isReportingObligation() {
        return reportingObligation;
    }

    public CreateJobAdvertisementDto setReportingObligation(Boolean reportingObligation) {
        this.reportingObligation = reportingObligation;
        return this;
    }

    public LocalDate getReportingObligationEndDate() {
        return reportingObligationEndDate;
    }

    public CreateJobAdvertisementDto setReportingObligationEndDate(LocalDate reportingObligationEndDate) {
        this.reportingObligationEndDate = reportingObligationEndDate;
        return this;
    }

    public String getJobCenterCode() {
        return jobCenterCode;
    }

    public CreateJobAdvertisementDto setJobCenterCode(String jobCenterCode) {
        this.jobCenterCode = jobCenterCode;
        return this;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public CreateJobAdvertisementDto setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
        return this;
    }

    public ContactDto getContact() {
        return contact;
    }

    public CreateJobAdvertisementDto setContact(ContactDto contact) {
        this.contact = contact;
        return this;
    }

    public PublicationDto getPublication() {
        return publication;
    }

    public CreateJobAdvertisementDto setPublication(PublicationDto publication) {
        this.publication = publication;
        return this;
    }

    public String getNumberOfJobs() {
        return numberOfJobs;
    }

    public CreateJobAdvertisementDto setNumberOfJobs(String numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
        return this;
    }

    public List<JobDescriptionDto> getJobDescriptions() {
        return jobDescriptions;
    }

    public CreateJobAdvertisementDto setJobDescriptions(List<JobDescriptionDto> jobDescriptions) {
        this.jobDescriptions = jobDescriptions;
        return this;
    }

    public CompanyDto getCompany() {
        return company;
    }

    public CreateJobAdvertisementDto setCompany(CompanyDto company) {
        this.company = company;
        return this;
    }

    public EmployerDto getEmployer() {
        return employer;
    }

    public CreateJobAdvertisementDto setEmployer(EmployerDto employer) {
        this.employer = employer;
        return this;
    }

    public EmploymentDto getEmployment() {
        return employment;
    }

    public CreateJobAdvertisementDto setEmployment(EmploymentDto employment) {
        this.employment = employment;
        return this;
    }

    public CreateLocationDto getLocation() {
        return location;
    }

    public CreateJobAdvertisementDto setLocation(CreateLocationDto location) {
        this.location = location;
        return this;
    }

    public List<OccupationDto> getOccupations() {
        return occupations;
    }

    public CreateJobAdvertisementDto setOccupations(List<OccupationDto> occupations) {
        this.occupations = occupations;
        return this;
    }

    public List<LanguageSkillDto> getLanguageSkills() {
        return languageSkills;
    }

    public CreateJobAdvertisementDto setLanguageSkills(List<LanguageSkillDto> languageSkills) {
        this.languageSkills = languageSkills;
        return this;
    }

    public ApplyChannelDto getApplyChannel() {
        return applyChannel;
    }

    public CreateJobAdvertisementDto setApplyChannel(ApplyChannelDto applyChannel) {
        this.applyChannel = applyChannel;
        return this;
    }

    public PublicContactDto getPublicContact() {
        return publicContact;
    }

    public CreateJobAdvertisementDto setPublicContact(PublicContactDto publicContact) {
        this.publicContact = publicContact;
        return this;
    }

    public String getNullsafeJobCenterUserId() {
        if (jobCenterUserId == null) {
            return null;
        }
        return jobCenterUserId;
    }

    public String getJobCenterUserId() {
        return jobCenterUserId;
    }

    public CreateJobAdvertisementDto setJobCenterUserId(String jobCenterUserId) {
        this.jobCenterUserId = jobCenterUserId;
        return this;
    }

    public OccupationDto getSingleOccupation() {
        return occupations.get(0);
    }

    public JobDescriptionDto getSingleJobDescription() {
        return jobDescriptions.get(0);
    }

    public Boolean getReportingObligation() {
        return reportingObligation;
    }

    public String getProfessionCodes() {
        return professionCodes;
    }

    public CreateJobAdvertisementDto setProfessionCodes(String professionCodes) {
        this.professionCodes = professionCodes;
        return this;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public CreateJobAdvertisementDto setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
        return this;
    }

    public String getStellennummerEgov() {
        return stellennummerEgov;
    }

    public CreateJobAdvertisementDto setStellennummerEgov(String stellennummerEgov) {
        this.stellennummerEgov = stellennummerEgov;
        return this;
    }

    @Override
    public String toString() {
        return "CreateJobAdvertisementDto{" +
                "reportToAvam=" + reportToAvam +
                ", externalUrl='" + externalUrl + '\'' +
                ", externalReference='" + externalReference + '\'' +
                ", stellennummerAvam='" + stellennummerAvam + '\'' +
                ", stellennummerEgov='" + stellennummerEgov + '\'' +
                ", reportingObligation=" + reportingObligation +
                ", reportingObligationEndDate=" + reportingObligationEndDate +
                ", JobCenterCode='" + jobCenterCode + '\'' +
                ", JobCenterUserId='" + jobCenterUserId + '\'' +
                ", approvalDate=" + approvalDate +
                ", contact=" + contact +
                ", publication=" + publication +
                ", numberOfJobs='" + numberOfJobs + '\'' +
                ", jobDescriptions=" + jobDescriptions +
                ", company=" + company +
                ", employer=" + employer +
                ", employment=" + employment +
                ", location=" + location +
                ", occupations=" + occupations +
                ", languageSkills=" + languageSkills +
                ", applyChannel=" + applyChannel +
                ", publicContact=" + publicContact +
                ", professionCodes='" + professionCodes + '\'' +
                ", fingerprint='" + fingerprint + '\'' +
                '}';
    }
}
