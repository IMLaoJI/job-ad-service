package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ApplyChannelDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.CompanyDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LanguageSkillDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.OccupationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.LanguageIsoCode;

public class AvamCreateJobAdvertisementDto {

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


    protected AvamCreateJobAdvertisementDto() {
        // For reflection libs
    }

    public AvamCreateJobAdvertisementDto(String stellennummerAvam, String title, String description, String languageIsoCode, String numberOfJobs,
                                         boolean reportingObligation, LocalDate reportingObligationEndDate, String jobCenterCode, String jobCenterUserId, LocalDate approvalDate,
                                         EmploymentDto employment, ApplyChannelDto applyChannel, CompanyDto company, ContactDto contact, CreateLocationDto location,
                                         List<OccupationDto> occupations, List<LanguageSkillDto> languageSkills, PublicationDto publication, PublicContactDto publicContact
    ) {
        this.stellennummerAvam = stellennummerAvam;
        this.title = title;
        this.description = description;
        this.languageIsoCode = languageIsoCode;
        this.numberOfJobs = numberOfJobs;
        this.reportingObligation = reportingObligation;
        this.reportingObligationEndDate = reportingObligationEndDate;
        this.jobCenterCode = jobCenterCode;
        this.jobCenterUserId = jobCenterUserId;
        this.approvalDate = approvalDate;
        this.employment = employment;
        this.applyChannel = applyChannel;
        this.company = company;
        this.contact = contact;
        this.location = location;
        this.occupations = occupations;
        this.languageSkills = languageSkills;
        this.publication = publication;
        this.publicContact = publicContact;

    }

    public String getStellennummerAvam() {
        return stellennummerAvam;
    }

    public void setStellennummerAvam(String stellennummerAvam) {
        this.stellennummerAvam = stellennummerAvam;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguageIsoCode() {
        return languageIsoCode;
    }

    public void setLanguageIsoCode(String languageIsoCode) {
        this.languageIsoCode = languageIsoCode;
    }

    public String getNumberOfJobs() {
        return numberOfJobs;
    }

    public void setNumberOfJobs(String numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
    }

    public String getJobCenterCode() {
        return jobCenterCode;
    }

    public void setJobCenterCode(String jobCenterCode) {
        this.jobCenterCode = jobCenterCode;
    }

    public String getJobCenterUserId() {
        return jobCenterUserId;
    }

    public void setJobCenterUserId(String jobCenterUserId) {
        this.jobCenterUserId = jobCenterUserId;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public boolean isReportingObligation() {
        return reportingObligation;
    }

    public void setReportingObligation(boolean reportingObligation) {
        this.reportingObligation = reportingObligation;
    }

    public LocalDate getReportingObligationEndDate() {
        return reportingObligationEndDate;
    }

    public void setReportingObligationEndDate(LocalDate reportingObligationEndDate) {
        this.reportingObligationEndDate = reportingObligationEndDate;
    }

    public EmploymentDto getEmployment() {
        return employment;
    }

    public void setEmployment(EmploymentDto employment) {
        this.employment = employment;
    }

    public ApplyChannelDto getApplyChannel() {
        return applyChannel;
    }

    public void setApplyChannel(ApplyChannelDto applyChannel) {
        this.applyChannel = applyChannel;
    }

    public CompanyDto getCompany() {
        return company;
    }

    public void setCompany(CompanyDto company) {
        this.company = company;
    }

    public ContactDto getContact() {
        return contact;
    }

    public void setContact(ContactDto contact) {
        this.contact = contact;
    }

    public CreateLocationDto getLocation() {
        return location;
    }

    public void setLocation(CreateLocationDto location) {
        this.location = location;
    }

    public List<OccupationDto> getOccupations() {
        return occupations;
    }

    public void setOccupations(List<OccupationDto> occupations) {
        this.occupations = occupations;
    }

    public List<LanguageSkillDto> getLanguageSkills() {
        return languageSkills;
    }

    public void setLanguageSkills(List<LanguageSkillDto> languageSkills) {
        this.languageSkills = languageSkills;
    }

    public PublicationDto getPublication() {
        return publication;
    }

    public void setPublication(PublicationDto publication) {
        this.publication = publication;
    }

    public PublicContactDto getPublicContact() {
        return publicContact;
    }

    public void setPublicContact(PublicContactDto publicContact) {
        this.publicContact = publicContact;
    }
}
