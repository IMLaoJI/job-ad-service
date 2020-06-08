package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.application.BusinessLogEvent;
import ch.admin.seco.jobs.services.jobadservice.application.BusinessLogger;
import ch.admin.seco.jobs.services.jobadservice.application.IsSysAdmin;
import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.LocationService;
import ch.admin.seco.jobs.services.jobadservice.application.ProfessionService;
import ch.admin.seco.jobs.services.jobadservice.application.ReportingObligationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.AddressDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ApplyChannelDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.CompanyDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmployerDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobDescriptionDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LanguageSkillDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.OccupationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external.ExternalCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.ApprovalDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.CancellationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.RejectionDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.UpdateJobAdvertisementFromAvamDto;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUser;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.*;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.ContactDisplayStyle;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenterUser;
import ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static ch.admin.seco.jobs.services.jobadservice.application.BusinessLogConstants.STATUS_ADDITIONAL_DATA;
import static ch.admin.seco.jobs.services.jobadservice.application.BusinessLogEventType.JOB_ADVERTISEMENT_ACCESS_EVENT;
import static ch.admin.seco.jobs.services.jobadservice.application.BusinessLogObjectType.JOB_ADVERTISEMENT_LOG;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.INSPECTING;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.REFINING;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.REJECTED;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.hasText;

@Service
@Transactional(rollbackFor = {Exception.class})
public class JobAdvertisementApplicationService {

    private static final int PUBLICATION_MAX_DAYS = 60;

    static final int EXTERN_JOB_AD_REACTIVATION_DAY_NUM = 10;

    static final String COUNTRY_ISO_CODE_SWITZERLAND = "CH";

    private static final Logger LOG = LoggerFactory.getLogger(JobAdvertisementApplicationService.class);

    private static final int START_RANGE_OF_NEW_AVAMCODES = 101001;

    private final CurrentUserContext currentUserContext;

    private final JobAdvertisementRepository jobAdvertisementRepository;

    private final JobAdvertisementFactory jobAdvertisementFactory;

    private final ReportingObligationService reportingObligationService;

    private final LocationService locationService;

    private final ProfessionService professionService;

    private final JobCenterService jobCenterService;

    private final TransactionTemplate transactionTemplate;

    private final BusinessLogger businessLogger;

    private final ExternalJobAdvertisementArchiverService externalJobAdvertisementService;

    @Autowired
    public JobAdvertisementApplicationService(CurrentUserContext currentUserContext,
                                              JobAdvertisementRepository jobAdvertisementRepository,
                                              JobAdvertisementFactory jobAdvertisementFactory,
                                              ReportingObligationService reportingObligationService,
                                              LocationService locationService,
                                              ProfessionService professionService,
                                              JobCenterService jobCenterService,
                                              TransactionTemplate transactionTemplate,
                                              BusinessLogger businessLogger,
                                              ExternalJobAdvertisementArchiverService externalJobAdvertisementSerivce) {
        this.currentUserContext = currentUserContext;
        this.jobAdvertisementRepository = jobAdvertisementRepository;
        this.jobAdvertisementFactory = jobAdvertisementFactory;
        this.reportingObligationService = reportingObligationService;
        this.locationService = locationService;
        this.professionService = professionService;
        this.jobCenterService = jobCenterService;
        this.transactionTemplate = transactionTemplate;
        this.businessLogger = businessLogger;
        this.externalJobAdvertisementService = externalJobAdvertisementSerivce;
    }

    public JobAdvertisementId createFromWebForm(CreateJobAdvertisementDto createJobAdvertisementDto) {
        LOG.debug("Start creating new job ad from WebForm");
        Condition.notNull(createJobAdvertisementDto, "CreateJobAdvertisementDto can't be null");
        LOG.debug("Create '{}'", createJobAdvertisementDto.getJobDescriptions().get(0).getTitle());
        final JobAdvertisementCreator creator = getJobAdvertisementCreatorFromInternal(createJobAdvertisementDto);
        JobAdvertisement jobAdvertisement = jobAdvertisementFactory.createFromWebForm(creator);
        return jobAdvertisement.getId();
    }

    public CreatedJobAdvertisementIdWithTokenDto createFromApi(CreateJobAdvertisementDto createJobAdvertisementDto) {
        LOG.debug("Start creating new job ad from API");
        Condition.notNull(createJobAdvertisementDto, "CreateJobAdvertisementDto can't be null");
        String avamOccupationCode = createJobAdvertisementDto.getOccupation().getAvamOccupationCode();
        if(isDeprecatedAvamCode(avamOccupationCode)) {
            LOG.info("The ApiUser with the ID: '{}' and the E-Mail: '{}' is using a deprecated avam code '{}'", currentUserContext.getCurrentUser().getUserId(), currentUserContext.getCurrentUser().getEmail(), avamOccupationCode);
        }
        Condition.isTrue(professionService.isKnownAvamCode(avamOccupationCode),
                String.format("Unknown AVAM Occupation Code: %s", avamOccupationCode));
        LOG.debug("Create '{}'", createJobAdvertisementDto.getJobDescriptions().get(0).getTitle());

        final JobAdvertisementCreator creator = getJobAdvertisementCreatorFromInternal(createJobAdvertisementDto);
        JobAdvertisement newJobAdvertisement = jobAdvertisementFactory.createFromApi(creator);
        return new CreatedJobAdvertisementIdWithTokenDto()
                .setJobAdvertisementId(newJobAdvertisement.getId().getValue())
                .setToken(newJobAdvertisement.getOwner().getAccessToken());
    }

