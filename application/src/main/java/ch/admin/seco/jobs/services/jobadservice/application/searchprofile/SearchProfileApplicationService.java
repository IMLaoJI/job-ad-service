package ch.admin.seco.jobs.services.jobadservice.application.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.application.LocationService;
import ch.admin.seco.jobs.services.jobadservice.application.ProfessionService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LocationDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileResultDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.create.CreateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.*;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.update.UpdateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventPublisher;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileResult;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileCreatedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileDeletedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Order.desc;

@Service
@Transactional
public class SearchProfileApplicationService {

    private static Logger LOG = LoggerFactory.getLogger(SearchProfileApplicationService.class);

    private final SearchProfileRepository searchProfileRepository;

    private final LocationService locationService;

    private final ProfessionService professionService;

    public SearchProfileApplicationService(SearchProfileRepository searchProfileRepository, LocationService locationService, ProfessionService professionService) {
        this.searchProfileRepository = searchProfileRepository;
        this.locationService = locationService;
        this.professionService = professionService;
    }

    @PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.isCurrentUserOwner(#searchProfileId)")
    public SearchProfileResultDto getSearchProfile(SearchProfileId searchProfileId) throws SearchProfileNotExitsException {
        Condition.notNull(searchProfileId, "SearchProfileId can't be null");
        SearchProfile searchProfile = getById(searchProfileId);
        List<LocalityFilter> localityFilters = searchProfile.getSearchFilter().getLocalityFilters();
        List<OccupationFilter> occupationFilters = searchProfile.getSearchFilter().getOccupationFilters();

        List<LocationDto> locations = localityFilters.stream()
                .map(localityFilter -> locationService.findById(localityFilter.getLocalityId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        List<OccupationSuggestionDto> professionSuggestions = occupationFilters.stream()
                .map(occupationFilter -> professionService.findById(occupationFilter.getLabelId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(OccupationSuggestionDto::toDto)
                .collect(Collectors.toList());
        SearchProfileResultDto searchProfileResultDto = new SearchProfileResultDto();

        searchProfileResultDto.setId(searchProfile.getId().getValue());
        searchProfileResultDto.setCreatedTime(searchProfile.getCreatedTime());
        searchProfileResultDto.setUpdatedTime(searchProfile.getUpdatedTime());
        searchProfileResultDto.setName(searchProfile.getName());
        searchProfileResultDto.setUserOwnerId(searchProfile.getOwnerUserId());
        searchProfileResultDto.setLocations(locations);
        searchProfileResultDto.setOccupationSuggestions(professionSuggestions);

        return searchProfileResultDto;
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

    @PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.isCurrentUserOwner(#searchProfileId)")
    public void deleteSearchProfile(SearchProfileId searchProfileId) {
        Condition.notNull(searchProfileId, "SearchProfileId can't be null");
        SearchProfile searchProfile = getById(searchProfileId);
        DomainEventPublisher.publish(new SearchProfileDeletedEvent(searchProfile));
        this.searchProfileRepository.delete(searchProfile);
        LOG.debug("SearchProfile {} has been deleted for user {}.", searchProfile.getId().getValue(), searchProfile.getOwnerUserId());
    }

    @PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.isCurrentUserOwner(#ownerUserId)")
    public Page<SearchProfileResultDto> getSearchProfiles(String ownerUserId, int page, int size) {
        Condition.notNull(ownerUserId, "OwnerUserId can't be null");
        Pageable pageable = PageRequest.of(page, size, Sort.by(desc("updatedTime"), desc("createdTime")));

        List<SearchProfile> searchProfileList = this.searchProfileRepository.findAllByOwnerUserId(ownerUserId, pageable);
        List<SearchProfileResultDto> searchProfileResultDtos = new ArrayList<>();
        for (SearchProfile searchProfile : searchProfileList) {
            SearchProfileResult searchProfileResult = new SearchProfileResult(
                    searchProfile.getId().getValue()
                    , searchProfile.getCreatedTime()
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
                .setRadiusSearchFilter(convertRadiusSearchFromDto(searchFilterDto.getRadiusSearchFilter()))
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

    private RadiusSearchFilter convertRadiusSearchFromDto(RadiusSearchFilterDto radiusSearchFilterDto) {
        if (radiusSearchFilterDto == null) {
            return null;
        }
        return new RadiusSearchFilter(radiusSearchFilterDto.getGeoPoint(), radiusSearchFilterDto.getDistance());
    }
}
