package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LocationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.ContractType;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.Sort;

import java.util.List;
import java.util.Set;

public class ResolvedSearchFilterDto {

	private Sort sort;

	private ContractType contractType;

	private Set<String> keywords;

	private Integer workloadPercentageMin;

	private Integer workloadPercentageMax;

	private String companyName;

	private Integer onlineSince;

	private Boolean displayRestricted;

	private Boolean euresDisplay;

	private List<CantonFilterDto> cantons;

	private Integer distance;

	private List<LocationDto> locations;

	private List<ResolvedOccupationFilterDto> occupations;

	public Sort getSort() {
		return sort;
	}

	public ResolvedSearchFilterDto setSort(Sort sort) {
		this.sort = sort;
		return this;
	}

	public ContractType getContractType() {
		return contractType;
	}

	public ResolvedSearchFilterDto setContractType(ContractType contractType) {
		this.contractType = contractType;
		return this;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public ResolvedSearchFilterDto setKeywords(Set<String> keywords) {
		this.keywords = keywords;
		return this;
	}

	public Integer getWorkloadPercentageMin() {
		return workloadPercentageMin;
	}

	public ResolvedSearchFilterDto setWorkloadPercentageMin(Integer workloadPercentageMin) {
		this.workloadPercentageMin = workloadPercentageMin;
		return this;
	}

	public Integer getWorkloadPercentageMax() {
		return workloadPercentageMax;
	}

	public ResolvedSearchFilterDto setWorkloadPercentageMax(Integer workloadPercentageMax) {
		this.workloadPercentageMax = workloadPercentageMax;
		return this;
	}

	public String getCompanyName() {
		return companyName;
	}

	public ResolvedSearchFilterDto setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}

	public Integer getOnlineSince() {
		return onlineSince;
	}

	public ResolvedSearchFilterDto setOnlineSince(Integer onlineSince) {
		this.onlineSince = onlineSince;
		return this;
	}

	public Boolean getDisplayRestricted() {
		return displayRestricted;
	}

	public ResolvedSearchFilterDto setDisplayRestricted(Boolean displayRestricted) {
		this.displayRestricted = displayRestricted;
		return this;
	}

	public Boolean getEuresDisplay() {
		return euresDisplay;
	}

	public ResolvedSearchFilterDto setEuresDisplay(Boolean euresDisplay) {
		this.euresDisplay = euresDisplay;
		return this;
	}

	public List<CantonFilterDto> getCantons() {
		return cantons;
	}

	public ResolvedSearchFilterDto setCantons(List<CantonFilterDto> cantons) {
		this.cantons = cantons;
		return this;
	}

	public Integer getDistance() {
		return distance;
	}

	public ResolvedSearchFilterDto setDistance(Integer distance) {
		this.distance = distance;
		return this;
	}

	public List<LocationDto> getLocations() {
		return locations;
	}

	public ResolvedSearchFilterDto setLocations(List<LocationDto> locations) {
		this.locations = locations;
		return this;
	}

	public List<ResolvedOccupationFilterDto> getOccupations() {
		return occupations;
	}

	public ResolvedSearchFilterDto setOccupations(List<ResolvedOccupationFilterDto> occupations) {
		this.occupations = occupations;
		return this;
	}
}
