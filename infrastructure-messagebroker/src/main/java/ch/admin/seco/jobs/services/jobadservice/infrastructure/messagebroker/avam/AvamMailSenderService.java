package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class AvamMailSenderService {

    private final MailSenderService mailSenderService;

    private final MessageSource messageSource;

    public AvamMailSenderService(MailSenderService mailSenderService, MessageSource messageSource) {
        this.mailSenderService = mailSenderService;
        this.messageSource = messageSource;
    }

    private static final String JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_SUBJECT = "mail.jobAd.cancelled.subject_multilingual";
    private static final String JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_TEMPLATE = "JobAdCancelledMail_multilingual.html";
    private static final String DEFAULT_LANGUAGE = "";
    private static final String EMAIL_DELIMITER = "\\s*;\\s*";


    Map<String, Object> prepareTemplateVariables(AvamCancellationDto cancellationDto, JobCenter jobCenter) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("stellennummer", cancellationDto.getStellennummerAvam());
        variables.put("jobCenter", jobCenter);
        return variables;
    }

    MailSenderData prepareMailSenderData(AvamCancellationDto cancellationDto, Map<String, Object> variables) {
        return new MailSenderData.Builder()
                .setTo(parseMultipleAddresses(cancellationDto.getContactEmail()))
                .setSubject(messageSource.getMessage(JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_SUBJECT,
                        new Object[]{cancellationDto.getJobDescriptionTitle(), cancellationDto.getStellennummerAvam()}, new Locale(DEFAULT_LANGUAGE)))
                .setTemplateName(JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_TEMPLATE)
                .setTemplateVariables(variables)
                .setLocale(new Locale(DEFAULT_LANGUAGE))
                .build();
    }

    private static String[] parseMultipleAddresses(String emailAddress) {
        return (emailAddress == null) ? null : emailAddress.split(EMAIL_DELIMITER);
    }

    void send(MailSenderData mailSenderData){
        mailSenderService.send(mailSenderData);
    }
}
