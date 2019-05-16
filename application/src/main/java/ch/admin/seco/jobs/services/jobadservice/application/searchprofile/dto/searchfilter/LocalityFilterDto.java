package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter;

import java.util.List;
import java.util.stream.Collectors;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.LocalityFilter;

public class LocalityFilterDto {

	private String localityId;

	public LocalityFilterDto() {
	}

	public String getLocalityId() {
		return localityId;
	}

	public LocalityFilterDto setLocalityId(String localityId) {
		this.localityId = localityId;
		return this;
	}

	public static LocalityFilterDto toDto(LocalityFilter localityFilter) {
		if (localityFilter == null) {
			return null;
		}
		return new LocalityFilterDto()
				.setLocalityId(localityFilter.getLocalityId());
	}

	public static List<LocalityFilterDto> toDto(List<LocalityFilter> localityFilters) {
		return localityFilters.stream()
				.map(LocalityFilterDto::toDto)
				.collect(Collectors.toList());
	}
}
