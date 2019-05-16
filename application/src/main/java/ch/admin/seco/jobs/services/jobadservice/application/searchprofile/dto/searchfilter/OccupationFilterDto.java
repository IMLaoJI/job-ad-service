package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.OccupationFilter;

import java.util.List;
import java.util.stream.Collectors;

public class OccupationFilterDto {

	private String labelId;

	public OccupationFilterDto() {
	}

	public String getLabelId() {
		return labelId;
	}

	public OccupationFilterDto setLabelId(String labelId) {
		this.labelId = labelId;
		return this;
	}

	public static OccupationFilterDto toDto(OccupationFilter occupationFilter) {
		if (occupationFilter == null) {
			return null;
		}
		return new OccupationFilterDto()
				.setLabelId(occupationFilter.getLabelId());
	}

	public static List<OccupationFilterDto> toDto(List<OccupationFilter> occupationFilters) {
		return occupationFilters.stream().map(OccupationFilterDto::toDto).collect(Collectors.toList());
	}
}