    public JobAdvertisementId createFromAvam(CreateJobAdvertisementDto createJobAdvertisementFromAvamDto) {
        LOG.debug("Start creating new job ad from AVAM");

        Condition.notNull(createJobAdvertisementFromAvamDto, "CreateJobAdvertisementFromAvamDto can't be null");

        Optional<JobAdvertisement> existingJobAdvertisement = jobAdvertisementRepository.findByStellennummerAvam(createJobAdvertisementFromAvamDto.getStellennummerAvam());
        if (existingJobAdvertisement.isPresent()) {
            JobAdvertisement jobAdvertisement = existingJobAdvertisement.get();
            LOG.debug("Update StellennummerAvam '{}' from AVAM", createJobAdvertisementFromAvamDto.getStellennummerAvam());
            jobAdvertisement.update(prepareUpdaterFromAvam(createJobAdvertisementFromAvamDto));
            return jobAdvertisement.getId();
        }

        LOG.debug("Create StellennummerAvam: {}", createJobAdvertisementFromAvamDto.getStellennummerAvam());
        checkIfJobAdvertisementAlreadyExists(createJobAdvertisementFromAvamDto);

        Condition.notNull(createJobAdvertisementFromAvamDto.getLocation(), "Location can't be null");
        Location location = toLocation(createJobAdvertisementFromAvamDto.getLocation());
        location = locationService.enrichCodes(location);

        Condition.notEmpty(createJobAdvertisementFromAvamDto.getOccupations(), "Occupations can't be empty");
        List<Occupation> occupations = enrichAndToOccupations(createJobAdvertisementFromAvamDto.getOccupations());

        Company company = toCompany(createJobAdvertisementFromAvamDto.getCompany());
        JobContent jobContent = new JobContent.Builder()
                .setNumberOfJobs(createJobAdvertisementFromAvamDto.getNumberOfJobs())
                .setJobDescriptions(Collections.singletonList(
                        new JobDescription.Builder()
                                .setLanguage(Locale.GERMAN)
                                .setTitle(createJobAdvertisementFromAvamDto.getJobDescriptions().get(0).getTitle()) // TODO: 07/03/2019 Fago check this
                                .setDescription(createJobAdvertisementFromAvamDto.getJobDescriptions().get(0).getDescription()) // TODO: 07/03/2019 Fago check this
                                .build()
                ))
                .setLocation(location)
                .setOccupations(occupations)
                .setEmployment(toEmployment(createJobAdvertisementFromAvamDto.getEmployment()))
                .setApplyChannel(toApplyChannel(createJobAdvertisementFromAvamDto.getApplyChannel()))
                .setDisplayApplyChannel(determineApplyChannel(createJobAdvertisementFromAvamDto))
                .setDisplayCompany(determineDisplayCompany(createJobAdvertisementFromAvamDto))
                .setCompany(company)
                .setPublicContact(toPublicContact(createJobAdvertisementFromAvamDto.getPublicContact()))
                .setLanguageSkills(toLanguageSkills(createJobAdvertisementFromAvamDto.getLanguageSkills()))
                .build();

        final JobAdvertisementCreator creator = new JobAdvertisementCreator.Builder(currentUserContext.getAuditUser())
                .setStellennummerAvam(createJobAdvertisementFromAvamDto.getStellennummerAvam())
                .setReportingObligation(createJobAdvertisementFromAvamDto.isReportingObligation())
                .setReportingObligationEndDate(createJobAdvertisementFromAvamDto.getReportingObligationEndDate())
                .setJobCenterCode(createJobAdvertisementFromAvamDto.getJobCenterCode())
                .setJobCenterUserId(createJobAdvertisementFromAvamDto.getJobCenterUserId())
                .setApprovalDate(createJobAdvertisementFromAvamDto.getApprovalDate())
                .setJobContent(jobContent)
                .setContact(toContact(createJobAdvertisementFromAvamDto.getContact()))
                .setPublication(toPublication(createJobAdvertisementFromAvamDto.getPublication()))
                .build();

        JobAdvertisement jobAdvertisement = jobAdvertisementFactory.createFromAvam(creator);
        return jobAdvertisement.getId();
    }

    public JobAdvertisementId createFromExtern(ExternalCreateJobAdvertisementDto createJobAdvertisementFromExternalDto) {
        LOG.debug("Start creating new job ad from external");

        Condition.notNull(createJobAdvertisementFromExternalDto, "CreateJobAdvertisementFromExternalDto can't be null");
        LOG.debug("Create '{}'", createJobAdvertisementFromExternalDto.getTitle());

        checkIfJobAdvertisementAlreadyExists(createJobAdvertisementFromExternalDto);

        Location location = toLocation(createJobAdvertisementFromExternalDto.toCreateLocationDto());
        location = locationService.enrichCodes(location);

        List<Occupation> occupations = enrichAndToOccupations(createJobAdvertisementFromExternalDto.toOccupationDtos());

        Company company = toCompany(createJobAdvertisementFromExternalDto.toCompanyDto());
        JobContent jobContent = new JobContent.Builder()
                .setNumberOfJobs(createJobAdvertisementFromExternalDto.getNumberOfJobs())
                .setJobDescriptions(Collections.singletonList(
                        new JobDescription.Builder()
                                .setLanguage(Locale.GERMAN)
                                .setTitle(createJobAdvertisementFromExternalDto.getTitle())
                                .setDescription(createJobAdvertisementFromExternalDto.getDescription())
                                .build()
                ))
                .setExternalUrl(createJobAdvertisementFromExternalDto.getExternalUrl())
                .setLocation(location)
                .setOccupations(occupations)
                .setX28OccupationCodes(createJobAdvertisementFromExternalDto.getProfessionCodes())
                .setEmployment(toEmployment(createJobAdvertisementFromExternalDto.getEmployment()))
                .setDisplayCompany(determineDisplayCompany(createJobAdvertisementFromExternalDto))
                .setCompany(company)
                .setPublicContact(toPublicContact(createJobAdvertisementFromExternalDto.toPublicContactDto()))
                .setLanguageSkills(toLanguageSkills(createJobAdvertisementFromExternalDto.toLanguageSkillDtos()))
                .build();

        LocalDate publicationStartDate = createJobAdvertisementFromExternalDto.getPublicationStartDate();
        if (publicationStartDate == null) {
            publicationStartDate = TimeMachine.now().toLocalDate();
        }
        LocalDate publicationEndDate = createJobAdvertisementFromExternalDto.getPublicationEndDate();
        if (publicationEndDate == null) {
            publicationEndDate = publicationStartDate.plusDays(PUBLICATION_MAX_DAYS);
        }
        final JobAdvertisementCreator creator = new JobAdvertisementCreator.Builder(currentUserContext.getAuditUser())
                .setFingerprint(createJobAdvertisementFromExternalDto.getFingerprint())
                .setJobContent(jobContent)
                .setContact(toContact(createJobAdvertisementFromExternalDto.toContactDto()))
                .setPublication(
                        new Publication.Builder()
                                .setStartDate(publicationStartDate)
                                .setEndDate(publicationEndDate)
                                .setEuresDisplay(false)
                                .setEuresAnonymous(false)
                                .setPublicDisplay(true)
                                .setRestrictedDisplay(true)
                                .setCompanyAnonymous(createJobAdvertisementFromExternalDto.isCompanyAnonymous())
                                .build()
                )
                .build();

        JobAdvertisement jobAdvertisement = jobAdvertisementFactory.createFromExtern(creator);
        return jobAdvertisement.getId();
    }

    public JobAdvertisementId updateFromExtern(JobAdvertisementId jobAdvertisementId, ExternalCreateJobAdvertisementDto createFromExternal) {
        LOG.debug("Update JobAdvertisement '{}' from Extern", jobAdvertisementId);

        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.findById(jobAdvertisementId)
                .orElseThrow(() -> new EntityNotFoundException("JobAdvertisement not found. JobAdvertisementId: " + jobAdvertisementId.getValue()));

        JobAdvertisementUpdater updater = new JobAdvertisementUpdater.Builder(currentUserContext.getAuditUser())
                .setJobDescription(createFromExternal.getTitle(), createFromExternal.getDescription())
                .setX28OccupationCodes(createFromExternal.getProfessionCodes())
                .build();

        jobAdvertisement.update(updater);

        republishIfArchived(jobAdvertisementId);

        return jobAdvertisement.getId();
    }

