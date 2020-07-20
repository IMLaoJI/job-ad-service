package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.jobcenter;

import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenterAddress;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenterUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class DefaultJobCenterService implements JobCenterService {

    private final Logger LOG = LoggerFactory.getLogger(DefaultJobCenterService.class);

    private final JobCenterApiClient jobCenterApiClient;

    DefaultJobCenterService(JobCenterApiClient jobCenterApiClient) {
        this.jobCenterApiClient = jobCenterApiClient;
    }

    @Override
    public String findJobCenterCode(String countryCode, String postalCode) {
        StopWatch stopWatch = new StopWatch();
        startTask(".", "jobCenterApiClient.searchJobCenterByLocation", stopWatch);
        JobCenterResource jobCenterResource = jobCenterApiClient.searchJobCenterByLocation(countryCode, postalCode);
        stopTask(stopWatch);
        return (jobCenterResource != null) ? jobCenterResource.getCode() : null;
    }

    @Override
    public JobCenter findJobCenterByCode(String code) {
        return findJobCenterByCode(code, null);
    }

    @Override
    public JobCenter findJobCenterByCode(String code, Locale language) {
        String languageKey = language == null ? Locale.GERMAN.getLanguage() : language.getLanguage();

        StopWatch stopWatch = new StopWatch();
        startTask(".", "jobCenterApiClient.searchJobCenterByCode", stopWatch);
        JobCenterResource jobCenterResource = jobCenterApiClient.searchJobCenterByCode(code, languageKey);
        stopTask(stopWatch);

        if (jobCenterResource == null) {
            return null;
        }
        return toJobCenter(jobCenterResource);
    }

    @Override
    public List<JobCenter> findAllJobCenters() {
        StopWatch stopWatch = new StopWatch();
        startTask(".", "jobCenterApiClient.findAllJobCenters", stopWatch);
        List<JobCenter> jobCenters = jobCenterApiClient.findAllJobCenters().stream()
                .map(this::toJobCenter)
                .collect(Collectors.toList());
        stopTask(stopWatch);

        return jobCenters;
    }

    @Override
    public Optional<JobCenterUser> findJobCenterUserByJobCenterUserId(String jobCenterUserId) {
        StopWatch stopWatch = new StopWatch();
        startTask(".", "jobCenterApiClient.findJobCenterUserByJobCenterUseId", stopWatch);
        Optional<JobCenterUser> jobCenterUser = jobCenterApiClient.findJobCenterUserByJobCenterUseId(jobCenterUserId)
                .map(this::toJobCenterUser);
        stopTask(stopWatch);
        return jobCenterUser;
    }

    private JobCenter toJobCenter(JobCenterResource jobCenterResource) {
        return JobCenter.builder()
                .setCode(jobCenterResource.getCode())
                .setEmail(jobCenterResource.getEmail())
                .setPhone(jobCenterResource.getPhone())
                .setFax(jobCenterResource.getFax())
                .setContactDisplayStyle(jobCenterResource.getContactDisplayStyle())
                .setAddress(toJobCenterAddress(jobCenterResource.getAddress()))
                .build();
    }

    private JobCenterUser toJobCenterUser(JobCenterUserResource jobCenterUserResource) {
        return JobCenterUser.builder()
                .setCode(jobCenterUserResource.getCode())
                .setEmail(jobCenterUserResource.getEmail())
                .setPhone(jobCenterUserResource.getPhone())
                .setFax(jobCenterUserResource.getFax())
                .setExternalId(jobCenterUserResource.getExternalId())
                .setFirstName(jobCenterUserResource.getFirstName())
                .setLastName(jobCenterUserResource.getLastName())
                .setAddress(toJobCenterAddress(jobCenterUserResource.getAddress()))
                .build();
    }

    private JobCenterAddress toJobCenterAddress(AddressResource jobCenterAddressResource) {
        return JobCenterAddress.builder()
                .setName(jobCenterAddressResource.getName())
                .setCity(jobCenterAddressResource.getCity())
                .setStreet(jobCenterAddressResource.getStreet())
                .setHouseNumber(jobCenterAddressResource.getHouseNumber())
                .setZipCode(jobCenterAddressResource.getZipCode())
                .build();
    }

    private void startTask(String prefix, String task, StopWatch stopWatch) {
        LOG.trace(prefix + " start: {}", task);
        stopWatch.start(task);
    }

    private void stopTask(StopWatch stopWatch) {
        stopWatch.stop();
        LOG.trace("finished: {} in {} ms", stopWatch.getLastTaskName(), stopWatch.getLastTaskTimeMillis());
    }
}
