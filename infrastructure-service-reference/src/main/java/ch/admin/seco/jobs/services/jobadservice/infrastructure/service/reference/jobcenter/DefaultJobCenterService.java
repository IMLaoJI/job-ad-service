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
        return jobCenterApiClient.findJobCenterUserByJobCenterUseId(jobCenterUserId)
                .map(this::toJobCenterUser);
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
}