    public JobAdvertisementId enrichFromExtern(JobAdvertisementId jobAdvertisementId, String fingerprint, String professtionCodes) {
        LOG.debug("Enrich JobAdvertisement '{}' from Extern", jobAdvertisementId);

        JobAdvertisement jobAdvertisement = jobAdvertisementRepository.findById(jobAdvertisementId)
                .orElseThrow(() -> new EntityNotFoundException("JobAdvertisement not found. JobAdvertisementId: " + jobAdvertisementId.getValue()));

        JobAdvertisementUpdater updater = new JobAdvertisementUpdater.Builder(currentUserContext.getAuditUser())
                .setFingerprint(fingerprint)
                .setX28OccupationCodes(professtionCodes)
                .build();

        jobAdvertisement.update(updater);
        return jobAdvertisement.getId();
    }

    public Page<JobAdvertisementDto> findOwnJobAdvertisements(Pageable pageable) {
        CurrentUser currentUser = currentUserContext.getCurrentUser();
        if (currentUser == null) {
            return new PageImpl<>(Collections.EMPTY_LIST, pageable, 0);
        }
        Page<JobAdvertisement> jobAdvertisements = jobAdvertisementRepository.findOwnJobAdvertisements(pageable, currentUser.getUserId(), currentUser.getCompanyId());
        return new PageImpl<>(
                jobAdvertisements.getContent().stream().map(JobAdvertisementDto::toDto).collect(toList()),
                jobAdvertisements.getPageable(),
                jobAdvertisements.getTotalElements()
        );
    }

    public Page<JobAdvertisementDto> findJobAdvertisementsByStatus(Pageable pageable, ApiSearchRequestDto searchRequest) {
        CurrentUser currentUser = currentUserContext.getCurrentUser();

        if (currentUser == null || searchRequest.getStatus() == null || searchRequest.getStatus().isEmpty()) {
            return new PageImpl<>(Collections.EMPTY_LIST, pageable, 0);
        }


        Page<JobAdvertisement> jobAdvertisements = jobAdvertisementRepository.findJobAdvertisementsByStatus(currentUser.getUserId(), currentUser.getCompanyId(), searchRequest.getStatus(), pageable);
        return new PageImpl<>(
                jobAdvertisements.getContent().stream().map(JobAdvertisementDto::toDto).collect(toList()),
                jobAdvertisements.getPageable(),
                jobAdvertisements.getTotalElements()
        );
    }

    @IsSysAdmin
    public Page<JobAdvertisementDto> findAllPaginated(Pageable pageable) {
        Page<JobAdvertisement> jobAdvertisements = jobAdvertisementRepository.findAll(pageable);
        return new PageImpl<>(
                jobAdvertisements.getContent().stream().map(JobAdvertisementDto::toDto).collect(toList()),
                jobAdvertisements.getPageable(),
                jobAdvertisements.getTotalElements()
        );
    }

    @PreAuthorize("@jobAdvertisementAuthorizationService.canViewJob(#jobAdvertisementId)")
    public JobAdvertisementDto getById(JobAdvertisementId jobAdvertisementId) throws AggregateNotFoundException {
        JobAdvertisement jobAdvertisement = getJobAdvertisement(jobAdvertisementId);
        BusinessLogEvent logData = new BusinessLogEvent(JOB_ADVERTISEMENT_ACCESS_EVENT, JOB_ADVERTISEMENT_LOG)
                .withObjectId(jobAdvertisementId.getValue())
                .withAdditionalData(STATUS_ADDITIONAL_DATA, jobAdvertisement.getStatus());
        this.businessLogger.log(logData);

        return JobAdvertisementDto.toDto(jobAdvertisement);
    }

    public JobAdvertisementDto getByAccessToken(String accessToken) {
        JobAdvertisement jobAdvertisement = getJobAdvertisementByAccessToken(accessToken);
        return JobAdvertisementDto.toDto(jobAdvertisement);
    }

    @PreAuthorize("@jobAdvertisementAuthorizationService.canViewJob(#stellennummerAvam)")
    public JobAdvertisementDto getByStellennummerAvam(String stellennummerAvam) {
        JobAdvertisement jobAdvertisement = getJobAdvertisementByStellennummerAvam(stellennummerAvam);
        return JobAdvertisementDto.toDto(jobAdvertisement);
    }

    public JobAdvertisementDto getByStellennummerEgovOrAvam(String stellennummerEgov, String stellennummerAvam) {
        final String externalId = hasText(stellennummerEgov) ? stellennummerEgov : (hasText(stellennummerAvam) ? stellennummerAvam : null);
        Optional<JobAdvertisement> jobAdvertisement = jobAdvertisementRepository.findByStellennummerAvamOrStellennummerEgov(externalId);
        return jobAdvertisement.map(JobAdvertisementDto::toDto).orElseThrow(() -> new AggregateNotFoundException(JobAdvertisement.class, AggregateNotFoundException.IndentifierType.EXTERNAL_ID, externalId));
    }

    public JobAdvertisementDto findByStellennummerEgovOrAvam(String stellennummerEgov, String stellennummerAvam) {
        final String externalId = hasText(stellennummerEgov) ? stellennummerEgov : (hasText(stellennummerAvam) ? stellennummerAvam : null);
        Optional<JobAdvertisement> jobAdvertisement = jobAdvertisementRepository.findByStellennummerAvamOrStellennummerEgov(externalId);
        return jobAdvertisement.map(JobAdvertisementDto::toDto).orElse(null);
    }

    @PreAuthorize("@jobAdvertisementAuthorizationService.canViewJob(#stellennummerEgov)")
    public JobAdvertisementDto getByStellennummerEgov(String stellennummerEgov) {
        JobAdvertisement jobAdvertisement = getJobAdvertisementByStellennummerEgov(stellennummerEgov);
        return JobAdvertisementDto.toDto(jobAdvertisement);
    }

    public JobAdvertisementDto getByFingerprint(String fingerprint) {
        final Optional<JobAdvertisement> jobAdvertisement = jobAdvertisementRepository.findByFingerprint(fingerprint);
        return jobAdvertisement.map(JobAdvertisementDto::toDto)
                .orElseThrow(() -> new AggregateNotFoundException(JobAdvertisement.class, AggregateNotFoundException.IndentifierType.EXTERNAL_ID, fingerprint));
    }

    public void inspect(JobAdvertisementId jobAdvertisementId) {
        Condition.notNull(jobAdvertisementId, "JobAdvertisementId can't be null");
        LOG.debug("Starting inspect for JobAdvertisementId: '{}'", jobAdvertisementId.getValue());
        JobAdvertisement jobAdvertisement = getJobAdvertisement(jobAdvertisementId);
        jobAdvertisement.inspect();
    }

