package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;
import com.google.common.collect.ImmutableMap;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

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
        MailSenderData mailSenderData = new MailSenderData.Builder()
                .setTo(avamCancellationDto.getContactEmail())
                .setSubject(messageSource.getMessage(JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_SUBJECT,
                        new Object[]{avamCancellationDto.getJobDescriptionTitle(), avamCancellationDto.getStellennummerAvam()}, new Locale(DEFAULT_LANGUAGE)))
                .setTemplateName(JOB_ADVERTISEMENT_CANCELLED_MULTILINGUAL_TEMPLATE)
                .setTemplateVariables(ImmutableMap.of("stellennummer", avamCancellationDto.getStellennummerAvam(), "jobCenter", jobCenter))
                .setLocale(new Locale(DEFAULT_LANGUAGE))
                .build();
        mailSenderService.send(mailSenderData);
    }

}
