package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobContent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobDescription;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.LanguageSkill;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Occupation;

import java.util.Collections;
import java.util.Locale;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures.AvamSinkCompanyFixture.testCompany;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures.AvamSinkEmployerFixture.testEmployer;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures.AvamSinkEmploymentFixture.permanentEmployment;

public class AvamSinkJobContentFixture {

	public static JobContent.Builder testJobContentBuilder() {
		JobDescription jobDescription = new JobDescription.Builder()
				.setLanguage(Locale.GERMAN)
				.setTitle("title")
				.setDescription("description")
				.build();
		Occupation occupation = new Occupation.Builder()
				.setAvamOccupationCode("avamOccupationCode")
				.build();
		LanguageSkill languageSkill = new LanguageSkill.Builder()
				.setLanguageIsoCode("de")
				.build();

		return new JobContent.Builder()
				.setJobDescriptions(Collections.singletonList(jobDescription))
				.setOccupations(Collections.singletonList(occupation))
				.setLanguageSkills(Collections.singletonList(languageSkill))
				.setCompany(testCompany())
				.setEmployment(permanentEmployment())
				.setEmployer(testEmployer());
	}

}