    public void approve(ApprovalDto approvalDto) {
        Condition.notNull(approvalDto.getStellennummerEgov(), "StellennummerEgov can't be null");
        JobAdvertisement jobAdvertisement = getJobAdvertisementByStellennummerEgov(approvalDto.getStellennummerEgov());
        if (jobAdvertisement.getStatus().equals(INSPECTING) || jobAdvertisement.getStatus().equals(REJECTED)) {
            LOG.debug("Starting approve for JobAdvertisementId: '{}'", jobAdvertisement.getId().getValue());
            jobAdvertisement.approve(approvalDto.getStellennummerAvam(), approvalDto.getDate(), approvalDto.isReportingObligation(),
                    approvalDto.getReportingObligationEndDate(), approvalDto.getJobCenterCode(), approvalDto.getJobCenterUserId());
        }
        // FIXME This is a workaround when updating after approval, until AVAM add an actionType on there message.
        LOG.debug("Starting UPDATE for JobAdvertisementId: '{}'", jobAdvertisement.getId().getValue());
        UpdateJobAdvertisementFromAvamDto updateJobAdvertisement = approvalDto.getUpdateJobAdvertisement();
        jobAdvertisement.update(prepareUpdaterFromAvam(updateJobAdvertisement));
    }

    public void updateJobCenters() {
        jobCenterService.findAllJobCenters()
                .forEach(this::updateGivenJobCenter);
    }

    public void updateJobCenter(String code) {
        JobCenter jobCenter = jobCenterService.findJobCenterByCode(code);
        this.updateGivenJobCenter(jobCenter);
    }

    private void updateGivenJobCenter(JobCenter jobCenter) {
        LOG.info("Updating Job Advertisements for Job-Center: {}", jobCenter.getCode());
        try {
            this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            this.transactionTemplate.execute((TransactionCallback<Void>) status -> {
                jobAdvertisementRepository.findByJobCenterCode(jobCenter.getCode())
                        .peek(jobAdvertisement -> LOG.debug("Update Job-Center for Job-Advertisement: {}", jobAdvertisement.getId().getValue()))
                        .forEach(jobAdvertisement -> jobAdvertisement.updateJobCenter(jobCenter));
                return null;
            });
        } catch (TransactionSystemException e) {
            LOG.warn("Could not update Job Advertisements for Job-Center: " + jobCenter.getCode(), e);
        }
    }

    private JobAdvertisementUpdater prepareUpdaterFromAvam(CreateJobAdvertisementDto createJobAdvertisement) {
        Condition.notNull(createJobAdvertisement.getLocation(), "Location can't be null");
        Location location = toLocation(createJobAdvertisement.getLocation());
        location = locationService.enrichCodes(location);

        List<OccupationDto> occupationDtos = createJobAdvertisement.getOccupations();
        Condition.notEmpty(occupationDtos, "Occupations can't be empty");
        List<Occupation> occupations = enrichAndToOccupations(occupationDtos);

        Company company = toCompany(createJobAdvertisement.getCompany());
        return new JobAdvertisementUpdater.Builder(currentUserContext.getAuditUser())
                .setNumberOfJobs(createJobAdvertisement.getNumberOfJobs())
                .setJobDescription(createJobAdvertisement.getJobDescriptions().get(0).getTitle(), createJobAdvertisement.getJobDescriptions().get(0).getDescription())
                .setReportingObligation(createJobAdvertisement.isReportingObligation(), createJobAdvertisement.getReportingObligationEndDate())
                .setJobCenterCode(createJobAdvertisement.getJobCenterCode())
                .setJobCenterUserId(createJobAdvertisement.getJobCenterUserId())
                .setDisplayCompany(determineDisplayCompany(createJobAdvertisement))
                .setCompany(company)
                .setEmployment(toEmployment(createJobAdvertisement.getEmployment()))
                .setLocation(location)
                .setOccupations(occupations)
                .setLanguageSkills(toLanguageSkills(createJobAdvertisement.getLanguageSkills()))
                .setApplyChannel(toApplyChannel(createJobAdvertisement.getApplyChannel()))
                .setDisplayApplyChannel(determineApplyChannel(createJobAdvertisement))
                .setContact(toContact(createJobAdvertisement.getContact()))
                .setPublicContact(toPublicContact(createJobAdvertisement.getPublicContact()))
                .setPublication(toPublication(createJobAdvertisement.getPublication()))
                .build();
    }

    private JobAdvertisementUpdater prepareUpdaterFromAvam(UpdateJobAdvertisementFromAvamDto updateJobAdvertisement) {
        Condition.notNull(updateJobAdvertisement.getLocation(), "Location can't be null");
        Location location = toLocation(updateJobAdvertisement.getLocation());
        location = locationService.enrichCodes(location);

        List<OccupationDto> occupationDtos = updateJobAdvertisement.getOccupations();
        Condition.notEmpty(occupationDtos, "Occupations can't be empty");
        List<Occupation> occupations = enrichAndToOccupations(occupationDtos);

        Company company = toCompany(updateJobAdvertisement.getCompany());
        return new JobAdvertisementUpdater.Builder(currentUserContext.getAuditUser())
                .setNumberOfJobs(updateJobAdvertisement.getNumberOfJobs())
                .setJobDescription(updateJobAdvertisement.getTitle(), updateJobAdvertisement.getDescription())
                .setJobCenterCode(updateJobAdvertisement.getJobCenterCode())
                .setJobCenterUserId(updateJobAdvertisement.getJobCenterUserId())
                .setDisplayCompany(determineDisplayCompany(updateJobAdvertisement))
                .setCompany(company)
                .setEmployment(toEmployment(updateJobAdvertisement.getEmployment()))
                .setLocation(location)
                .setOccupations(occupations)
                .setLanguageSkills(toLanguageSkills(updateJobAdvertisement.getLanguageSkills()))
                .setApplyChannel(toApplyChannel(updateJobAdvertisement.getApplyChannel()))
                .setDisplayApplyChannel(determineApplyChannel(updateJobAdvertisement))
                .setContact(toContact(updateJobAdvertisement.getContact()))
                .setPublicContact(toPublicContact(updateJobAdvertisement.getPublicContact()))
                .setPublication(toPublication(updateJobAdvertisement.getPublication()))
                .build();
    }

    private List<Occupation> enrichAndToOccupations(List<OccupationDto> occupationDtos) {
        return occupationDtos.stream()
                .map(this::toOccupation)
                .map(this::enrichOccupationWithProfessionCodes)
                .collect(toList());
    }

