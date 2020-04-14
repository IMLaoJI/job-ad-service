package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read.fixtures;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LocationDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.ResolvedSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.ResolvedOccupationFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.ResolvedSearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.GeoPoint;
import ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType;

import java.util.Arrays;
import java.util.Collections;

public class ResolvedSearchProfileDtoFixture {


	public static ResolvedSearchProfileDto createResolvedSearchProfileDtoFixture() {
		return new ResolvedSearchProfileDto()
				.setSearchFilter(createResolvedSearchFilterDtoWithoutOccupationMappings());

	}

	private static ResolvedSearchFilterDto createResolvedSearchFilterDtoWithoutOccupationMappings() {
		return new ResolvedSearchFilterDto()
				.setCantons(Collections.emptyList())
				.setKeywords(Collections.emptySet())
				.setLocations(Arrays.asList(
						new LocationDto()
								.setCity("Bern")
								.setCoordinates(new GeoPoint(7.4, 46.9)), new LocationDto().setCity("Zürich")
				))
				.setOccupations(Collections.singletonList(
						new ResolvedOccupationFilterDto()
								.setCode("12345").setType(ProfessionCodeType.AVAM)
								.setMappings(null)
				));
	}
}
