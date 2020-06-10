package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.*;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Company;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Qualification;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Salutation;
import org.assertj.core.util.Sets;

import java.time.LocalDate;

import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationServiceForAvamTest.STELLENNUMMER_AVAM;
import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.PublicationDtoTestFixture.testPublicationDto;
import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.PublicationDtoTestFixture.testPublicationDtoWithCompanyAnonymous;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.LanguageLevel.PROFICIENT;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkExperience.MORE_THAN_1_YEAR;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.CompanyFixture.testCompany;
import static java.util.Collections.singletonList;

public class CreateJobAdvertisementFromAvamDtoTestFixture {

    public static CreateJobAdvertisementDto testCreateJobAdvertisementDto() {
        return testCreateJobAdvertisementDto(testCompany().build(), testPublicationDto());
    }

    public static CreateJobAdvertisementDto testCreateJobAdvertisementDtoWithCompanyAnonymous() {
        return testCreateJobAdvertisementDto(testCompany().build(), testPublicationDtoWithCompanyAnonymous());
    }

    public static CreateJobAdvertisementDto testCreateJobAdvertisementDto(Company company, PublicationDto publicationDto) {
        return new CreateJobAdvertisementDto()
                .setStellennummerAvam(STELLENNUMMER_AVAM)
                .setJobDescriptions(singletonList(new JobDescriptionDto().setTitle("title").setDescription("description").setLanguageIsoCode("de")))
                .setNumberOfJobs(null)
                .setReportingObligation(true)
                .setReportingObligationEndDate(LocalDate.of(2018, 1, 1))
                .setJobCenterCode("jobCenter")
                .setJobCenterUserId("Markus Meier")
                .setApprovalDate(LocalDate.of(2017, 12, 26))
                .setEmployment(new EmploymentDto()
                        .setStartDate(LocalDate.of(2018, 1, 1))
                        .setEndDate(LocalDate.of(2018, 12, 31))
                        .setShortEmployment(false)
                        .setImmediately(false)
                        .setPermanent(false)
                        .setWorkloadPercentageMin(80)
                        .setWorkloadPercentageMax(100)
                        .setWorkForms(Sets.newHashSet()))
                .setApplyChannel(new ApplyChannelDto()
                        .setRawPostAddress("rawPostAddress")
                        .setPostAddress(new AddressDto()
                                .setName("postAddressName")
                                .setStreet("postAddressStreet")
                                .setHouseNumber("123")
                                .setPostalCode("3000")
                                .setCity("postAddressCity")
                                .setPostOfficeBoxNumber("200")
                                .setPostOfficeBoxPostalCode("3000")
                                .setPostOfficeBoxCity("postAddressPostOfficeBoxCity")
                                .setCountryIsoCode("CH")
                        )
                        .setEmailAddress("emailAddress")
                        .setPhoneNumber("phoneNumber")
                        .setFormUrl("formUrl")
                        .setAdditionalInfo("additionalInfo"))
                .setCompany(CompanyDto.toDto(company))
                .setContact(new ContactDto()
                        .setSalutation(Salutation.MR)
                        .setFirstName("firstName")
                        .setLastName("lastName")
                        .setPhone("phone")
                        .setEmail("email")
                        .setLanguageIsoCode("de"))
                .setPublicContact(new PublicContactDto()
                        .setSalutation(Salutation.MR)
                        .setFirstName("man")
                        .setLastName("contact")
                        .setEmail("contact@man.example")
                        .setPhone("+41319999999"))
                .setLocation(new CreateLocationDto()
                        .setRemarks("remarks")
                        .setCity("city")
                        .setPostalCode("postalCode")
                        .setCountryIsoCode("CH"))
                .setOccupations(singletonList(new OccupationDto()
                        .setAvamOccupationCode("avamCode")
                        .setWorkExperience(MORE_THAN_1_YEAR)
                        .setEducationCode("educationCode")
                        .setQualificationCode(Qualification.SKILLED)))
                .setLanguageSkills(singletonList(new LanguageSkillDto()
                        .setLanguageIsoCode("de")
                        .setSpokenLevel(PROFICIENT)
                        .setWrittenLevel(PROFICIENT)))
                .setPublication(publicationDto);
    }

}
