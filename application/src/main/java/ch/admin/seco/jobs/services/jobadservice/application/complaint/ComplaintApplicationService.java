package ch.admin.seco.jobs.services.jobadservice.application.complaint;

import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class ComplaintApplicationService {

    private final MailSenderService mailSenderService;

    private final MessageSource messageSource;

    private static final String COMPLAINT_SUBJECT = "mail.complaint.subject";
    private static final String COMPLAINT_BODY = "Complaint.html";

    public ComplaintApplicationService(MailSenderService mailSenderService, MessageSource messageSource) {
        this.mailSenderService = mailSenderService;
        this.messageSource = messageSource;
    }

    private static Logger LOG = LoggerFactory.getLogger(ComplaintApplicationService.class);

    public void sendComplaint(ComplaintDto complaintDto) {
        LOG.info("Sending complaint for JobAdvertisement with ID: " + complaintDto.getJobAdvertisementId());
        final Map<String, Object> variables = new HashMap<>();
        variables.put("jobAdvertisementId", complaintDto.getJobAdvertisementId());
        variables.put("contactInformation", complaintDto.getContactInformation());
        variables.put("complaintMessage", complaintDto.getComplaintMessage());

        mailSenderService.send(new MailSenderData.Builder()
                .setTo("test@example.com")
                .setSubject(messageSource.getMessage(COMPLAINT_SUBJECT, new Object[]{complaintDto.getJobAdvertisementId()}, Locale.GERMAN))
                .setTemplateName(COMPLAINT_BODY)
                .setTemplateVariables(variables)
                .setLocale(Locale.GERMAN)
                .build());
    }

}