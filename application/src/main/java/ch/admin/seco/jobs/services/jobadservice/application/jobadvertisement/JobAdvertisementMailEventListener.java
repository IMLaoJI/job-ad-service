package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Contact;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementCancelledEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementCreatedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementRefinedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.events.JobAdvertisementRejectedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import static org.apache.commons.lang3.math.NumberUtils.toInt;

@Component
public class JobAdvertisementMailEventListener {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final String LANGUAGE_DE = Locale.GERMAN.getLanguage();
    private static final String LANGUAGE_FR = Locale.FRENCH.getLanguage();
    private static final String LANGUAGE_IT = Locale.ITALIAN.getLanguage();
    private static final String LANGUAGE_EN = Locale.ENGLISH.getLanguage();

    private static Logger LOG = LoggerFactory.getLogger(JobAdvertisementMailEventListener.class);

    private static final String EMAIL_DELIMITER = "\\s*;\\s*";

    private static final String JOB_ADVERTISEMENT_CREATED_SUBJECT = "mail.jobAd.created.subject";
    private static final String JOB_ADVERTISEMENT_CREATED_TEMPLATE = "JobAdCreatedMail.html";
    private static final String JOB_ADVERTISEMENT_REFINED_SUBJECT = "mail.jobAd.refined.subject";
    private static final String JOB_ADVERTISEMENT_REFINED_TEMPLATE = "JobAdRefinedMail.html";
    private static final String JOB_ADVERTISEMENT_REFINED_MULTILINGUAL_SUBJECT = "mail.jobAd.refined.subject_multilingual";
    private static final String JOB_ADVERTISEMENT_REFINED_MULTILINGUAL_TEMPLATE = "JobAdRefinedMail_multilingual.html";
    private static final String JOB_ADVERTISEMENT_REJECTED_SUBJECT = "mail.jobAd.rejected.subject";
    private static final String JOB_ADVERTISEMENT_REJECTED_TEMPLATE = "JobAdRejectedMail.html";
    private static final String JOB_ADVERTISEMENT_CANCELLED_SUBJECT = "mail.jobAd.cancelled.subject";
    private static final String JOB_ADVERTISEMENT_CANCELLED_TEMPLATE = "JobAdCancelledMail.html";
    private static final String JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_SUBJECT = "mail.jobAd.cancelled.subject_multilingual";
    private static final String JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_TEMPLATE = "JobAdCancelledMail_multilingual.html";
    private static final String DEFAULT_LANGUAGE = "";

    private final JobAdvertisementRepository jobAdvertisementRepository;
    private final MailSenderService mailSenderService;
    private final MessageSource messageSource;
    private final JobCenterService jobCenterService;

    @Autowired
    public JobAdvertisementMailEventListener(JobAdvertisementRepository jobAdvertisementRepository, MailSenderService mailSenderService, MessageSource messageSource, JobCenterService jobCenterService) {
        this.jobAdvertisementRepository = jobAdvertisementRepository;
        this.mailSenderService = mailSenderService;
        this.messageSource = messageSource;
        this.jobCenterService = jobCenterService;
    }

    @EventListener
    void onCreated(JobAdvertisementCreatedEvent event) {
        final JobAdvertisement jobAdvertisement = getJobAdvertisement(event.getAggregateId());
        if (jobAdvertisement.getSourceSystem().equals(SourceSystem.API)) {
            return;
        }
        if (hasNoContactEmail(jobAdvertisement.getContact())) {
            return;
        }
        LOG.debug("EVENT catched for mail: JOB_ADVERTISEMENT_CREATED for JobAdvertisementId: '{}'", event.getAggregateId().getValue());
        final Locale contactLocale = resolveLocale(jobAdvertisement);
        final JobCenter jobCenter = jobCenterService.findJobCenterByCode(jobAdvertisement.getJobCenterCode(), contactLocale);
        final String stellennummer = extractStellennummer(jobAdvertisement);
        mailSenderService.send(new MailSenderData.Builder()
                .setTo(parseMultipleAddresses(jobAdvertisement.getContact().getEmail()))
                .setSubject(messageSource.getMessage(JOB_ADVERTISEMENT_CREATED_SUBJECT, new Object[]{jobAdvertisement.getJobContent().getJobDescriptions().get(0).getTitle(), stellennummer}, contactLocale))
                .setTemplateName(JOB_ADVERTISEMENT_CREATED_TEMPLATE)
                .setTemplateVariables(ImmutableMap.of(
                        "stellennummer", stellennummer,
                        "showReportingObligation", showReportingObligation(jobAdvertisement),
                        "jobAdvertisementId", jobAdvertisement.getId().getValue(),
                        "accessToken", jobAdvertisement.getOwner().getAccessToken(),
                        "jobCenter", jobCenter))
                .setLocale(contactLocale)
                .build());
    }

