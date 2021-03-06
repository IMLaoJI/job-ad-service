package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.search.JobAdvertisementSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobAdvertisementSearchService {

    enum SearchSort {
        score,
        date_asc,
        date_desc
    }

    Page<JobAdvertisementSearchResult> search(JobAdvertisementSearchRequest jobSearchRequest, int page, int size, JobAdvertisementSearchService.SearchSort sort);

    Page<JobAdvertisementSearchResult> searchFavouriteJobAds(String ownerId, String query, int page, int size);

    Page<JobAdvertisementDto> searchManagedJobAds(ManagedJobAdSearchRequest searchRequest, Pageable pageable);

}
