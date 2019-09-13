package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.CancellationCode;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Company;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Employer;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.ws.avam.TOsteEgov;
import org.junit.Test;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.AvamDateTimeFormatter.formatLocalDate;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.JobAdvertisementToAvamAssembler.fetchFirstEmail;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures.AvamSinkCompanyFixture.testCompany;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures.AvamSinkCompanyFixture.testCompanyWithSurrogate;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures.AvamSinkEmployerFixture.testEmployer;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures.AvamSinkEmploymentFixture.fixedTermEmployment;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures.AvamSinkEmploymentFixture.permanentEmployment;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures.AvamSinkEmploymentFixture.shortTermEmployment;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures.AvamSinkJobAdFixture.testJobAd;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures.AvamSinkJobAdFixture.testJobAdWithContent;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures.AvamSinkJobContentFixture.testJobContentBuilder;
import static org.assertj.core.api.Assertions.assertThat;

public class JobAdvertisementToAvamAssemblerTest {

	@Test
	public void testFetchFirstEmail() {
		String inputEmails0 = null;
		String inputEmails1 = "";
		String inputEmails2 = "asdf.ghjk@example.org";
		String inputEmails3 = "asdf.ghjk@example.org, test@example.org";
		String inputEmails4 = "asdf.ghjk@example.org, test@example.org, fdsa@example.org";

		assertThat(fetchFirstEmail(inputEmails0)).isNull();
		assertThat(fetchFirstEmail(inputEmails1)).isNull();
		assertThat(fetchFirstEmail(inputEmails2)).isEqualTo("asdf.ghjk@example.org");
		assertThat(fetchFirstEmail(inputEmails3)).isEqualTo("asdf.ghjk@example.org");
		assertThat(fetchFirstEmail(inputEmails4)).isEqualTo("asdf.ghjk@example.org");
	}

	@Test
	public void shouldHaveCompanyAndEmployer() {
		// given
		Company company = testCompanyWithSurrogate();
		Employer employer = testEmployer();
		JobAdvertisement jobAdvertisement = testJobAdWithContent(
				testJobContentBuilder()
						.setCompany(testCompanyWithSurrogate())
						.setEmployer(testEmployer())
						.build())
				.build();

		JobAdvertisementToAvamAssembler assembler = new JobAdvertisementToAvamAssembler();

		// when
		TOsteEgov oste = assembler.toOsteEgov(jobAdvertisement, AvamAction.ANMELDUNG);

		// then
		assertThat(oste).isNotNull();

		assertThat(oste.getUntName()).isEqualTo(company.getName());
		assertThat(oste.getUntStrasse()).isEqualTo(company.getStreet());
		assertThat(oste.getUntHausNr()).isEqualTo(company.getHouseNumber());
		assertThat(oste.getUntPlz()).isEqualTo(company.getPostalCode());
		assertThat(oste.getUntOrt()).isEqualTo(company.getCity());
		assertThat(oste.getUntLand()).isEqualTo(company.getCountryIsoCode());
		assertThat(oste.isAuftraggeber()).isEqualTo(company.isSurrogate());

		assertThat(oste.getAuftraggeberName()).isEqualTo(employer.getName());
		assertThat(oste.getAuftraggeberPlz()).isEqualTo(employer.getPostalCode());
		assertThat(oste.getAuftraggeberOrt()).isEqualTo(employer.getCity());
		assertThat(oste.getAuftraggeberLand()).isEqualTo(employer.getCountryIsoCode());
	}

