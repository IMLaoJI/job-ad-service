package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter;

import java.util.List;
import java.util.stream.Collectors;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.CantonFilter;

public class CantonFilterDto {

	private String name;

	private String code;

	public CantonFilterDto() {
	}

	public String getName() {
		return name;
	}

	public CantonFilterDto setName(String name) {
		this.name = name;
		return this;
	}

	public String getCode() {
		return code;
	}

	public CantonFilterDto setCode(String code) {
		this.code = code;
		return this;
	}

	public static CantonFilterDto toDto(CantonFilter cantonFilter) {
		if (cantonFilter == null) {
			return null;
		}
		return new CantonFilterDto()
				.setName(cantonFilter.getName())
				.setCode(cantonFilter.getCode());
	}

	public static List<CantonFilterDto> toDto(List<CantonFilter> cantonFilters) {
		return cantonFilters.stream()
				.map(CantonFilterDto::toDto)
				.collect(Collectors.toList());
	}
}