    public void reject(RejectionDto rejectionDto) {
        Condition.notNull(rejectionDto.getStellennummerEgov(), "StellennummerEgov can't be null");
        JobAdvertisement jobAdvertisement = getJobAdvertisementByStellennummerEgov(rejectionDto.getStellennummerEgov());
        LOG.debug("Starting reject for JobAdvertisementId: '{}'", jobAdvertisement.getId().getValue());
        jobAdvertisement.reject(rejectionDto.getStellennummerAvam(), rejectionDto.getDate(), rejectionDto.getCode(),
                rejectionDto.getReason(), rejectionDto.getJobCenterCode(), rejectionDto.getJobCenterUserId());
    }

    // FIXME @PreAuthorize("@jobAdvertisementAuthorizationService.canCancel(jobAdvertisementId, token)")
    public void cancel(JobAdvertisementId jobAdvertisementId, CancellationDto cancellationDto, String token) {
        Condition.notNull(jobAdvertisementId, "JobAdvertisementId can't be null");
        JobAdvertisement jobAdvertisement = getJobAdvertisement(jobAdvertisementId);
        LOG.debug("Starting cancel for JobAdvertisementId: '{}'", jobAdvertisement.getId().getValue());
        jobAdvertisement.cancel(cancellationDto.getCancellationDate(), cancellationDto.getCancellationCode(), cancellationDto.getCancelledBy());
    }

    public void refining(JobAdvertisementId jobAdvertisementId) {
        Condition.notNull(jobAdvertisementId, "JobAdvertisementId can't be null");
        LOG.debug("Starting refining for JobAdvertisementId: '{}'", jobAdvertisementId.getValue());
        JobAdvertisement jobAdvertisement = getJobAdvertisement(jobAdvertisementId);
        jobAdvertisement.refining();
    }

    public void publish(JobAdvertisementId jobAdvertisementId) {
        Condition.notNull(jobAdvertisementId, "JobAdvertisementId can't be null");
        LOG.debug("Starting publish for JobAdvertisementId: '{}'", jobAdvertisementId.getValue());
        JobAdvertisement jobAdvertisement = getJobAdvertisement(jobAdvertisementId);
        publish(jobAdvertisement);
    }

    public void republishIfArchived(JobAdvertisementId jobAdvertisementId) {
        Condition.notNull(jobAdvertisementId, "JobAdvertisementId can't be null");

        JobAdvertisement jobAdvertisement = getJobAdvertisement(jobAdvertisementId);

        LocalDate lastUpdateDate = jobAdvertisement.getUpdatedTime().toLocalDate();
        LocalDate publicationEndDate = jobAdvertisement.getPublication().getEndDate();

        LocalDate lastDateToRepublish = TimeMachine.now().toLocalDate().minusDays(EXTERN_JOB_AD_REACTIVATION_DAY_NUM);
        boolean republishAllowed = !lastDateToRepublish.isAfter(lastUpdateDate) && TimeMachine.isNotBeforeToday(publicationEndDate);

        if (JobAdvertisementStatus.ARCHIVED.equals(jobAdvertisement.getStatus()) && republishAllowed) {
            jobAdvertisement.republish();
        } else {
            LOG.debug("Republish is not allowed for jobAdvertisement with id: '{}' in status: '{}', with last update date: '{}' and publicationEndDate: {}",
                    jobAdvertisement.getId(), jobAdvertisement.getStatus(), lastUpdateDate, publicationEndDate);
        }
    }

    private void publish(JobAdvertisement jobAdvertisement) {
        Condition.notNull(jobAdvertisement, "JobAdvertisement can't be null");
        LocalDate startDate = jobAdvertisement.getPublication().getStartDate();
        if (TimeMachine.isAfterToday(startDate)) {
            return;
        }
        if (determineIfValidForRestrictedPublication(jobAdvertisement)) {
            LOG.debug("Publish in restricted area for JobAdvertisementId: '{}'", jobAdvertisement.getId().getValue());
            jobAdvertisement.publishRestricted();
        } else {
            LOG.debug("Publish in public area for JobAdvertisementId: '{}'", jobAdvertisement.getId().getValue());
            jobAdvertisement.publishPublic();
        }
    }

    public void archive(JobAdvertisementId jobAdvertisementId) {
        Condition.notNull(jobAdvertisementId, "JobAdvertisementId can't be null");
        LOG.debug("Starting archive for JobAdvertisementId: '{}'", jobAdvertisementId.getValue());
        JobAdvertisement jobAdvertisement = getJobAdvertisement(jobAdvertisementId);
        jobAdvertisement.archive();
    }

    public void archiveExternalJobAdvertisements() {
        this.externalJobAdvertisementService.archiveExternalJobAdvertisements();
    }

    public void checkPublicationStarts() {
        this.jobAdvertisementRepository
                .findAllWherePublicationShouldStart(TimeMachine.now().toLocalDate())
                .forEach(this::publish);

    }

    public void checkBlackoutPolicyExpiration() {
        this.jobAdvertisementRepository
                .findAllWhereBlackoutNeedToExpire(TimeMachine.now().toLocalDate())
                .forEach(JobAdvertisement::expireBlackout);

    }

    public void checkPublicationExpiration() {
        this.jobAdvertisementRepository
                .findAllWherePublicationNeedToExpire(TimeMachine.now().toLocalDate())
                .forEach(JobAdvertisement::expirePublication);

    }

    private void checkIfJobAdvertisementAlreadyExists(ExternalCreateJobAdvertisementDto createJobAdvertisementFromExternalDto) {
        Optional<JobAdvertisement> jobAdvertisement = jobAdvertisementRepository.findByFingerprint(createJobAdvertisementFromExternalDto.getFingerprint());
        jobAdvertisement.ifPresent(jobAd -> {
            String message = String.format("JobAdvertisement '%s' with fingerprint '%s' already exists", jobAd.getId().getValue(), jobAd.getFingerprint());
            throw new JobAdvertisementAlreadyExistsException(jobAd.getId(), message);
        });
    }

    private void checkIfJobAdvertisementAlreadyExists(CreateJobAdvertisementDto createJobAdvertisementFromAvamDto) {
        Optional<JobAdvertisement> jobAdvertisement = jobAdvertisementRepository.findByStellennummerAvam(createJobAdvertisementFromAvamDto.getStellennummerAvam());
        jobAdvertisement.ifPresent(jobAd -> {
            String message = String.format("JobAdvertisement '%s' with stellennummerAvam '%s' already exists", jobAd.getId().getValue(), jobAd.getStellennummerAvam());
            throw new JobAdvertisementAlreadyExistsException(jobAd.getId(), message);
        });
    }

