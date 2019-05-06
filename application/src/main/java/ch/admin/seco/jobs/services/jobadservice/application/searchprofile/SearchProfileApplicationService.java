package ch.admin.seco.jobs.services.jobadservice.application.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileResultDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.create.CreateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.CantonFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.LocalityFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.OccupationFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.RadiusSearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.SearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.update.UpdateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventPublisher;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileResult;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileCreatedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileDeletedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.CantonFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.LocalityFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.OccupationFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.RadiusSearchFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SearchProfileApplicationService {

    private static Logger LOG = LoggerFactory.getLogger(SearchProfileApplicationService.class);

    private final SearchProfileRepository searchProfileRepository;

    public SearchProfileApplicationService(SearchProfileRepository searchProfileRepository) {
        this.searchProfileRepository = searchProfileRepository;
    }

    @PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.matchesCurrentUserId(#createFavouriteItemDto.ownerUserId)")
    public SearchProfileDto getSearchProfile(SearchProfileId searchProfileId) throws SearchProfileNotExitsException {
        Condition.notNull(searchProfileId, "SearchProfileId can't be null");
        SearchProfile searchProfile = getById(searchProfileId);

        return SearchProfileDto.toDto(searchProfile);
    }

    @PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.matchesCurrentUserId(#createSearchProfileDto.ownerUserId)")
    public SearchProfileDto createSearchProfile(CreateSearchProfileDto createSearchProfileDto) {
        Condition.notNull(createSearchProfileDto, "CreateSearchProfileDto can't be null");
        Optional<SearchProfile> tmpSearchProfile = searchProfileRepository.findByNameAndOwnerUserId(createSearchProfileDto.getName(), createSearchProfileDto.getOwnerUserId());
        if (tmpSearchProfile.isPresent()) {
            throw new SearchProfileNameAlreadyExistsException(tmpSearchProfile.get().getName(), tmpSearchProfile.get().getOwnerUserId());
        }

        SearchProfile searchProfile = new SearchProfile.Builder()
                .setId(new SearchProfileId())
                .setName(createSearchProfileDto.getName())
                .setOwnerUserId(createSearchProfileDto.getOwnerUserId())
                .setSearchFilter(convertFromDto(createSearchProfileDto.getSearchFilter()))
                .build();

        SearchProfile newSearchProfile = this.searchProfileRepository.save(searchProfile);
        LOG.debug("SearchProfile {} has been created for user {}.", newSearchProfile.getId().getValue(), newSearchProfile.getOwnerUserId());
        DomainEventPublisher.publish(new SearchProfileCreatedEvent(newSearchProfile));

        return SearchProfileDto.toDto(newSearchProfile);
    }

    @PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.isCurrentUserOwner(#updateSearchProfileDto.ownerUserId)")
    public SearchProfileDto updateSearchProfile(UpdateSearchProfileDto updateSearchProfileDto) {
        Condition.notNull(updateSearchProfileDto, "UpdateSearchProfileDto can't be null");
        SearchProfile searchProfile = getById(updateSearchProfileDto.getId());

        Optional<SearchProfile> existingNameOfSearchProfile = searchProfileRepository.findByNameAndOwnerUserId(updateSearchProfileDto.getName(), searchProfile.getOwnerUserId());
        if (existingNameOfSearchProfile.isPresent()) {
            if (!searchProfile.getId().equals(existingNameOfSearchProfile.get().getId())) {
                throw new SearchProfileNameAlreadyExistsException(searchProfile.getName(), searchProfile.getOwnerUserId());
            }
        }
        searchProfile.update(updateSearchProfileDto.getName(), convertFromDto(updateSearchProfileDto.getSearchFilter()));
        LOG.debug("{} has been updated.", searchProfile.toString());

        return SearchProfileDto.toDto(searchProfile);
    }

    @PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.isCurrentUserOwner(#createFavouriteItemDto.ownerUserId)")
    public void deleteSearchProfile(SearchProfileId searchProfileId) {
        Condition.notNull(searchProfileId, "FavouriteItemId can't be null");
        SearchProfile searchProfile = getById(searchProfileId);
        DomainEventPublisher.publish(new SearchProfileDeletedEvent(searchProfile));
        this.searchProfileRepository.delete(searchProfile);
        LOG.debug("SearchProfile {} has been deleted for user {}.", searchProfile.getId().getValue(), searchProfile.getOwnerUserId());
    }

    @PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.isCurrentUserOwner(ownerUserId)")
    public Page<SearchProfileResultDto> getSearchProfiles(String ownerUserId, int page, int size) {
        Condition.notNull(ownerUserId, "OwnerUserId can't be null");
        List<SearchProfile> searchProfileList = this.searchProfileRepository.findAllByOwnerUserId(ownerUserId);
        List<SearchProfileResultDto> searchProfileResultDtos = new ArrayList<>();
        for (SearchProfile searchProfile : searchProfileList) {
            SearchProfileResult searchProfileResult = new SearchProfileResult(
                    searchProfile.getId().getValue()
                    , searchProfile.getUpdatedTime()
                    , searchProfile.getName()
                    , searchProfile.getOwnerUserId()
            );
            searchProfileResultDtos.add(SearchProfileResultDto.toDto(searchProfileResult));
        }
        return  new PageImpl<>(searchProfileResultDtos);
    }

    private SearchProfile getById(SearchProfileId id) {
        return this.searchProfileRepository.findById(id).orElseThrow(() -> new SearchProfileNotExitsException(id));
    }

    private SearchFilter convertFromDto(SearchFilterDto searchFilterDto) {
        return new SearchFilter.Builder()
                .setSort(searchFilterDto.getSort())
                .setContractType(searchFilterDto.getContractType())
                .setKeywords(searchFilterDto.getKeywords())
                .setWorkloadPercentageMin(searchFilterDto.getWorkloadPercentageMin())
                .setWorkloadPercentageMax(searchFilterDto.getWorkloadPercentageMax())
                .setCompanyName(searchFilterDto.getCompanyName())
                .setOnlineSince(searchFilterDto.getOnlineSince())
                .setDisplayRestricted(searchFilterDto.getDisplayRestricted())
                .setEuresDisplay(searchFilterDto.getEuresDisplay())
                .setOccupationFilters(convertOccupationsFromDto(searchFilterDto.getOccupationFilters()))
                .setLocalityFilters(convertLocalitiesFromDto(searchFilterDto.getLocalityFilters()))
                .setCantonFilters(convertCantonsFromDto(searchFilterDto.getCantonFilters()))
                .setRadiusSearchFilters(convertRadiusSearchesFromDto(searchFilterDto.getRadiusSearchFilters()))
                .build();
    }

    private List<OccupationFilter> convertOccupationsFromDto(List<OccupationFilterDto> occupationFilterDtos) {
        if (CollectionUtils.isEmpty(occupationFilterDtos)) {
            return null;
        }
        return occupationFilterDtos.stream()
                .map(occupationFilterDto ->
                        new OccupationFilter(occupationFilterDto.getLabelId(), occupationFilterDto.getType()))
                .collect(Collectors.toList());
    }

    private List<LocalityFilter> convertLocalitiesFromDto(List<LocalityFilterDto> localityFilterDtos) {
        if (CollectionUtils.isEmpty(localityFilterDtos)) {
            return null;
        }
        return localityFilterDtos.stream()
                .map(localityFilterDto ->
                        new LocalityFilter(localityFilterDto.getLocalityId()))
                .collect(Collectors.toList());
    }

    private List<CantonFilter> convertCantonsFromDto(List<CantonFilterDto> cantonFilterDtos) {
        if (CollectionUtils.isEmpty(cantonFilterDtos)) {
            return null;
        }
        return cantonFilterDtos.stream()
                .map(cantonFilterDto ->
                        new CantonFilter(cantonFilterDto.getName(), cantonFilterDto.getCode()))
                .collect(Collectors.toList());
    }

    private List<RadiusSearchFilter> convertRadiusSearchesFromDto(List<RadiusSearchFilterDto> radiusSearchFilterDtos) {
        if (CollectionUtils.isEmpty(radiusSearchFilterDtos)) {
            return null;
        }
        return radiusSearchFilterDtos.stream()
                .map(radiusSearchFilterDto ->
                        new RadiusSearchFilter(radiusSearchFilterDto.getGeoPoint(), radiusSearchFilterDto.getDistance()))
                .collect(Collectors.toList());
    }
}
