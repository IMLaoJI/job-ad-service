package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ApplyChannelDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.CompanyDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobDescriptionDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LanguageSkillDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.OccupationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.LanguageIsoCode;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singletonList;

@Valid
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

    public String getStellennummerAvam() {
        return stellennummerAvam;
    }

    public AvamCreateJobAdvertisementDto setStellennummerAvam(String stellennummerAvam) {
        this.stellennummerAvam = stellennummerAvam;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public AvamCreateJobAdvertisementDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AvamCreateJobAdvertisementDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getLanguageIsoCode() {
        return languageIsoCode;
    }

    public AvamCreateJobAdvertisementDto setLanguageIsoCode(String languageIsoCode) {
        this.languageIsoCode = languageIsoCode;
        return this;
    }

    public String getNumberOfJobs() {
        return numberOfJobs;
    }

    public AvamCreateJobAdvertisementDto setNumberOfJobs(String numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
        return this;
    }

    public boolean isReportingObligation() {
        return reportingObligation;
    }

    public AvamCreateJobAdvertisementDto setReportingObligation(boolean reportingObligation) {
        this.reportingObligation = reportingObligation;
        return this;
    }

    public LocalDate getReportingObligationEndDate() {
        return reportingObligationEndDate;
    }

    public AvamCreateJobAdvertisementDto setReportingObligationEndDate(LocalDate reportingObligationEndDate) {
        this.reportingObligationEndDate = reportingObligationEndDate;
        return this;
    }

    public String getJobCenterCode() {
        return jobCenterCode;
    }



    public AvamCreateJobAdvertisementDto setJobCenterCode(String jobCenterCode) {
        this.jobCenterCode = jobCenterCode;
        return this;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public AvamCreateJobAdvertisementDto setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
        return this;
    }

    public EmploymentDto getEmployment() {
        return employment;
    }

    public AvamCreateJobAdvertisementDto setEmployment(EmploymentDto employment) {
        this.employment = employment;
        return this;
    }

    public ApplyChannelDto getApplyChannel() {
        return applyChannel;
    }

    public AvamCreateJobAdvertisementDto setApplyChannel(ApplyChannelDto applyChannel) {
        this.applyChannel = applyChannel;
        return this;
    }

    public CompanyDto getCompany() {
        return company;
    }

    public AvamCreateJobAdvertisementDto setCompany(CompanyDto company) {
        this.company = company;
        return this;
    }

    public ContactDto getContact() {
        return contact;
    }

    public AvamCreateJobAdvertisementDto setContact(ContactDto contact) {
        this.contact = contact;
        return this;
    }

    public PublicContactDto getPublicContact() {
        return publicContact;
    }

    public AvamCreateJobAdvertisementDto setPublicContact(PublicContactDto publicContact) {
        this.publicContact = publicContact;
        return this;
    }

    public CreateLocationDto getLocation() {
        return location;
    }

    public AvamCreateJobAdvertisementDto setLocation(CreateLocationDto location) {
        this.location = location;
        return this;
    }

    public List<OccupationDto> getOccupations() {
        return occupations;
    }

    public AvamCreateJobAdvertisementDto setOccupations(List<OccupationDto> occupations) {
        this.occupations = occupations;
        return this;
    }

    public List<LanguageSkillDto> getLanguageSkills() {
        return languageSkills;
    }

    public AvamCreateJobAdvertisementDto setLanguageSkills(List<LanguageSkillDto> languageSkills) {
        this.languageSkills = languageSkills;
        return this;
    }

    public PublicationDto getPublication() {
        return publication;
    }

    public AvamCreateJobAdvertisementDto setPublication(PublicationDto publication) {
        this.publication = publication;
        return this;
    }

    public String getJobCenterUserId() {
        return jobCenterUserId;
    }

    public AvamCreateJobAdvertisementDto setJobCenterUserId(String jobCenterUserId) {
        this.jobCenterUserId = jobCenterUserId;
        return this;
    }

    public static CreateJobAdvertisementDto toDto(AvamCreateJobAdvertisementDto createJobAdvertisementDto) {
        return new CreateJobAdvertisementDto()
                .setReportToAvam(false) // TODO: 07/03/2019 fago check this
                .setExternalUrl("") // TODO: 07/03/2019 fago check this
                .setExternalReference("")  // TODO: 07/03/2019 fago check this
                .setStellennummerAvam(createJobAdvertisementDto.getStellennummerAvam())
                .setReportingObligation(createJobAdvertisementDto.isReportingObligation())
                .setReportingObligationEndDate(createJobAdvertisementDto.getReportingObligationEndDate())
                .setJobCenterCode(createJobAdvertisementDto.getJobCenterCode())
                .setJobCenterUserId(createJobAdvertisementDto.getJobCenterUserId())
                .setApprovalDate(createJobAdvertisementDto.getApprovalDate())
                .setContact(createJobAdvertisementDto.getContact())
                .setPublication(createJobAdvertisementDto.getPublication())
                .setNumberOfJobs(createJobAdvertisementDto.getNumberOfJobs())
                .setJobDescriptions(
                        singletonList(
                                new JobDescriptionDto()
                                        .setDescription(createJobAdvertisementDto.getDescription())
                                        .setTitle(createJobAdvertisementDto.getTitle())
                                        .setLanguageIsoCode(createJobAdvertisementDto.getLanguageIsoCode())
                        )
                )
                .setCompany(createJobAdvertisementDto.getCompany())
                .setEmployer(null) // TODO: 08/03/2019 fago check this
                .setEmployment(createJobAdvertisementDto.getEmployment())
                .setLocation(createJobAdvertisementDto.getLocation())
                .setOccupations(createJobAdvertisementDto.getOccupations())
                .setLanguageSkills(createJobAdvertisementDto.getLanguageSkills())
                .setApplyChannel(createJobAdvertisementDto.getApplyChannel())
                .setPublicContact(createJobAdvertisementDto.getPublicContact());
    }


    @Override
    public String toString() {
        return "AvamCreateJobAdvertisementDto{" +
                "stellennummerAvam='" + stellennummerAvam + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", languageIsoCode='" + languageIsoCode + '\'' +
                ", numberOfJobs='" + numberOfJobs + '\'' +
                ", reportingObligation=" + reportingObligation +
                ", reportingObligationEndDate=" + reportingObligationEndDate +
                ", jobCenterCode='" + jobCenterCode + '\'' +
                ", approvalDate=" + approvalDate +
                ", employment=" + employment +
                ", applyChannel=" + applyChannel +
                ", company=" + company +
                ", contact=" + contact +
                ", publicContact=" + publicContact +
                ", location=" + location +
                ", occupations=" + occupations +
                ", languageSkills=" + languageSkills +
                ", publication=" + publication +
                '}';
    }
}
