package ch.admin.seco.jobs.services.jobadservice.infrastructure.mail;

import ch.admin.seco.alv.shared.mail.MailSendingService;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.validation.Valid;

@Validated
class DefaultMailSenderService implements MailSenderService {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultMailSenderService.class);

	private final SpringTemplateEngine templateEngine;

	private final MailSenderProperties mailSenderProperties;

	private final MessageSource messageSource;

	private final MailSendingService mailSendingService;

	DefaultMailSenderService(SpringTemplateEngine templateEngine, MailSenderProperties mailSenderProperties, MessageSource messageSource, MailSendingService mailSendingService) {
		this.templateEngine = templateEngine;
		this.mailSenderProperties = mailSenderProperties;
		this.messageSource = messageSource;
		this.mailSendingService = mailSendingService;
	}

	@Override
	public void send(@Valid MailSenderData mailSenderData) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Save email with MailSenderData={}", mailSenderData);
		}
		mailSendingService.send(toMailData(mailSenderData));
	}

	private ch.admin.seco.alv.shared.mail.MailSenderData toMailData(MailSenderData mailSenderData) {
		String subject = messageSource.getMessage(mailSenderData.getSubject(), null, mailSenderData.getSubject(), mailSenderData.getLocale());
		String content = createContent(mailSenderData);
		return new ch.admin.seco.alv.shared.mail.MailSenderData.Builder()
				.setBcc(mailSenderData.getBcc().toArray(new String[0]))
				.setCc(mailSenderData.getCc().toArray(new String[0]))
				.setContent(content)
				.setSubject(subject)
				.setTo(mailSenderData.getTo().toArray(new String[0]))
				.build();
	}

	private String createContent(MailSenderData mailSenderData) {
		return StringUtils.strip(templateEngine.process(mailSenderData.getTemplateName(), createTemplateContext(mailSenderData)));
	}

	private Context createTemplateContext(MailSenderData mailSenderData) {
		Context context = new Context();
		context.setVariable("baseUrl", mailSenderProperties.getBaseUrl());
		context.setVariable("linkToJobAdDetailPage", mailSenderProperties.getLinkToJobAdDetailPage());
		context.setVariable("user", null);
		context.setVariables(mailSenderData.getTemplateVariables());
		context.setLocale(mailSenderData.getLocale());
		return context;
	}
}
