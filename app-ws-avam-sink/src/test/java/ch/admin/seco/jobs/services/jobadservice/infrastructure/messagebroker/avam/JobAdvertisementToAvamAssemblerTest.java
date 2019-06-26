package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.*;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.ws.avam.TOsteEgov;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Locale;

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
        JobAdvertisement jobAdvertisement = createJobAdvertisementForCompanyAndEmployer(company, employer);

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

        JobAdvertisement jobAdvertisement = createJobAdvertisementForCompanyAndEmployer(company, null);

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
                .setJobContent(resolveJobContent(company, null))
                .setOwner(resolveOwner())
                .setPublication(resolvePublication())
                .setCancellationCode(CancellationCode.OCCUPIED_OTHER)
                .setCancellationDate(LocalDate.now())
                .build();
    }

    private JobAdvertisement createJobAdvertisementForCompanyAndEmployer(Company company, Employer employer) {
        return new JobAdvertisement.Builder()
                .setId(new JobAdvertisementId("id"))
                .setStatus(JobAdvertisementStatus.INSPECTING)
                .setSourceSystem(SourceSystem.JOBROOM)
                .setJobContent(resolveJobContent(company, employer))
                .setOwner(resolveOwner())
                .setPublication(resolvePublication())
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

    private Owner resolveOwner() {
        return new Owner.Builder()
                .setAccessToken("accessToken")
                .build();
    }

    private Publication resolvePublication() {
        return new Publication.Builder()
                .build();
    }

    private JobContent resolveJobContent(Company company, Employer employer) {
        JobDescription jobDescription = new JobDescription.Builder()
                .setLanguage(Locale.GERMAN)
                .setTitle("title")
                .setDescription("description")
                .build();
        Employment employment = new Employment.Builder()
                .setWorkloadPercentageMin(80)
                .setWorkloadPercentageMax(100)
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