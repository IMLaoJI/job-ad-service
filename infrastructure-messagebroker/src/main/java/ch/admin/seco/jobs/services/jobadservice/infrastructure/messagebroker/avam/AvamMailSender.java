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
public class AvamMailSender {

    private final MailSenderService mailSenderService;

    private final MessageSource messageSource;

    public AvamMailSender(MailSenderService mailSenderService, MessageSource messageSource) {
        this.mailSenderService = mailSenderService;
        this.messageSource = messageSource;
    }

    private static final String JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_SUBJECT = "mail.jobAd.cancelled.subject_multilingual";
    private static final String JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_TEMPLATE = "JobAdCancelledMail_multilingual.html";
    private static final String DEFAULT_LANGUAGE = "";

    void sendCancellation(AvamCancellationDto avamCancellationDto, JobCenter jobCenter) {
        final Map<String, Object> variables = new HashMap<>();
        variables.put("stellennummer", avamCancellationDto.getStellennummerAvam());
        variables.put("jobCenter", jobCenter);
        MailSenderData mailSenderData = new MailSenderData.Builder()
                .setTo(avamCancellationDto.getContactEmail())
                .setSubject(messageSource.getMessage(JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_SUBJECT,
                        new Object[]{avamCancellationDto.getJobDescriptionTitle(), avamCancellationDto.getStellennummerAvam()}, new Locale(DEFAULT_LANGUAGE)))
                .setTemplateName(JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_TEMPLATE)
                .setTemplateVariables(variables)
                .setLocale(new Locale(DEFAULT_LANGUAGE))
                .build();
        mailSenderService.send(mailSenderData);
    }
}
