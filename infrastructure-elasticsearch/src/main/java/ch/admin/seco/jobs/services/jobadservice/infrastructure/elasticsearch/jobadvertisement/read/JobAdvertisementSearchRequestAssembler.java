package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.ProfessionCode;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.ProfessionCodeType;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.RadiusSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.GeoPointDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LocationDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.search.JobAdvertisementSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.ResolvedSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.ResolvedOccupationFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.ResolvedSearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.GeoPoint;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.ContractType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class JobAdvertisementSearchRequestAssembler {

	private final JobAdvertisementSearchQueryBuilder jobAdvertisementSearchQueryBuilder;

	JobAdvertisementSearchRequestAssembler(JobAdvertisementSearchQueryBuilder jobAdvertisementSearchQueryBuilder) {
		this.jobAdvertisementSearchQueryBuilder = jobAdvertisementSearchQueryBuilder;
	}

	//TODO: Fago, this duplicates the logic we have in the frontend (JobSearchRequestMapper). Check if this duplication can be removed. DF-2154
	public NativeSearchQuery toJobAdvertisementSearchRequest(ResolvedSearchProfileDto resolvedSearchProfileDto) {
		final ResolvedSearchFilterDto searchFilter = resolvedSearchProfileDto.getSearchFilter();

		JobAdvertisementSearchRequest jobAdvertisementSearchRequest = new JobAdvertisementSearchRequest()
				.setCantonCodes(extractCantonCodes(resolvedSearchProfileDto))
				.setCommunalCodes(extractCommunalCodes(resolvedSearchProfileDto))
				.setKeywords(extractKeywords(resolvedSearchProfileDto))
				.setProfessionCodes(extractProfessionCodes(resolvedSearchProfileDto))
				.setRadiusSearchRequest(extractRadiusSearch(searchFilter))
				.setDisplayRestricted(searchFilter.getDisplayRestricted())
				.setEuresDisplay(searchFilter.getEuresDisplay())
				.setCompanyName(searchFilter.getCompanyName())
				.setOnlineSince(searchFilter.getOnlineSince())
				.setPermanent(isPermanent(searchFilter))
				.setWorkloadPercentageMax(searchFilter.getWorkloadPercentageMax())
				.setWorkloadPercentageMin(searchFilter.getWorkloadPercentageMin());
		return jobAdvertisementSearchQueryBuilder
				.createSearchQuery(jobAdvertisementSearchRequest, PageRequest.of(0, 20, Sort.by(Sort.Order.desc("onlineSince"))));
	}

	private RadiusSearchRequest extractRadiusSearch(ResolvedSearchFilterDto searchFilter) {
		if (searchFilter.getLocations().isEmpty()) {
			return null;
		}
		GeoPoint coordinates = searchFilter.getLocations().get(0).getCoordinates();
		return new RadiusSearchRequest()
				.setDistance(searchFilter.getDistance())
				.setGeoPoint(new GeoPointDto()
						.setLat(coordinates.getLat())
						.setLon(coordinates.getLon()));
	}

	private Boolean isPermanent(ResolvedSearchFilterDto searchFilter) {
		final ContractType contractType = searchFilter.getContractType();
		if (contractType == null) {
			return null;
		}
		switch (contractType) {
			case PERMANENT:
				return true;
			case TEMPORARY:
				return false;
			default:
				return null;
		}
	}

	private ProfessionCode[] extractProfessionCodes(ResolvedSearchProfileDto resolvedSearchProfileDto) {
		if (resolvedSearchProfileDto.getSearchFilter().getOccupations().isEmpty()) {
			return null;
		}
		final List<ResolvedOccupationFilterDto> occupations = filterMappings(resolvedSearchProfileDto.getSearchFilter().getOccupations());
		ArrayList<ProfessionCode> professionCodes = new ArrayList<>();
		for (ResolvedOccupationFilterDto resolvedOccupationFilterDto : occupations) {
			final Map<ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType, String> mappings = resolvedOccupationFilterDto.getMappings();
			if (mappings == null || mappings.isEmpty()) {
				continue;
			}
			mappings.forEach((key, value) -> professionCodes.add(new ProfessionCode(ProfessionCodeType.fromString(key.toString()), value)));
		}
		return professionCodes.toArray(new ProfessionCode[0]);
	}

	private List<ResolvedOccupationFilterDto> filterMappings(List<ResolvedOccupationFilterDto> occupations) {
		return occupations.stream()
				.filter(i -> i.getType().equals(ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType.X28))
				.peek(filteredOccupations -> {
					final Map<ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType, String> filteredMappings = filteredOccupations.getMappings();
					filteredMappings.remove(ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType.CHISCO3);
					filteredMappings.remove(ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType.CHISCO5);
					filteredMappings.remove(ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType.BFS);
				})
				.collect(Collectors.toList());
	}

	private static String[] extractKeywords(ResolvedSearchProfileDto resolvedSearchProfileDto) {
		final Set<String> keywords = resolvedSearchProfileDto.getSearchFilter().getKeywords();
		String allKeywords = String.join(" ", keywords);
		if (keywords.isEmpty()) {
			return null;
		}
		if (isNotBlank(allKeywords)) {
			return keywords.toArray(new String[0]);
		}
		return null;
	}

	private static String[] extractCantonCodes(ResolvedSearchProfileDto resolvedSearchProfileDto) {
		if (resolvedSearchProfileDto.getSearchFilter().getCantons().isEmpty()) {
			return null;
		}
		return resolvedSearchProfileDto.getSearchFilter().getLocations().stream()
				.map(LocationDto::getCantonCode)
				.toArray(String[]::new);
	}

	private static String[] extractCommunalCodes(ResolvedSearchProfileDto resolvedSearchProfileDto) {
		if (resolvedSearchProfileDto.getSearchFilter().getLocations().isEmpty()) {
			return null;
		}
		return resolvedSearchProfileDto.getSearchFilter().getLocations().stream()
				.map(LocationDto::getCommunalCode)
				.toArray(String[]::new);
	}
}
