package ch.admin.seco.jobs.services.jobadservice.infrastructure.mail;

import org.apache.commons.mail.util.IDNEmailAddressConverter;
import org.thymeleaf.spring5.SpringTemplateEngine;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.admin.seco.alv.shared.mail.MailSendingService;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;

@Configuration
@EnableConfigurationProperties(MailSenderProperties.class)
class MailSenderConfig {

	private final MailSenderProperties mailSenderProperties;

	private final MessageSource messageSource;

	private final SpringTemplateEngine templateEngine;

	private final IDNEmailAddressConverter idnEmailAddressConverter;

	private final MailSendingService mailSendingService;

	public MailSenderConfig(
			MailSenderProperties mailSenderProperties,
			MessageSource messageSource,
			SpringTemplateEngine templateEngine,
			MailSendingService mailSendingService) {
		this.mailSenderProperties = mailSenderProperties;
		this.messageSource = messageSource;
		this.templateEngine = templateEngine;
		this.mailSendingService = mailSendingService;
		this.idnEmailAddressConverter = new IDNEmailAddressConverter();
	}

	@Bean
	MailSenderService mailSenderService() {
		return new DefaultMailSenderService(
				templateEngine,
				mailSenderProperties,
				messageSource,
				idnEmailAddressConverter,
				mailSendingService
		);
	}
}
