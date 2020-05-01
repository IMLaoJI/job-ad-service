package ch.admin.seco.jobs.services.jobadservice.application.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.JobAlertReleasedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.JobAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JobAlertMailListener {

	private static final String JOBADS = "jobads";

	private static final String SEARCH_PROFILE = "searchProfile";

	private static final String DEREGISTER_URL = "deregisterUrl";

	private static final String BASE_URL = "baseUrl";

	private static final String JOB_ALERT_RELEASED_MAIL_TEMPLATE = "JobAlertReleasedMail.html";

	private static final String JOB_ALERT_RELEASED_SUBJECT = "mail.jobalert.subject";

	private static Logger LOGGER = LoggerFactory.getLogger(JobAlertMailListener.class);

	private final MailSenderService mailSenderService;

	private final MessageSource messageSource;

	private final JobAdvertisementRepository jobAdvertisementRepository;



	@Value("${mail.sender.base-url:}")
	private String baseUrl;

	public JobAlertMailListener(MailSenderService mailSenderService, MessageSource messageSource, JobAdvertisementRepository jobAdvertisementRepository) {
		this.mailSenderService = mailSenderService;
		this.messageSource = messageSource;
		this.jobAdvertisementRepository = jobAdvertisementRepository;
	}

	@EventListener
	public void onRelease(JobAlertReleasedEvent jobAlertReleasedEvent) {
		final List<JobAdvertisement> jobAdvertisements = jobAlertReleasedEvent.getMatchedIds().stream()
				.map(jobAdvertisementRepository::findById)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.sorted(Comparator.comparing((JobAdvertisement jobAd) -> (jobAd.getPublication().getStartDate())).reversed())
				.collect(Collectors.toList());

		final Map<String, Object> variables = new HashMap<>();
		final SearchProfile searchProfile = jobAlertReleasedEvent.getSearchProfile();
		variables.put(JOBADS, jobAdvertisements);
		variables.put(SEARCH_PROFILE, searchProfile);
		variables.put(DEREGISTER_URL, buildDeregisterUrl(searchProfile));
		variables.put(BASE_URL, baseUrl);

		JobAlert jobAlert = searchProfile.getJobAlert();
		LOGGER.info("Sending Job-Alert Mail to user: {}", jobAlert.getEmail());

		mailSenderService.send(new MailSenderData.Builder()
				.setTo(jobAlert.getEmail())
				.setSubject(messageSource.getMessage(JOB_ALERT_RELEASED_SUBJECT, new Object[]{searchProfile.getName()}, new Locale(jobAlert.getLanguage())))
				.setTemplateName(JOB_ALERT_RELEASED_MAIL_TEMPLATE)
				.setTemplateVariables(variables)
				.setLocale(new Locale(jobAlert.getLanguage()))
				.build());
	}

	private String buildDeregisterUrl(SearchProfile searchProfile) {
		return MessageFormat.format("{0}'/job-search-profiles/unsubscribe-job-alert/'{1}'?token='{2}", baseUrl, searchProfile.getId().getValue(), searchProfile.getJobAlert().getAccessToken());
	}

}
