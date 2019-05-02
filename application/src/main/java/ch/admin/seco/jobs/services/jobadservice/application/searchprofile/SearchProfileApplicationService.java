package ch.admin.seco.jobs.services.jobadservice.application.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.CreateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileResult;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.UpdateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SearchProfileApplicationService {

    private static Logger LOG = LoggerFactory.getLogger(SearchProfileApplicationService.class);

    private final SearchProfileRepository searchProfileRepository;

    public SearchProfileApplicationService(SearchProfileRepository searchProfileRepository) {
        this.searchProfileRepository = searchProfileRepository;
    }

    @PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.matchesCurrentUserId(#createFavouriteItemDto.ownerUserId)")
    public SearchProfileDto getSearchProfile(SearchProfileId searchProfileId) {
        return null;
    }

    @PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.matchesCurrentUserId(#createSearchProfileDto.ownerUserId)")
    public SearchProfileDto createSearchProfile(CreateSearchProfileDto createSearchProfileDto) {
        Condition.notNull(createSearchProfileDto, "CreateSearchProfileDto can't be null");
        return null;
    }

    @PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.isCurrentUserOwner(#updateSearchProfileDto.ownerUserId)")
    public SearchProfileDto updateSearchProfile(UpdateSearchProfileDto updateSearchProfileDto) {
        return null;
    }

    @PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.isCurrentUserOwner(#createFavouriteItemDto.ownerUserId)")
    public void deleteSearchProfile(SearchProfileId searchProfileId) {

    }

    @PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.isCurrentUserOwner(ownerUserId)")
    public SearchProfileResult getSearchProfiles(String ownerUserId) {
        return null;
    }
}