    private JobAdvertisementCreator getJobAdvertisementCreatorFromInternal(CreateJobAdvertisementDto createJobAdvertisementDto) {
        Location location = convertCreateLocationToEnrichedLocation(createJobAdvertisementDto.getLocation());

        Condition.notNull(createJobAdvertisementDto.getOccupations(), "Occupations can't be null");

        Occupation occupation = toOccupation(createJobAdvertisementDto.getOccupations().get(0));
        occupation = enrichOccupationWithProfessionCodes(occupation);
        List<Occupation> occupations = Collections.singletonList(occupation);

        Employment employment = toEmployment(createJobAdvertisementDto.getEmployment());
        boolean reportingObligation = checkReportingObligation(
                occupation,
                location,
                employment
        );

        String jobCenterCode = determineJobCenterCode(reportingObligation, createJobAdvertisementDto.isReportToAvam(), location);

        Company company = toCompany(createJobAdvertisementDto.getCompany());
        ApplyChannel applyChannel = toApplyChannel(createJobAdvertisementDto.getApplyChannel());
        JobContent jobContent = new JobContent.Builder()
                .setNumberOfJobs(createJobAdvertisementDto.getNumberOfJobs())
                .setExternalUrl(createJobAdvertisementDto.getExternalUrl())
                .setJobDescriptions(toJobDescriptions(createJobAdvertisementDto.getJobDescriptions()))
                .setLocation(location)
                .setOccupations(occupations)
                .setEmployment(employment)
                .setApplyChannel(applyChannel)
                .setDisplayApplyChannel(applyChannel)
                .setDisplayCompany(company)
                .setCompany(company)
                .setEmployer(toEmployer(createJobAdvertisementDto.getEmployer()))
                .setPublicContact(toPublicContact(createJobAdvertisementDto.getPublicContact()))
                .setLanguageSkills(toLanguageSkills(createJobAdvertisementDto.getLanguageSkills()))
                .build();

        PublicationDto publicationDto = createJobAdvertisementDto.getPublication();

        return new JobAdvertisementCreator.Builder(currentUserContext.getAuditUser())
                .setExternalReference(createJobAdvertisementDto.getExternalReference())
                .setReportToAvam(createJobAdvertisementDto.isReportToAvam())
                .setReportingObligation(reportingObligation)
                .setJobCenterCode(jobCenterCode)
                .setJobContent(jobContent)
                .setContact(toContact(createJobAdvertisementDto.getContact()))
                .setPublication(
                        new Publication.Builder()
                                .setStartDate(publicationDto.getStartDate())
                                .setEndDate((publicationDto.getEndDate() != null) ? publicationDto.getEndDate() : TimeMachine.now().plusDays(PUBLICATION_MAX_DAYS).toLocalDate())
                                .setEuresDisplay(publicationDto.isEuresDisplay())
                                .setEuresAnonymous(false)
                                .setRestrictedDisplay(publicationDto.isPublicDisplay())
                                .setPublicDisplay(publicationDto.isPublicDisplay())
                                .setCompanyAnonymous(false)
                                .build()
                )
                .build();
    }

    private Location convertCreateLocationToEnrichedLocation(CreateLocationDto createLocationDto) {
        Condition.notNull(createLocationDto, "Location can't be null");
        Location location = toLocation(createLocationDto);
        Condition.isTrue(locationService.isLocationValid(location), String.format("Invalid location: %s %s %s", location.getCountryIsoCode(), location.getPostalCode(), location.getCity()));

        return locationService.enrichCodes(location);
    }

    private JobAdvertisement getJobAdvertisement(JobAdvertisementId jobAdvertisementId) throws AggregateNotFoundException {
        Optional<JobAdvertisement> jobAdvertisement = jobAdvertisementRepository.findById(jobAdvertisementId);
        return jobAdvertisement.orElseThrow(() -> new AggregateNotFoundException(JobAdvertisement.class, jobAdvertisementId.getValue()));
    }

    private JobAdvertisement getJobAdvertisementByStellennummerEgov(String stellennummerEgov) throws AggregateNotFoundException {
        Optional<JobAdvertisement> jobAdvertisement = jobAdvertisementRepository.findByStellennummerEgov(stellennummerEgov);
        return jobAdvertisement.orElseThrow(() -> new AggregateNotFoundException(JobAdvertisement.class, AggregateNotFoundException.IndentifierType.EXTERNAL_ID, stellennummerEgov));
    }

    private JobAdvertisement getJobAdvertisementByStellennummerAvam(String stellennummerAvam) throws AggregateNotFoundException {
        Optional<JobAdvertisement> jobAdvertisement = jobAdvertisementRepository.findByStellennummerAvam(stellennummerAvam);
        return jobAdvertisement.orElseThrow(() -> new AggregateNotFoundException(JobAdvertisement.class, AggregateNotFoundException.IndentifierType.EXTERNAL_ID, stellennummerAvam));
    }

    private JobAdvertisement getJobAdvertisementByAccessToken(String accessToken) {
        Optional<JobAdvertisement> jobAdvertisement = jobAdvertisementRepository.findOneByAccessToken(accessToken);
        return jobAdvertisement.orElseThrow(() -> new AggregateNotFoundException(JobAdvertisement.class, AggregateNotFoundException.IndentifierType.ACCESS_TOKEN, accessToken));
    }

    private Occupation enrichOccupationWithProfessionCodes(Occupation occupation) {
        Condition.notNull(occupation, "Occupation can't be null");
        return professionService.findByAvamCode(occupation.getAvamOccupationCode())
                .map(profession -> new Occupation.Builder()
                        .setAvamOccupationCode(profession.getAvamCode())
                        .setBfsCode(profession.getBfsCode())
                        .setChIsco3Code(profession.getChIsco3Code())
                        .setChIsco5Code(profession.getChIsco5Code())
                        .setBfsCode(profession.getBfsCode())
                        .setLabel(profession.getLabel())
                        .setWorkExperience(occupation.getWorkExperience())
                        .setEducationCode(occupation.getEducationCode())
                        .setQualification(occupation.getQualificationCode())
                        .build())
                .orElse(occupation);
    }

    private boolean checkReportingObligation(Occupation occupation, Location location, Employment employment) {
        Condition.notNull(occupation, "Occupation can't be null");
        Condition.notNull(location, "Location can't be null");
        Condition.notNull(employment, "Employment can't be null");

        if (employment.isShortEmployment()) {
            return false;
        }
        String avamOccupationCode = occupation.getAvamOccupationCode();
        String cantonCode = location.getCantonCode();
        if (COUNTRY_ISO_CODE_SWITZERLAND.equals(location.getCountryIsoCode())) {
            return reportingObligationService.hasReportingObligation(ProfessionCodeType.AVAM, avamOccupationCode, cantonCode);
        }
        return false;
    }

    private String determineJobCenterCode(boolean reportingObligation, boolean reportToAvam, Location location) {
        return reportingObligation || reportToAvam
                ? jobCenterService.findJobCenterCode(location.getCountryIsoCode(), location.getPostalCode())
                : null;
    }

