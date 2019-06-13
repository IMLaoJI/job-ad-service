package ch.admin.seco.jobs.services.jobadservice.application.complaint.dto.fixture;

import ch.admin.seco.jobs.services.jobadservice.application.complaint.ComplaintDto;
import ch.admin.seco.jobs.services.jobadservice.application.complaint.ComplaintType;
import ch.admin.seco.jobs.services.jobadservice.application.complaint.ContactInformationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Salutation;

import java.util.Locale;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;

public class ComplaintDtoFixture {

    public static ComplaintDto testComplaintDto() {
        return new ComplaintDto()
                .setJobAdvertisementId(job01.id().getValue())
                .setComplaintType(ComplaintType.DISCRIMINATION)
                .setComplaintMessage("meckermecker")
                .setContactInformation(
                        new ContactInformationDto()
                                .setSalutation(Salutation.MR)
                                .setName("name")
                                .setPhone("phone")
                                .setEmail("email")
                                .setContactLanguage(new Locale("de")));
    }
}
