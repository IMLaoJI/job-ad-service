package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read.fixtures;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LocationDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.ResolvedSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.ResolvedOccupationFilterDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.ResolvedSearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.GeoPoint;
import ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResolvedSearchProfileDtoFixture {


	public static ResolvedSearchProfileDto createResolvedSearchProfileDtoFixture() {
		return new ResolvedSearchProfileDto()
				.setSearchFilter(createResolvedSearchFilterDtoWithoutOccupationMappings());
	}

	public static ResolvedSearchProfileDto createResolvedSearchProfileDtoFixtureWithOccupationCodes() {
		return new ResolvedSearchProfileDto()
				.setSearchFilter(createResolvedSearchFilterDtoWithOccupationMappings());
	}

	private static Map<ProfessionCodeType, String> occupationCodesFixture() {
		Map<ProfessionCodeType, String> professionCodeMap = new HashMap<>();
		professionCodeMap.put(ProfessionCodeType.X28, "11000820");
		professionCodeMap.put(ProfessionCodeType.AVAM, "101167");
		professionCodeMap.put(ProfessionCodeType.BFS, "29112197");
		professionCodeMap.put(ProfessionCodeType.CHISCO3, "214");
		professionCodeMap.put(ProfessionCodeType.CHISCO5, "21400");
		return professionCodeMap;
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

	private static ResolvedSearchFilterDto createResolvedSearchFilterDtoWithOccupationMappings() {
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
								.setCode("12345").setType(ProfessionCodeType.X28)
								.setMappings(occupationCodesFixture())
				));
	}
}
