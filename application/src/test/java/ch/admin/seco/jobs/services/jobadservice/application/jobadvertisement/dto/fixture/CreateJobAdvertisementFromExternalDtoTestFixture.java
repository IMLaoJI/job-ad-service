package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture;

import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.ExternalCompanyDtoFixture.testExternalCompanyDto;

import java.time.LocalDate;
import java.util.Collections;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external.ExternalCompanyDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external.ExternalCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external.ExternalLanguageSkillDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external.ExternalLocationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external.ExternalOccupationDto;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.LanguageLevel;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Qualification;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkExperience;

public class CreateJobAdvertisementFromExternalDtoTestFixture {

    public static ExternalCreateJobAdvertisementDto createCreateJobAdvertisementDto(ExternalCompanyDto externalCompanyDto) {
        return new ExternalCreateJobAdvertisementDto()
                .setStellennummerEgov(null)
                .setStellennummerAvam(null)
                .setTitle("title")
                .setDescription("description")
                .setNumberOfJobs(null)
                .setFingerprint("fingerprint")
                .setExternalUrl("url")
                .setJobCenterCode(null)
                .setCompany(externalCompanyDto)
                .setContact(null)
                .setEmployment(new EmploymentDto()
                        .setStartDate(LocalDate.of(2018, 1, 1))
                        .setEndDate(LocalDate.of(2018, 12, 31))
                        .setShortEmployment(false)
                        .setImmediately(false)
                        .setPermanent(false)
                        .setWorkloadPercentageMin(80)
                        .setWorkloadPercentageMax(100)
                        .setWorkForms(null))
                .setCompany(new ExternalCompanyDto("name", "street", "houseNumber", "postalCode", "city", "CH", null, null, null, "phone", "email", "website", false))
                .setLocation(new ExternalLocationDto(null, "city", "postalCode", null))
                .setOccupations(Collections.singletonList(new ExternalOccupationDto()
                        .setAvamOccupationCode("avamCode")
                        .setWorkExperience(WorkExperience.MORE_THAN_1_YEAR)
                        .setEducationCode( "educationCode")
                        .setQualificationCode(Qualification.SKILLED)))
                .setProfessionCodes("1,2")
                .setLanguageSkills(Collections.singletonList(new ExternalLanguageSkillDto("de", LanguageLevel.PROFICIENT, LanguageLevel.PROFICIENT)))
                .setPublicationStartDate(TimeMachine.now().toLocalDate())
                .setPublicationEndDate(null)
                .setCompanyAnonymous(false);
    }

    public static ExternalCreateJobAdvertisementDto testCreateJobAdvertisementFromExternalDto() {
        return createCreateJobAdvertisementDto(testExternalCompanyDto());
    }
}