package ch.admin.seco.jobs.services.jobadservice.application.searchprofile;

import ch.admin.seco.jobs.services.jobadservice.application.IsAdmin;
import ch.admin.seco.jobs.services.jobadservice.application.IsSysAdmin;
import ch.admin.seco.jobs.services.jobadservice.application.LocationService;
import ch.admin.seco.jobs.services.jobadservice.application.ProfessionService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LocationDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.CreateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.JobAlertDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.JobAlertMaxAmountReachedException;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.ResolvedSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.SearchProfileResultDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.UpdateSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.CantonFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.LocalityFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.OccupationFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.ResolvedOccupationFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.ResolvedSearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.SearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventPublisher;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.LanguageProvider;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileCreatedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.events.SearchProfileDeletedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.Interval;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.JobAlert;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.CantonFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.LocalityFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.OccupationFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

	private final LanguageProvider languageProvider;

	private static final int MAX_AMOUNT_JOB_ALERTS = 5;

	public SearchProfileApplicationService(SearchProfileRepository searchProfileRepository,
	                                       LocationService locationService,
	                                       ProfessionService professionService, LanguageProvider languageProvider) {
		this.searchProfileRepository = searchProfileRepository;
		this.locationService = locationService;
		this.professionService = professionService;
		this.languageProvider = languageProvider;
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
		LOG.info("SearchProfile {} has been created for user {}.", newSearchProfile.getId().getValue(), newSearchProfile.getOwnerUserId());
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
	public ResolvedSearchProfileDto subscribeToJobAlert(SearchProfileId searchProfileId, JobAlertDto jobAlertDto) {
		SearchProfile searchProfile = getById(searchProfileId);
		if (this.isMaximumAmountOfJobAlertsReached(searchProfile.getId(), searchProfile.getOwnerUserId())) {
			throw new JobAlertMaxAmountReachedException();
		}
		searchProfile.subscribeToJobAlert(jobAlertDto.getInterval(), jobAlertDto.getEmail(), this.languageProvider.getSupportedLocale().toString());
		LOG.info("JobAlert has been subscribed to for SearchProfile with ID: {}, for User: {}", searchProfile.getId().getValue(), searchProfile.getOwnerUserId());
		return toResolvedSearchProfileDto(searchProfile);
	}

	public void unsubscribeFromJobAlert(SearchProfileId searchProfileId) {
		Condition.notNull(searchProfileId, "searchProfileId can't be null");
		SearchProfile searchProfile = getById(searchProfileId);
		searchProfile.unsubscribeFromJobAlert();
		LOG.info("JobAlert has been unsubscribed from for SearchProfile with ID: {}, for User: {}", searchProfile.getId().getValue(), searchProfile.getOwnerUserId());
	}

	@PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.isCurrentUserOwner(#searchProfileId)")
	public void deleteSearchProfile(SearchProfileId searchProfileId) {
		Condition.notNull(searchProfileId, "SearchProfileId can't be null");
		delete(getById(searchProfileId));
	}

	@PreAuthorize("isAuthenticated() and @searchProfileAuthorizationService.matchesCurrentUserId(#ownerUserId)")
	public Page<SearchProfileResultDto> getSearchProfiles(String ownerUserId, int page, int size) {
		Condition.notNull(ownerUserId, "OwnerUserId can't be null");
		Pageable pageable = PageRequest.of(page, size, Sort.by(desc("updatedTime"), desc("createdTime")));
		Page<SearchProfile> searchProfiles = this.searchProfileRepository.findAllByOwnerUserId(ownerUserId, pageable);
		List<SearchProfileResultDto> result = toSearchProfileResults(searchProfiles.getContent());
		return new PageImpl<>(result, pageable, searchProfiles.getTotalElements());
	}

	@IsSysAdmin
	public void deleteUserSearchProfiles(String ownerUserId) {
		Condition.notNull(ownerUserId, "OwnerUserId can't be null");
		List<SearchProfile> searchProfiles = this.searchProfileRepository.findAllByOwnerUserId(ownerUserId);
		searchProfiles.forEach(this::delete);
	}


	//	manually trigger jobalerts for testing purposes
	@IsSysAdmin
	@IsAdmin
	public void manualReleaseJobAlert(SearchProfileId searchProfileId) {
		SearchProfile searchProfile = this.getById(searchProfileId);
		searchProfile.release();
	}

	@IsSysAdmin
	public void jobAlertHousekeeping(LocalDateTime localDateTime) {
		final List<SearchProfile> jobAlertsCreatedBefore = this.searchProfileRepository.findJobAlertsCreatedBefore(localDateTime);
		jobAlertsCreatedBefore.forEach(searchProfile -> {
			LOG.info("Unsubscribing JobAlert from SearchProfile with id: '{}'", searchProfile.getId());
			this.unsubscribeFromJobAlert(searchProfile.getId());
		});
	}

	public void releaseJobAlerts() {
		List<SearchProfile> searchProfiles = this.searchProfileRepository.findJobAlertsByNextReleaseAtBefore(TimeMachine.now());
		searchProfiles.forEach(SearchProfile::release);
	}


	public SearchProfile getById(SearchProfileId id) {
		return this.searchProfileRepository.findById(id).orElseThrow(() -> new SearchProfileNotExitsException(id));
	}

	public ResolvedSearchProfileDto toResolvedSearchProfileDto(SearchProfile searchProfile) {
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
				.setSearchFilter(toResolvedSearchFilterDto(searchFilter, locations, occupations))
				.setInterval(extractInterval(searchProfile.getJobAlert()));
	}

	private void delete(SearchProfile searchProfile) {
		if (searchProfile.getJobAlert() != null) {
			this.unsubscribeFromJobAlert(searchProfile.getId());
		}
		this.searchProfileRepository.delete(searchProfile);
		DomainEventPublisher.publish(new SearchProfileDeletedEvent(searchProfile));
		LOG.info("SearchProfile {} has been deleted for user {}.", searchProfile.getId().getValue(), searchProfile.getOwnerUserId());
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
				.setOwnerUserId(searchProfile.getOwnerUserId())
				.setInterval(extractInterval(searchProfile.getJobAlert()));
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

	private boolean isMaximumAmountOfJobAlertsReached(SearchProfileId searchProfileId, String ownerUserId) {
		final List<SearchProfile> searchProfiles = this.searchProfileRepository.findSearchProfilesWithJobAlertByOwner(ownerUserId);
		final List<SearchProfileId> searchProfileIds = searchProfiles.stream()
				.filter(searchProfile -> !searchProfile.getId().equals(searchProfileId))
				.map(SearchProfile::getId).collect(Collectors.toList());
		return searchProfileIds.size() >= MAX_AMOUNT_JOB_ALERTS;
	}

	private Interval extractInterval(JobAlert jobAlert) {
		if (jobAlert == null) {
			return null;
		}
		return jobAlert.getInterval();
	}

}
