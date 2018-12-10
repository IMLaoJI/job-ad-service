package ch.admin.seco.jobs.services.jobadservice.application;

import java.util.List;
import java.util.Locale;

import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;

public interface JobCenterService {

    String findJobCenterCode(String countryCode, String postalCode);

    JobCenter findJobCenterByCode(String code);

    JobCenter findJobCenterByCode(String code, Locale language);

    List<JobCenter> findAllJobCenters();
}