    private List<JobDescription> toJobDescriptions(List<JobDescriptionDto> jobDescriptionDtos) {
        if (jobDescriptionDtos == null) {
            return null;
        }
        return jobDescriptionDtos.stream()
                .map(jobDescriptionDto -> new JobDescription.Builder()
                        .setLanguage(hasText(jobDescriptionDto.getLanguageIsoCode()) ? new Locale(jobDescriptionDto.getLanguageIsoCode()) : Locale.GERMAN)
                        .setTitle(jobDescriptionDto.getTitle())
                        .setDescription(jobDescriptionDto.getDescription())
                        .build()
                )
                .collect(toList());
    }

    private Publication toPublication(PublicationDto publicationDto) {
        if (publicationDto == null) {
            return null;
        }
        return new Publication.Builder()
                .setStartDate(publicationDto.getStartDate())
                .setEndDate(publicationDto.getEndDate())
                .setEuresDisplay(publicationDto.isEuresDisplay())
                .setEuresAnonymous(publicationDto.isEuresAnonymous())
                .setPublicDisplay(publicationDto.isPublicDisplay())
                .setRestrictedDisplay(publicationDto.isRestrictedDisplay())
                .setCompanyAnonymous(publicationDto.isCompanyAnonymous())
                .build();
    }

    private Employment toEmployment(EmploymentDto employmentDto) {
        if (employmentDto == null) {
            return null;
        }
        return new Employment.Builder()
                .setStartDate(employmentDto.getStartDate())
                .setEndDate(employmentDto.getEndDate())
                .setShortEmployment(employmentDto.isShortEmployment())
                .setImmediately(employmentDto.isImmediately())
                .setPermanent(employmentDto.isPermanent())
                .setWorkloadPercentageMin(employmentDto.getWorkloadPercentageMin())
                .setWorkloadPercentageMax(employmentDto.getWorkloadPercentageMax())
                .setWorkForms(employmentDto.getWorkForms())
                .build();
    }

    private ApplyChannel toApplyChannel(JobCenter jobCenter) {
        if (jobCenter == null) {
            return null;
        }
        return new ApplyChannel.Builder(jobCenter)
                .build();
    }

    private ApplyChannel toApplyChannel(ApplyChannelDto applyChannelDto) {
        if (applyChannelDto == null) {
            return null;
        }
        return new ApplyChannel.Builder()
                .setRawPostAddress(applyChannelDto.getRawPostAddress())
                .setPostAddress(toAddress(applyChannelDto.getPostAddress()))
                .setEmailAddress(applyChannelDto.getEmailAddress())
                .setPhoneNumber(applyChannelDto.getPhoneNumber())
                .setFormUrl(applyChannelDto.getFormUrl())
                .setAdditionalInfo(applyChannelDto.getAdditionalInfo())
                .build();
    }

    private ApplyChannel toApplyChannel(JobCenterUser jobCenterUser) {
        final AddressDto addressDto = new AddressDto();
        addressDto.setName(jobCenterUser.getFirstName() + " " + jobCenterUser.getLastName())
                .setCity(jobCenterUser.getAddress().getCity())
                .setHouseNumber(jobCenterUser.getAddress().getHouseNumber())
                .setPostalCode(jobCenterUser.getAddress().getZipCode())
                .setStreet(jobCenterUser.getAddress().getStreet());
        return new ApplyChannel.Builder()
                .setPostAddress(toAddress(addressDto))
                .setEmailAddress(jobCenterUser.getEmail())
                .setPhoneNumber(jobCenterUser.getPhone())
                .build();
    }

    private Address toAddress(AddressDto addressDto) {
        if (addressDto == null) {
            return null;
        }
        return new Address.Builder()
                .setName(addressDto.getName())
                .setStreet(addressDto.getStreet())
                .setHouseNumber(addressDto.getHouseNumber())
                .setPostalCode(addressDto.getPostalCode())
                .setCity(addressDto.getCity())
                .setPostOfficeBoxNumber(addressDto.getPostOfficeBoxNumber())
                .setPostOfficeBoxPostalCode(addressDto.getPostOfficeBoxPostalCode())
                .setPostOfficeBoxCity(addressDto.getPostOfficeBoxCity())
                .setCountryIsoCode(addressDto.getCountryIsoCode())
                .build();
    }

    private Company determineDisplayCompany(CreateJobAdvertisementDto createJobAdvertisementFromAvamDto) {
        return this.determineDisplayCompany(
                toCompany(createJobAdvertisementFromAvamDto.getCompany()),
                createJobAdvertisementFromAvamDto.getPublication().isCompanyAnonymous(),
                createJobAdvertisementFromAvamDto.getJobCenterCode(),
                createJobAdvertisementFromAvamDto.getJobCenterUserId()
        );
    }

    private Company determineDisplayCompany(ExternalCreateJobAdvertisementDto createJobAdvertisementFromExternalDto) {
        return this.determineDisplayCompany(
                toCompany(createJobAdvertisementFromExternalDto.toCompanyDto()),
                createJobAdvertisementFromExternalDto.isCompanyAnonymous(),
                createJobAdvertisementFromExternalDto.getJobCenterCode(),
                null
        );
    }

    private Company determineDisplayCompany(UpdateJobAdvertisementFromAvamDto updateJobAdvertisement) {
        return this.determineDisplayCompany(
                toCompany(updateJobAdvertisement.getCompany()),
                updateJobAdvertisement.getPublication().isCompanyAnonymous(),
                updateJobAdvertisement.getJobCenterCode(),
                updateJobAdvertisement.getJobCenterUserId()
        );
    }

    private Company determineDisplayCompany(Company company, boolean companyAnonymous, String jobCenterCode, String jobCenterUserId) {
        if (!companyAnonymous) {
            return company;
        }
        if (!hasText(jobCenterCode)) {
            return null;
        }
        final JobCenter jobCenter = jobCenterService.findJobCenterByCode(jobCenterCode);
        if (hasText(jobCenterUserId) && isJobCenterUserContactDataEnabled(jobCenter)) {
            final Optional<JobCenterUser> jobCenterUserOptional = jobCenterService.findJobCenterUserByJobCenterUserId(jobCenterUserId);
            if (jobCenterUserOptional.isPresent()) {
                return toCompany(jobCenterUserOptional.get());
            }
        }
        return toCompany(jobCenter);
    }

    private boolean isJobCenterUserContactDataEnabled(JobCenter jobCenter) {
        return jobCenter.getContactDisplayStyle() == ContactDisplayStyle.JOB_CENTER_USER_CONTACT_DATA;
    }

    private ApplyChannel determineApplyChannel(CreateJobAdvertisementDto createJobAdvertisementFromAvamDto) {
        return this.determineApplyChannel(
                toApplyChannel(createJobAdvertisementFromAvamDto.getApplyChannel()),
                createJobAdvertisementFromAvamDto.getPublication().isCompanyAnonymous(),
                createJobAdvertisementFromAvamDto.getJobCenterCode(),
                createJobAdvertisementFromAvamDto.getJobCenterUserId()
        );
    }

