package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.jobcenter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
class JobCenterApiClientFallback implements JobCenterApiClient {

    private static Logger LOG = LoggerFactory.getLogger(JobCenterApiClientFallback.class);

    @Override
    public JobCenterResource searchJobCenterByLocation(String countryCode, String postalCode) {
        LOG.warn("Fallback active for JobCenterApiClientFallback.searchJobCenterByLocation(" + countryCode + "," + postalCode + ")");
        return null;
    }

    @Override
    public JobCenterResource searchJobCenterByCode(String code, String language) {
        LOG.warn("Fallback active for JobCenterApiClientFallback.searchJobCenterByCode(" + code + ")");
        return null;
    }

    @Override
    public List<JobCenterResource> findAllJobCenters() {
        LOG.warn("Fallback active for JobCenterApiClientFallback.findAllJobCenters()");
        return Collections.emptyList();
    }

}
