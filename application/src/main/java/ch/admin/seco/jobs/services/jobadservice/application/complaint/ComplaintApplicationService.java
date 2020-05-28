package ch.admin.seco.jobs.services.jobadservice.application.complaint;

import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Transactional
public class ComplaintApplicationService {

    private static Logger LOG = LoggerFactory.getLogger(ComplaintApplicationService.class);

    private final MailSenderService mailSenderService;

    private final JobAdvertisementRepository jobAdvertisementRepository;

    private final ComplaintProperties complaintProperties;

    private static final String COMPLAINT_SUBJECT = "mail.complaint.subject";
    private static final String COMPLAINT_TEMPLATE = "Complaint.html";

    @Value("${alv.complaint.toggle.reportAdvertisementLink.visible}")
    private boolean reportAdvertisementLinkVisible;

    public ComplaintApplicationService(MailSenderService mailSenderService, ComplaintProperties complaintProperties, JobAdvertisementRepository jobAdvertisementRepository) {
        this.mailSenderService = mailSenderService;
        this.complaintProperties = complaintProperties;
        this.jobAdvertisementRepository = jobAdvertisementRepository;
    }

    public void sendComplaint(ComplaintDto complaintDto) {
        if (this.reportAdvertisementLinkVisible) {
            Condition.notNull(complaintDto);
            Condition.notEmpty(complaintDto.getJobAdvertisementId());
            JobAdvertisement jobAdvertisement = getJobAdvertisement(new JobAdvertisementId(complaintDto.getJobAdvertisementId()));

            LOG.info("Sending complaint for JobAdvertisement with ID: " + complaintDto.getJobAdvertisementId());

            final Map<String, Object> variables = new HashMap<>();
            variables.put("sourceSystem", jobAdvertisement.getSourceSystem());
            variables.put("jobAdvertisementId", jobAdvertisement.getId().getValue());
            variables.put("stellennummerEgov", jobAdvertisement.getStellennummerEgov());
            variables.put("stellennummerAvam", jobAdvertisement.getStellennummerAvam());
            variables.put("complaintType", complaintDto.getComplaintType());
            variables.put("linkToJobAdDetail", complaintProperties.getLinkToJobAdDetail() + complaintDto.getJobAdvertisementId());

            MailSenderData mailSenderData = new MailSenderData.Builder()
                    .setTo(complaintProperties.getReceiverEmailAddress())
                    .setSubject(COMPLAINT_SUBJECT)
                    .setTemplateName(COMPLAINT_TEMPLATE)
                    .setTemplateVariables(variables)
                    .setLocale(Locale.GERMAN)
                    .build();

            mailSenderService.send(mailSenderData);
        }
    }

    private JobAdvertisement getJobAdvertisement(JobAdvertisementId jobAdvertisementId) {
        Optional<JobAdvertisement> jobAdvertisement = jobAdvertisementRepository.findById(jobAdvertisementId);
        return jobAdvertisement.orElseThrow(() -> new AggregateNotFoundException(JobAdvertisement.class, jobAdvertisementId.getValue()));
    }

}