    private ApplyChannel determineApplyChannel(UpdateJobAdvertisementFromAvamDto updateJobAdvertisement) {
        return this.determineApplyChannel(
                toApplyChannel(updateJobAdvertisement.getApplyChannel()),
                updateJobAdvertisement.getPublication().isCompanyAnonymous(),
                updateJobAdvertisement.getJobCenterCode(),
                updateJobAdvertisement.getJobCenterUserId()
        );
    }

    private ApplyChannel determineApplyChannel(ApplyChannel applyChannel, boolean companyAnonymous, String jobCenterCode, String jobCenterUserId) {
        if (!companyAnonymous) {
            return applyChannel;
        }
        if (!hasText(jobCenterCode)) {
            return null;
        }
        final JobCenter jobCenter = jobCenterService.findJobCenterByCode(jobCenterCode);
        if (hasText(jobCenterUserId) && isJobCenterUserContactDataEnabled(jobCenter)) {
            final Optional<JobCenterUser> jobCenterUserOptional = jobCenterService.findJobCenterUserByJobCenterUserId(jobCenterUserId);
            if (jobCenterUserOptional.isPresent()) {
                return toApplyChannel(jobCenterUserOptional.get());
            }
        }
        return toApplyChannel(jobCenter);
    }

    private Company toCompany(JobCenter jobCenter) {
        if (jobCenter == null) {
            return null;
        }
        return new Company.Builder(jobCenter)
                .build();
    }

    private Company toCompany(CompanyDto companyDto) {
        if (companyDto == null) {
            return null;
        }
        return new Company.Builder()
                .setName(companyDto.getName())
                .setStreet(companyDto.getStreet())
                .setHouseNumber(companyDto.getHouseNumber())
                .setPostalCode(companyDto.getPostalCode())
                .setCity(companyDto.getCity())
                .setCountryIsoCode(companyDto.getCountryIsoCode())
                .setPostOfficeBoxNumber(companyDto.getPostOfficeBoxNumber())
                .setPostOfficeBoxPostalCode(companyDto.getPostOfficeBoxPostalCode())
                .setPostOfficeBoxCity(companyDto.getPostOfficeBoxCity())
                .setPhone(companyDto.getPhone())
                .setEmail(companyDto.getEmail())
                .setWebsite(companyDto.getWebsite())
                .setSurrogate(companyDto.isSurrogate())
                .build();
    }

    private Company toCompany(JobCenterUser jobCenterUser) {

        return new Company.Builder()
                .setName(jobCenterUser.getFirstName() + " " + jobCenterUser.getLastName())
                .setStreet(jobCenterUser.getAddress().getStreet())
                .setHouseNumber(jobCenterUser.getAddress().getHouseNumber())
                .setPostalCode(jobCenterUser.getAddress().getZipCode())
                .setCity(jobCenterUser.getAddress().getCity())
                .setPhone(jobCenterUser.getPhone())
                .setEmail(jobCenterUser.getEmail())
                .build();
    }

    private Employer toEmployer(EmployerDto employerDto) {
        if (employerDto == null) {
            return null;
        }
        return new Employer.Builder()
                .setName(employerDto.getName())
                .setPostalCode(employerDto.getPostalCode())
                .setCity(employerDto.getCity())
                .setCountryIsoCode(employerDto.getCountryIsoCode())
                .build();
    }

    private PublicContact toPublicContact(PublicContactDto publicContactDto) {
        if (publicContactDto == null) {
            return null;
        }
        return new PublicContact.Builder()
                .setSalutation(publicContactDto.getSalutation())
                .setFirstName(publicContactDto.getFirstName())
                .setLastName(publicContactDto.getLastName())
                .setPhone(publicContactDto.getPhone())
                .setEmail(publicContactDto.getEmail())
                .build();
    }

    private PublicContact toPublicContact(ContactDto contactDto) {
        if (contactDto == null) {
            return null;
        }
        return new PublicContact.Builder()
                .setSalutation(contactDto.getSalutation())
                .setFirstName(contactDto.getFirstName())
                .setLastName(contactDto.getLastName())
                .setPhone(contactDto.getPhone())
                .setEmail(contactDto.getEmail())
                .build();
    }

    private Contact toContact(ContactDto contactDto) {
        if (contactDto == null) {
            return null;
        }
        return new Contact.Builder()
                .setSalutation(contactDto.getSalutation())
                .setFirstName(contactDto.getFirstName())
                .setLastName(contactDto.getLastName())
                .setPhone(contactDto.getPhone())
                .setEmail(contactDto.getEmail())
                .setLanguage(new Locale(contactDto.getLanguageIsoCode()))
                .build();
    }

    private Location toLocation(CreateLocationDto createLocationDto) {
        if (createLocationDto == null) {
            return null;
        }
        String countryIsoCode = Optional.ofNullable(createLocationDto.getCountryIsoCode())
                .orElse(COUNTRY_ISO_CODE_SWITZERLAND);
        return new Location.Builder()
                .setRemarks(createLocationDto.getRemarks())
                .setCity(createLocationDto.getCity())
                .setPostalCode(createLocationDto.getPostalCode())
                .setCountryIsoCode(countryIsoCode)
                .build();
    }

    private Occupation toOccupation(OccupationDto occupationDto) {
        if (occupationDto == null) {
            return null;
        }
        return new Occupation.Builder()
                .setAvamOccupationCode(occupationDto.getAvamOccupationCode())
                .setWorkExperience(occupationDto.getWorkExperience())
                .setEducationCode(occupationDto.getEducationCode())
                .setQualification(occupationDto.getQualificationCode())
                .build();
    }

    private List<LanguageSkill> toLanguageSkills(List<LanguageSkillDto> languageSkillDtos) {
        if (languageSkillDtos == null) {
            return null;
        }
        return languageSkillDtos.stream()
                .map(languageSkillDto -> new LanguageSkill.Builder()
                        .setLanguageIsoCode(languageSkillDto.getLanguageIsoCode())
                        .setSpokenLevel(languageSkillDto.getSpokenLevel())
                        .setWrittenLevel(languageSkillDto.getWrittenLevel())
                        .build()
                )
                .collect(toList());
    }

    private boolean determineIfValidForRestrictedPublication(JobAdvertisement jobAdvertisement) {
        return REFINING.equals(jobAdvertisement.getStatus())
                && jobAdvertisement.isReportingObligation()
                && ((jobAdvertisement.getReportingObligationEndDate() == null) || TimeMachine.isAfterToday(jobAdvertisement.getReportingObligationEndDate()));
    }

    private boolean isDeprecatedAvamCode(String avamOccupationCode) {
        CurrentUser currentUser = currentUserContext.getCurrentUser();
        return Integer.parseInt(avamOccupationCode) < START_RANGE_OF_NEW_AVAMCODES
                && currentUser.getDisplayName() != null && currentUser.getUserId() != null;
    }
}
