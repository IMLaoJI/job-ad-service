package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.jobcenter;

import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenterAddress;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenterUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class DefaultJobCenterService implements JobCenterService {

    private final JobCenterApiClient jobCenterApiClient;

    DefaultJobCenterService(JobCenterApiClient jobCenterApiClient) {
        this.jobCenterApiClient = jobCenterApiClient;
    }

    @Override
    public String findJobCenterCode(String countryCode, String postalCode) {
        JobCenterResource jobCenterResource = jobCenterApiClient.searchJobCenterByLocation(countryCode, postalCode);
        return (jobCenterResource != null) ? jobCenterResource.getCode() : null;
    }

    @Override
    public JobCenter findJobCenterByCode(String code) {
        return findJobCenterByCode(code, null);
    }

    @Override
    public JobCenter findJobCenterByCode(String code, Locale language) {
        String languageKey = language == null ? Locale.GERMAN.getLanguage() : language.getLanguage();
        JobCenterResource jobCenterResource = jobCenterApiClient.searchJobCenterByCode(code, languageKey);
        if (jobCenterResource == null) {
            return null;
        }
        return toJobCenter(jobCenterResource);
    }

    @Override
    public List<JobCenter> findAllJobCenters() {
        return jobCenterApiClient.findAllJobCenters().stream()
                .map(this::toJobCenter)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<JobCenterUser> findJobCenterUserByJobCenterUserId(String jobCenterUserId) {
        return jobCenterApiClient.findJobCenterUserByJobCenterUseId(jobCenterUserId);
    }

    private JobCenter toJobCenter(JobCenterResource jobCenterResource) {
        AddressResource jobCenterAddressResource = jobCenterResource.getAddress();
        return new JobCenter(
                jobCenterResource.getId(),
                jobCenterResource.getCode(),
                jobCenterResource.getEmail(),
                jobCenterResource.getPhone(),
                jobCenterResource.getFax(),
                jobCenterResource.getContactDisplayStyle(),
                toJobCenterAddress(jobCenterAddressResource)
        );
    }

    private JobCenterAddress toJobCenterAddress(AddressResource jobCenterAddressResource) {
        return new JobCenterAddress(
                jobCenterAddressResource.getName(),
                jobCenterAddressResource.getCity(),
                jobCenterAddressResource.getStreet(),
                jobCenterAddressResource.getHouseNumber(),
                jobCenterAddressResource.getZipCode()
        );
    }
}