    @EventListener
    void onRejected(JobAdvertisementRejectedEvent event) {
        final JobAdvertisement jobAdvertisement = getJobAdvertisement(event.getAggregateId());
        if (hasNoContactEmail(jobAdvertisement.getContact())) {
            return;
        }
        LOG.debug("EVENT catched for mail: JOB_ADVERTISEMENT_REJECTED for JobAdvertisementId: '{}'", event.getAggregateId().getValue());
        final Locale contactLocale = resolveLocale(jobAdvertisement);
        final JobCenter jobCenter = jobCenterService.findJobCenterByCode(jobAdvertisement.getJobCenterCode(), contactLocale);
        final String stellennummer = extractStellennummer(jobAdvertisement);
        mailSenderService.send(new MailSenderData.Builder()
                .setTo(parseMultipleAddresses(jobAdvertisement.getContact().getEmail()))
                .setSubject(messageSource.getMessage(JOB_ADVERTISEMENT_REJECTED_SUBJECT, new Object[]{jobAdvertisement.getJobContent().getJobDescriptions().get(0).getTitle(), stellennummer}, contactLocale))
                .setTemplateName(JOB_ADVERTISEMENT_REJECTED_TEMPLATE)
                .setTemplateVariables(ImmutableMap.of(
                        "stellennummer", stellennummer,
                        "rejectionReason", jobAdvertisement.getRejectionReason(),
                        "jobCenter", jobCenter))
                .setLocale(contactLocale)
                .build());

    }

    @EventListener
    void onRefined(JobAdvertisementRefinedEvent event) {
        final JobAdvertisement jobAdvertisement = getJobAdvertisement(event.getAggregateId());
        if (jobAdvertisement.getSourceSystem().equals(SourceSystem.API) && (jobAdvertisement.getStellennummerAvam() == null)) {
            return;
        }
        if (hasNoContactEmail(jobAdvertisement.getContact())) {
            return;
        }
        LOG.debug("EVENT catched for mail: JOB_ADVERTISEMENT_REFINED for JobAdvertisementId: '{}'", event.getAggregateId().getValue());
        final Locale contactLocale = resolveLocale(jobAdvertisement);
        final JobCenter jobCenter = jobCenterService.findJobCenterByCode(jobAdvertisement.getJobCenterCode(), contactLocale);
        final String stellennummer = extractStellennummer(jobAdvertisement);
        final String subject = hasContactLanguage(jobAdvertisement) ? JOB_ADVERTISEMENT_REFINED_SUBJECT : JOB_ADVERTISEMENT_REFINED_MULTILINGUAL_SUBJECT;
        final String template = hasContactLanguage(jobAdvertisement) ? JOB_ADVERTISEMENT_REFINED_TEMPLATE : JOB_ADVERTISEMENT_REFINED_MULTILINGUAL_TEMPLATE;
        mailSenderService.send(new MailSenderData.Builder()
                .setTo(parseMultipleAddresses(jobAdvertisement.getContact().getEmail()))
                .setSubject(messageSource.getMessage(subject, new Object[]{jobAdvertisement.getJobContent().getJobDescriptions().get(0).getTitle(), stellennummer}, contactLocale))
                .setTemplateName(template)
                .setTemplateVariables(
                        new ImmutableMap.Builder<String, Object>()
                                .put("stellennummer", stellennummer)
                                .put("showReportingObligation", showReportingObligation(jobAdvertisement))
                                .put("reportingObligationEndDate", nullSafeFormatLocalDate(jobAdvertisement.getReportingObligationEndDate()))
                                .put("jobAdvertisementId", jobAdvertisement.getId().getValue())
                                .put("accessToken", jobAdvertisement.getOwner().getAccessToken())
                                .put("jobCenter", jobCenter)
                                .put("numberOfJobs", toInt(jobAdvertisement.getJobContent().getNumberOfJobs()))
                                .build())
                .setLocale(contactLocale)
                .build());
    }

