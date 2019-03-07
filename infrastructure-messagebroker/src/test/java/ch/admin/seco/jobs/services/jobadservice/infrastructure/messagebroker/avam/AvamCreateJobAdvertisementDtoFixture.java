package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ApplyChannelDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.CompanyDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LanguageSkillDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.OccupationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicContactDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.AvamCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;

import java.time.LocalDate;

import static java.util.Collections.singletonList;

public class AvamCreateJobAdvertisementDtoFixture {

    private static LocalDate approvalDate = LocalDate.of(2019, 03, 07);

    public static AvamCreateJobAdvertisementDto testAvamCreateJobAdvertisementDto() {
        return new AvamCreateJobAdvertisementDto()
                .setStellennummerAvam("stellennummeravam")
                .setTitle("title")
                .setDescription("description")
                .setLanguageIsoCode("languageIsoCode")
                .setNumberOfJobs("1")
                .setReportingObligation(false)
                .setReportingObligationEndDate(approvalDate.plusDays(5))
                .setJobCenterCode("BE10")
                .setApprovalDate(approvalDate)
                .setEmployment(new EmploymentDto())
                .setApplyChannel(new ApplyChannelDto())
                .setCompany(new CompanyDto())
                .setContact(new ContactDto())
                .setPublicContact(new PublicContactDto())
                .setLocation(new CreateLocationDto())
                .setOccupations(singletonList(new OccupationDto()))
                .setLanguageSkills(singletonList(new LanguageSkillDto()))
                .setPublication(new PublicationDto());
    }
}
