package ch.admin.seco.jobs.services.jobadservice.application.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.application.LocationService;
import ch.admin.seco.jobs.services.jobadservice.application.ProfessionService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LocationDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.CreateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.ResolvedSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileResultDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.UpdateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.*;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventPublisher;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileCreatedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileDeletedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.CantonFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.LocalityFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.OccupationFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public ResolvedSearchProfileDto getSearchProfile(SearchProfileId searchProfileId) throws SearchProfileNotExitsException {
		Condition.notNull(searchProfileId, "SearchProfileId can't be null");
		SearchProfile searchProfile = getById(searchProfileId);

		return toResolvedSearchProfileDto(searchProfile);
	}

	@PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.matchesCurrentUserId(#createSearchProfileDto.ownerUserId)")
	public ResolvedSearchProfileDto createSearchProfile(CreateSearchProfileDto createSearchProfileDto) {
		Condition.notNull(createSearchProfileDto, "CreateSearchProfileDto can't be null");
		Optional<SearchProfile> existingSearchProfile = this.searchProfileRepository.findByNameAndOwnerUserId(createSearchProfileDto.getName(), createSearchProfileDto.getOwnerUserId());
		if (existingSearchProfile.isPresent()) {
			throw new SearchProfileNameAlreadyExistsException(existingSearchProfile.get().getName(), existingSearchProfile.get().getOwnerUserId());
		}

		SearchProfile searchProfile = new SearchProfile.Builder()
				.setId(new SearchProfileId())
				.setName(createSearchProfileDto.getName())
				.setOwnerUserId(createSearchProfileDto.getOwnerUserId())
				.setSearchFilter(toSearchFilter(createSearchProfileDto.getSearchFilter()))
				.build();

		SearchProfile newSearchProfile = this.searchProfileRepository.save(searchProfile);
		LOG.debug("SearchProfile {} has been created for user {}.", newSearchProfile.getId().getValue(), newSearchProfile.getOwnerUserId());
		DomainEventPublisher.publish(new SearchProfileCreatedEvent(newSearchProfile));

		return toResolvedSearchProfileDto(searchProfile);
	}

	@PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.isCurrentUserOwner(#searchProfileId)")
	public ResolvedSearchProfileDto updateSearchProfile(SearchProfileId searchProfileId, UpdateSearchProfileDto updateSearchProfileDto) {
		Condition.notNull(updateSearchProfileDto, "UpdateSearchProfileDto can't be null");
		SearchProfile searchProfileToUpdate = getById(searchProfileId);
		Optional<SearchProfile> searchProfileWithSameName = searchProfileRepository.findByNameAndOwnerUserId(updateSearchProfileDto.getName(), searchProfileToUpdate.getOwnerUserId());
		if (searchProfileWithSameName.isPresent() && !searchProfileToUpdate.getId().equals(searchProfileWithSameName.get().getId())) {
			throw new SearchProfileNameAlreadyExistsException(searchProfileToUpdate.getName(), searchProfileToUpdate.getOwnerUserId());
		}
		searchProfileToUpdate.update(updateSearchProfileDto.getName(), toSearchFilter(updateSearchProfileDto.getSearchFilter()));
		LOG.debug("{} has been updated.", searchProfileToUpdate.toString());

		return toResolvedSearchProfileDto(searchProfileToUpdate);
	}

	@PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.isCurrentUserOwner(#searchProfileId)")
	public void deleteSearchProfile(SearchProfileId searchProfileId) {
		Condition.notNull(searchProfileId, "SearchProfileId can't be null");
		SearchProfile searchProfile = getById(searchProfileId);
		DomainEventPublisher.publish(new SearchProfileDeletedEvent(searchProfile));
		this.searchProfileRepository.delete(searchProfile);
		LOG.debug("SearchProfile {} has been deleted for user {}.", searchProfile.getId().getValue(), searchProfile.getOwnerUserId());
	}

	@PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.matchesCurrentUserId(#ownerUserId)")
	public Page<SearchProfileResultDto> getSearchProfiles(String ownerUserId, int page, int size) {
		Condition.notNull(ownerUserId, "OwnerUserId can't be null");
		Pageable pageable = PageRequest.of(page, size, Sort.by(desc("updatedTime"), desc("createdTime")));
		Page<SearchProfile> searchProfiles = this.searchProfileRepository.findAllByOwnerUserId(ownerUserId, pageable);
		List<SearchProfileResultDto> result = toSearchProfileResults(searchProfiles.getContent());

		return new PageImpl<>(result, pageable, searchProfiles.getTotalElements());
	}

	private List<SearchProfileResultDto> toSearchProfileResults(List<SearchProfile> searchProfileList) {
		return searchProfileList.stream()
				.map(this::toSearchProfileResultDto)
				.collect(Collectors.toList());
	}

	private SearchProfileResultDto toSearchProfileResultDto(SearchProfile searchProfile) {
		return new SearchProfileResultDto()
				.setName(searchProfile.getName())
				.setId(searchProfile.getId().getValue())
				.setCreatedTime(searchProfile.getCreatedTime())
				.setOwnerUserId(searchProfile.getOwnerUserId());
	}

	private ResolvedSearchProfileDto toResolvedSearchProfileDto(SearchProfile searchProfile) {
		SearchFilter searchFilter = searchProfile.getSearchFilter();

		List<LocationDto> locations = searchFilter.getLocalityFilters().stream()
				.map(localityFilter -> locationService.findById(localityFilter.getLocalityId()))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());

		List<ResolvedOccupationFilterDto> occupations = searchFilter.getOccupationFilters().stream()
				.map(occupationFilter -> professionService.findById(occupationFilter.getLabelId()))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(ResolvedOccupationFilterDto::toDto)
				.collect(Collectors.toList());

		return new ResolvedSearchProfileDto()
				.setId(searchProfile.getId().getValue())
				.setCreatedTime(searchProfile.getCreatedTime())
				.setName(searchProfile.getName())
				.setOwnerUserId(searchProfile.getOwnerUserId())
				.setSearchFilter(toResolvedSearchFilterDto(searchFilter, locations, occupations));
	}

	private ResolvedSearchFilterDto toResolvedSearchFilterDto(SearchFilter searchFilter, List<LocationDto> locations, List<ResolvedOccupationFilterDto> occupationSuggestions) {
		return new ResolvedSearchFilterDto()
				.setSort(searchFilter.getSort())
				.setContractType(searchFilter.getContractType())
				.setKeywords(searchFilter.getKeywords())
				.setWorkloadPercentageMin(searchFilter.getWorkloadPercentageMin())
				.setWorkloadPercentageMax(searchFilter.getWorkloadPercentageMax())
				.setCompanyName(searchFilter.getCompanyName())
				.setOnlineSince(searchFilter.getOnlineSince())
				.setDisplayRestricted(searchFilter.getDisplayRestricted())
				.setEuresDisplay(searchFilter.getEuresDisplay())
				.setCantons(CantonFilterDto.toDto(searchFilter.getCantonFilters()))
				.setDistance(searchFilter.getDistance())
				.setOccupations(occupationSuggestions)
				.setLocations(locations);
	}

	private SearchProfile getById(SearchProfileId id) {
		return this.searchProfileRepository.findById(id).orElseThrow(() -> new SearchProfileNotExitsException(id));
	}

	private SearchFilter toSearchFilter(SearchFilterDto searchFilterDto) {
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
				.setOccupationFilters(toOccupationFilters(searchFilterDto.getOccupationFilters()))
				.setLocalityFilters(toLocalityFilters(searchFilterDto.getLocalityFilters()))
				.setCantonFilters(toCantonFilters(searchFilterDto.getCantonFilters()))
				.setDistance(searchFilterDto.getDistance())
				.build();
	}

	private List<OccupationFilter> toOccupationFilters(List<OccupationFilterDto> occupationFilterDtos) {
		return occupationFilterDtos.stream()
				.map(occupationFilterDto -> new OccupationFilter(occupationFilterDto.getLabelId()))
				.collect(Collectors.toList());
	}

	private List<LocalityFilter> toLocalityFilters(List<LocalityFilterDto> localityFilterDtos) {
		return localityFilterDtos.stream()
				.map(localityFilterDto -> new LocalityFilter(localityFilterDto.getLocalityId()))
				.collect(Collectors.toList());
	}

	private List<CantonFilter> toCantonFilters(List<CantonFilterDto> cantonFilterDtos) {
		return cantonFilterDtos.stream()
				.map(cantonFilterDto -> new CantonFilter(cantonFilterDto.getName(), cantonFilterDto.getCode()))
				.collect(Collectors.toList());
	}

}