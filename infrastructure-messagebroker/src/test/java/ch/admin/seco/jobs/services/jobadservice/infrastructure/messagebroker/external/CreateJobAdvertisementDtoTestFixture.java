package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.external;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.CompanyDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LanguageSkillDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.OccupationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Company;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.LanguageLevel;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Qualification;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Salutation;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkExperience;

import java.time.LocalDate;
import java.util.Collections;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.CompanyFixture.testCompany;

public class CreateJobAdvertisementDtoTestFixture {

	public static CreateJobAdvertisementDto testCreateJobAdvertisementDto(Company company) {
		return new CreateJobAdvertisementDto()
				.setStellennummerEgov("stellennummerEgov")
				.setStellennummerAvam("stellennummerAvam")
				.setNumberOfJobs("numberOfJobs")
				.setFingerprint("fingerprint")
				.setExternalUrl("externalUrl")
				.setJobCenterCode("jobCenterCode")
				.setContact(new ContactDto()
						.setSalutation(Salutation.MR)
						.setFirstName("firstName")
						.setLastName("lastName")
						.setPhone("phone")
						.setEmail("email")
						.setLanguageIsoCode("de"))
				.setEmployment(
						new EmploymentDto()
								.setStartDate(LocalDate.of(2018, 1, 1))
								.setEndDate(LocalDate.of(2018, 12, 31))
								.setShortEmployment(false)
								.setImmediately(false)
								.setPermanent(false)
								.setWorkloadPercentageMin(100)
								.setWorkloadPercentageMax(100)
								.setWorkForms(null))
				.setCompany(new CompanyDto()
						.setName("companyName")
						.setStreet("companyStreet")
						.setHouseNumber("companyHouseNumber")
						.setPostalCode("companyPostalCode")
						.setCity("companyCity")
						.setCountryIsoCode("CH")
						.setPhone("companyPhone")
						.setEmail("companyEmail")
						.setWebsite("companyWebsite")
						.setPostOfficeBoxCity(null)
						.setPostOfficeBoxNumber(null)
						.setPostOfficeBoxPostalCode(null)
						.setSurrogate(false))
				.setLocation(new CreateLocationDto()
						.setCity("locationCity")
						.setPostalCode("locationPostalCode")
						.setCountryIsoCode("CH"))
				.setOccupations(Collections.singletonList(new OccupationDto().setAvamOccupationCode("avamOccupationCode")
						.setWorkExperience(WorkExperience.MORE_THAN_1_YEAR)
						.setEducationCode("educationCode")
						.setQualificationCode(Qualification.SKILLED)))
				.setProfessionCodes("professionCodes")
				.setLanguageSkills(Collections.singletonList(new LanguageSkillDto()
						.setLanguageIsoCode("de")
						.setSpokenLevel(LanguageLevel.PROFICIENT)
						.setWrittenLevel(LanguageLevel.INTERMEDIATE)))
				.setPublication(new PublicationDto().setStartDate(LocalDate.of(2018, 1, 1))
						.setEndDate(LocalDate.of(2018, 12, 31))
						.setCompanyAnonymous(false));
	}

	public static CreateJobAdvertisementDto testCreateJobAdvertisementDto() {
		return testCreateJobAdvertisementDto(testCompany().build());
	}
}
