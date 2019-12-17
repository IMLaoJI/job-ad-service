package ch.admin.seco.jobs.services.jobadservice.application;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenterUser;

public interface JobCenterService {

    String findJobCenterCode(String countryCode, String postalCode);

    JobCenter findJobCenterByCode(String code);

    JobCenter findJobCenterByCode(String code, Locale language);

    List<JobCenter> findAllJobCenters();

    Optional<JobCenterUser> findJobCenterUserByJobCenterUserId(String jobCenterUserId);
}