	@Test
	public void shouldHaveCompanyButNotEmployer() {
		// given
		Company company = testCompany();
		JobAdvertisement jobAdvertisement = testJobAdWithContent(
				testJobContentBuilder()
						.setCompany(company)
						.setEmployer(null)
						.build())
				.build();

		JobAdvertisementToAvamAssembler assembler = new JobAdvertisementToAvamAssembler();

		// when
		TOsteEgov oste = assembler.toOsteEgov(jobAdvertisement, AvamAction.ANMELDUNG);

		// then
		assertThat(oste).isNotNull();

		assertThat(oste.getUntName()).isEqualTo(company.getName());
		assertThat(oste.getUntStrasse()).isEqualTo(company.getStreet());
		assertThat(oste.getUntHausNr()).isEqualTo(company.getHouseNumber());
		assertThat(oste.getUntPlz()).isEqualTo(company.getPostalCode());
		assertThat(oste.getUntOrt()).isEqualTo(company.getCity());
		assertThat(oste.getUntLand()).isEqualTo(company.getCountryIsoCode());
		assertThat(oste.isAuftraggeber()).isEqualTo(company.isSurrogate());

		assertThat(oste.getAuftraggeberName()).isNull();
		assertThat(oste.getAuftraggeberPlz()).isNull();
		assertThat(oste.getAuftraggeberOrt()).isNull();
		assertThat(oste.getAuftraggeberLand()).isNull();
	}

	@Test
	public void shouldBeShortTermEmployment() {
		// given
		JobAdvertisement jobAdvertisement = testJobAdWithContent(
				testJobContentBuilder()
						.setEmployment(shortTermEmployment())
						.build())
				.build();

		JobAdvertisementToAvamAssembler assembler = new JobAdvertisementToAvamAssembler();

		// when
		TOsteEgov oste = assembler.toOsteEgov(jobAdvertisement, AvamAction.ANMELDUNG);

		// then
		assertThat(oste).isNotNull();

		assertThat(oste).isNotNull();
		assertThat(oste.getFristTyp()).isEqualTo(AvamCodeResolver.EMPLOYMENT_TERM_TYPE.getLeft(EmploymentTermType.SHORT_TERM));
	}

	@Test
	public void shouldbeFixedTermEmployment() {
		// given
		JobAdvertisement jobAdvertisement = testJobAdWithContent(
				testJobContentBuilder()
						.setEmployment(fixedTermEmployment())
						.build())
				.build();

		JobAdvertisementToAvamAssembler assembler = new JobAdvertisementToAvamAssembler();

		// when
		TOsteEgov oste = assembler.toOsteEgov(jobAdvertisement, AvamAction.ANMELDUNG);

		// then
		assertThat(oste).isNotNull();
		assertThat(oste.getVertragsdauer()).isNotBlank();
		assertThat(oste.getFristTyp()).isEqualTo(AvamCodeResolver.EMPLOYMENT_TERM_TYPE.getLeft(EmploymentTermType.FIXED_TERM));
	}

	@Test
	public void shouldBePermanentEmployment() {
		// given
		JobAdvertisement jobAdvertisement = testJobAdWithContent(
				testJobContentBuilder()
						.setEmployment(permanentEmployment())
						.build())
				.build();

		JobAdvertisementToAvamAssembler assembler = new JobAdvertisementToAvamAssembler();

		// when
		TOsteEgov oste = assembler.toOsteEgov(jobAdvertisement, AvamAction.ANMELDUNG);

		// then
		assertThat(oste).isNotNull();
		assertThat(oste.getFristTyp()).isEqualTo(AvamCodeResolver.EMPLOYMENT_TERM_TYPE.getLeft(EmploymentTermType.PERMANENT));
	}

	@Test
	public void shouldHaveCancellationCode() {
		// given
		JobAdvertisement jobAdvertisement = testJobAd()
				.setStatus(JobAdvertisementStatus.CANCELLED)
				.setCancellationCode(CancellationCode.OCCUPIED_OTHER)
				.build();

		JobAdvertisementToAvamAssembler assembler = new JobAdvertisementToAvamAssembler();

		// when
		TOsteEgov oste = assembler.toOsteEgov(jobAdvertisement, AvamAction.ABMELDUNG);

		// then
		assertThat(oste).isNotNull();

		assertThat(oste.getAbmeldeDatum()).isEqualTo(formatLocalDate(jobAdvertisement.getCancellationDate()));
		assertThat(oste.getAbmeldeGrundCode()).isEqualTo("7"); // JobAdvertisementToAvamAssembler.tempMapCancellationCode()
	}


}