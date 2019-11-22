package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.ApprovalDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.UpdateJobAdvertisementFromAvamDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;

import java.time.LocalDate;

import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementApplicationServiceForAvamTest.STELLENNUMMER_AVAM;
import static ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.fixture.UpdateJobAdvertisementFromAvamDtoTestFixture.testUpdateJobAdvertisementFromAvamDto;


public class ApprovalDtoTestFixture {

    public static ApprovalDto testApprovalDto(JobAdvertisement jobAdvertisement) {

        UpdateJobAdvertisementFromAvamDto updateJobAdAvamDto = testUpdateJobAdvertisementFromAvamDto(jobAdvertisement);
        updateJobAdAvamDto.setApprovalDate(LocalDate.of(2018, 1, 1));
        updateJobAdAvamDto.setReportingObligation(true);
        updateJobAdAvamDto.setReportingObligationEndDate(LocalDate.of(2018, 10, 1));
        updateJobAdAvamDto.setStellennummerAvam(STELLENNUMMER_AVAM);
        updateJobAdAvamDto.setJobCenterCode("job-center-id");

        return new ApprovalDto(
                jobAdvertisement.getId().getValue(),
                updateJobAdAvamDto.getStellennummerAvam(),
                updateJobAdAvamDto.getApprovalDate(),
                updateJobAdAvamDto.isReportingObligation(),
                updateJobAdAvamDto.getReportingObligationEndDate(),
                updateJobAdAvamDto.getJobCenterCode(),
                updateJobAdAvamDto.getJobCenterUserId(),
                updateJobAdAvamDto
        );
    }
}
