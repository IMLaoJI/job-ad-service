package ch.admin.seco.jobs.services.jobadservice.domain;

import java.util.Locale;
import java.util.function.Supplier;

public class LanguageProvider {

	private final Supplier<Locale> localeProvider;

	public LanguageProvider(Supplier<Locale> localeProvider) {
		this.localeProvider = localeProvider;
	}

	/**
	 * Supported languages for Job-Room: EN/DE/FR/IT
	 * @return the supported Locale
	 */
	public Language getSupportedLocale() {
		Locale locale = this.localeProvider.get();
		switch (locale.getLanguage()) {
			case "en":
				return Language.en;
			case "fr":
				return Language.fr;
			case "it":
				return Language.it;
			case "de":
			default:
				return Language.de;
		}
	}

}