    @EventListener
    void onCancelled(JobAdvertisementCancelledEvent event) {
        final JobAdvertisement jobAdvertisement = getJobAdvertisement(event.getAggregateId());
        if (jobAdvertisement.getSourceSystem().equals(SourceSystem.API) && (jobAdvertisement.getStellennummerAvam() == null)) {
            return;
        }
        if (hasNoContactEmail(jobAdvertisement.getContact())) {
            return;
        }
        LOG.debug("EVENT catched for mail: JOB_ADVERTISEMENT_CANCELLED for JobAdvertisementId: '{}'", event.getAggregateId().getValue());
        final Locale contactLocale = resolveLocale(jobAdvertisement);
        final String stellennummer = extractStellennummer(jobAdvertisement);
        final JobCenter jobCenter = jobCenterService.findJobCenterByCode(jobAdvertisement.getJobCenterCode(), contactLocale);
        final String subject = hasContactLanguage(jobAdvertisement) ? JOB_ADVERTISEMENT_CANCELLED_SUBJECT : JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_SUBJECT;
        final String template = hasContactLanguage(jobAdvertisement) ? JOB_ADVERTISEMENT_CANCELLED_TEMPLATE : JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_TEMPLATE;
        mailSenderService.send(new MailSenderData.Builder()
                .setTo(parseMultipleAddresses(jobAdvertisement.getContact().getEmail()))
                .setSubject(messageSource.getMessage(subject, new Object[]{jobAdvertisement.getJobContent().getJobDescriptions().get(0).getTitle(), stellennummer}, contactLocale))
                .setTemplateName(template)
                .setTemplateVariables(ImmutableMap.of(
                        "stellennummer", stellennummer,
                        "jobCenter", jobCenter))
                .setLocale(contactLocale)
                .build());
    }

    private boolean showReportingObligation(JobAdvertisement jobAdvertisement) {
        return jobAdvertisement.isReportingObligation()
                && ((jobAdvertisement.getReportingObligationEndDate() == null) || jobAdvertisement.getReportingObligationEndDate().isAfter(TimeMachine.now().toLocalDate()));
    }

    private String extractStellennummer(JobAdvertisement jobAdvertisement) {
        return (jobAdvertisement.getStellennummerEgov() != null) ? jobAdvertisement.getStellennummerEgov() : jobAdvertisement.getStellennummerAvam();
    }

    private boolean hasContactLanguage(JobAdvertisement jobAdvertisement) {
        if (jobAdvertisement.getContact() == null) {
            return false;
        }
        Locale locale = jobAdvertisement.getContact().getLanguage();
        if (locale.getLanguage().equals(LANGUAGE_DE)) {
            return true;
        }
        if (locale.getLanguage().equals(LANGUAGE_EN)) {
            return true;
        }
        if (locale.getLanguage().equals(LANGUAGE_FR)) {
            return true;
        }
        return locale.getLanguage().equals(LANGUAGE_IT);
    }

    private boolean hasNoContactEmail(Contact contact) {
        return ((contact == null) || (contact.getEmail() == null));
    }

    private static String[] parseMultipleAddresses(String emailAddress) {
        return (emailAddress == null) ? null : emailAddress.split(EMAIL_DELIMITER);
    }

    private JobAdvertisement getJobAdvertisement(JobAdvertisementId jobAdvertisementId) throws AggregateNotFoundException {
        Optional<JobAdvertisement> jobAdvertisement = jobAdvertisementRepository.findById(jobAdvertisementId);
        return jobAdvertisement.orElseThrow(() -> new AggregateNotFoundException(JobAdvertisement.class, jobAdvertisementId.getValue()));
    }

    private String nullSafeFormatLocalDate(LocalDate date) {
        return (date != null) ? date.format(DATE_FORMATTER) : null;
    }

    private Locale resolveLocale(JobAdvertisement jobAdvertisement) {
        return hasContactLanguage(jobAdvertisement) ? jobAdvertisement.getContact().getLanguage() : new Locale(DEFAULT_LANGUAGE);
    }

}
