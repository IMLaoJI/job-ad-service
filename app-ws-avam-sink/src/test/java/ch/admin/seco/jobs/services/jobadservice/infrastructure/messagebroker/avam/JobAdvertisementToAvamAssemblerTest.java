package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.CancellationCode;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Company;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Employer;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Employment;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobContent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobDescription;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.LanguageSkill;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Occupation;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Owner;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Publication;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.ws.avam.TOsteEgov;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Locale;

import static ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine.now;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.AvamDateTimeFormatter.formatLocalDate;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.JobAdvertisementToAvamAssembler.fetchFirstEmail;
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
		Company company = resolveCompanyWithSurrogate(true);
		Employer employer = new Employer.Builder()
				.setName("employerName")
				.setPostalCode("employerPostalCode")
				.setCity("employerCity")
				.setCountryIsoCode("ch")
				.build();
		JobAdvertisement jobAdvertisement = createJobAdvertisementForCompanyAndEmployer(company, employer, permanentEmployment());

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
		Company company = resolveCompanyWithSurrogate(false);

		JobAdvertisement jobAdvertisement = createJobAdvertisementForCompanyAndEmployer(company, null, permanentEmployment());

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
		Company company = resolveCompanyWithSurrogate(false);

		JobAdvertisement jobAdvertisement = createJobAdvertisementForCompanyAndEmployer(company, null, shortTermEmployment());

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
		Company company = resolveCompanyWithSurrogate(false);

		JobAdvertisement jobAdvertisement = createJobAdvertisementForCompanyAndEmployer(company, null, fixedTermEmployment());

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
		Company company = resolveCompanyWithSurrogate(false);

		JobAdvertisement jobAdvertisement = createJobAdvertisementForCompanyAndEmployer(company, null, permanentEmployment());

		JobAdvertisementToAvamAssembler assembler = new JobAdvertisementToAvamAssembler();

		// when
		TOsteEgov oste = assembler.toOsteEgov(jobAdvertisement, AvamAction.ANMELDUNG);

		// then
		assertThat(oste).isNotNull();
		assertThat(oste.getFristTyp()).isEqualTo(AvamCodeResolver.EMPLOYMENT_TERM_TYPE.getLeft(EmploymentTermType.PERMANENT));
	}



	@Test
	public void shouldHaveCancelCode() {
		// given
		JobAdvertisement jobAdvertisement = createCancelledJobAdvertisement();
		JobAdvertisementToAvamAssembler assembler = new JobAdvertisementToAvamAssembler();

		// when
		TOsteEgov oste = assembler.toOsteEgov(jobAdvertisement, AvamAction.ABMELDUNG);

		// then
		assertThat(oste).isNotNull();

		assertThat(oste.getAbmeldeDatum()).isEqualTo(formatLocalDate(jobAdvertisement.getCancellationDate()));
		assertThat(oste.getAbmeldeGrundCode()).isEqualTo("7"); // JobAdvertisementToAvamAssembler.tempMapCancellationCode()
	}

	private JobAdvertisement createCancelledJobAdvertisement() {
		Company company = resolveCompanyWithSurrogate(false);
		return new JobAdvertisement.Builder()
				.setId(new JobAdvertisementId("id"))
				.setStatus(JobAdvertisementStatus.INSPECTING)
				.setSourceSystem(SourceSystem.JOBROOM)
				.setJobContent(resolveJobContent(company, null, permanentEmployment()))
				.setOwner(resolveOwner())
				.setPublication(resolvePublication())
				.setCancellationCode(CancellationCode.OCCUPIED_OTHER)
				.setCancellationDate(LocalDate.now())
				.build();
	}

	private JobAdvertisement createJobAdvertisementForCompanyAndEmployer(Company company, Employer employer, Employment employment) {
		return new JobAdvertisement.Builder()
				.setId(new JobAdvertisementId("id"))
				.setStatus(JobAdvertisementStatus.INSPECTING)
				.setSourceSystem(SourceSystem.JOBROOM)
				.setJobContent(resolveJobContent(company, employer, employment))
				.setOwner(resolveOwner())
				.setPublication(resolvePublication())
				.build();
	}



	private static Employment permanentEmployment() {
		return new Employment.Builder()
				.setPermanent(true)
				.setWorkloadPercentageMax(100)
				.setWorkloadPercentageMin(80)
				.build();
	}

	private static Employment fixedTermEmployment() {
		return new Employment.Builder()
				.setPermanent(false)
				.setShortEmployment(false)
				.setEndDate(LocalDate.from(now().plusDays(14)))
				.setWorkloadPercentageMax(100)
				.setWorkloadPercentageMin(80)
				.build();
	}

	private static Employment shortTermEmployment() {
		return new Employment.Builder()
				.setShortEmployment(true)
				.setWorkloadPercentageMax(100)
				.setWorkloadPercentageMin(80)
				.build();
	}


	private Company resolveCompanyWithSurrogate(boolean surrogate) {
		return new Company.Builder<>()
				.setName("companyName")
				.setStreet("companyStreet")
				.setPostalCode("companyPostalCode")
				.setCity("companyCity")
				.setCountryIsoCode("ch")
				.setSurrogate(surrogate)
				.build();
	}

	private Employment resolveEmploymentWithShortEmployment() {
		return new Employment.Builder()
				.setPermanent(false)
				.setShortEmployment(true)
				.build();
	}

	private Owner resolveOwner() {
		return new Owner.Builder()
				.setAccessToken("accessToken")
				.build();
	}

	private Publication resolvePublication() {
		return new Publication.Builder()
				.build();
	}

	private JobContent resolveJobContent(Company company, Employer employer, Employment employment) {
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
				.setCompany(company)
				.setEmployer(employer)
				.setEmployment(employment)
				.setOccupations(Collections.singletonList(occupation))
				.setLanguageSkills(Collections.singletonList(languageSkill))
				.build();
	}

}