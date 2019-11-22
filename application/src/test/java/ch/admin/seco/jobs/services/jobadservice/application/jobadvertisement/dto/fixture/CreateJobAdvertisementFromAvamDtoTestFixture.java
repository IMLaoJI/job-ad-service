package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.*;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.AvamCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Company;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Qualification;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Salutation;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkForm;

import java.time.LocalDate;
import java.util.Collections;

import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationServiceForAvamTest.STELLENNUMMER_AVAM;
import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.PublicationDtoTestFixture.testPublicationDto;
import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.PublicationDtoTestFixture.testPublicationDtoWithCompanyAnonymous;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.LanguageLevel.PROFICIENT;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkExperience.MORE_THAN_1_YEAR;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.CompanyFixture.testCompany;
import static com.google.common.collect.Sets.immutableEnumSet;
import static java.time.LocalDate.now;

public class CreateJobAdvertisementFromAvamDtoTestFixture {

    public static AvamCreateJobAdvertisementDto testCreateJobAdvertisementDto() {
        return testCreateJobAdvertisementDto(testCompany().build(), testPublicationDto());
    }

    public static AvamCreateJobAdvertisementDto testCreateJobAdvertisementDtoWithCompanyAnonymous() {
        return testCreateJobAdvertisementDto(testCompany().build(), testPublicationDtoWithCompanyAnonymous());
    }

    public static AvamCreateJobAdvertisementDto testCreateJobAdvertisementDto(Company company, PublicationDto publicationDto) {
        return new AvamCreateJobAdvertisementDto(
                STELLENNUMMER_AVAM,
                "title",
                "description",
                "de",
                null,
                true,
                LocalDate.of(2018, 1, 1),
                "jobCenter", "14711",
                now(),
                new EmploymentDto()
                        .setStartDate(LocalDate.of(2018, 1, 1))
                        .setEndDate(LocalDate.of(2018, 12, 31))
                        .setShortEmployment(false)
                        .setImmediately(false)
                        .setPermanent(false)
                        .setWorkloadPercentageMin(80)
                        .setWorkloadPercentageMax(100)
                        .setWorkForms(immutableEnumSet(WorkForm.HOME_WORK, WorkForm.SHIFT_WORK)),
                new ApplyChannelDto()
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
                        .setAdditionalInfo("additionalInfo"),
                CompanyDto.toDto(company),
                new ContactDto()
                        .setSalutation(Salutation.MR)
                        .setFirstName("firstName")
                        .setLastName("lastName")
                        .setPhone("phone")
                        .setEmail("email")
                        .setLanguageIsoCode("de"),
                new CreateLocationDto()
                        .setRemarks("remarks")
                        .setCity("city")
                        .setPostalCode("postalCode")
                        .setCountryIsoCode("CH"),
                Collections.singletonList(new OccupationDto()
                        .setAvamOccupationCode("avamCode")
                        .setWorkExperience(MORE_THAN_1_YEAR)
                        .setEducationCode("educationCode")
                        .setQualificationCode(Qualification.SKILLED)),
                Collections.singletonList(new LanguageSkillDto()
                        .setLanguageIsoCode("de")
                        .setSpokenLevel(PROFICIENT)
                        .setWrittenLevel(PROFICIENT)),
                publicationDto,
                new PublicContactDto()
                    .setSalutation(Salutation.MR)
                    .setFirstName("man")
                    .setLastName("contact")
                    .setEmail("contact@man.example")
                    .setPhone("+41319999999")
        );
    }

}
