package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobAdvertisementSearchService {

    enum SearchSort {
        score,
        date_asc,
        date_desc
    }

    // TODO return the ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementSearchResult
    Page<JobAdvertisementSearchResult> search(JobAdvertisementSearchRequest jobSearchRequest, int page, int size, JobAdvertisementSearchService.SearchSort sort);

    // TODO implement used for the favorite item widget and the job-ad favorite item page on the logged in user dashboard
    Page<JobAdvertisementSearchResult> findByUserId(String ownerId, int page, int size);

    Page<JobAdvertisementDto> searchManagedJobAds(ManagedJobAdSearchRequest searchRequest, Pageable pageable);

    long count(JobAdvertisementSearchRequest jobSearchRequest);
}
