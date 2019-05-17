package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.ContractType;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.SearchFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.Sort;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class SearchFilterDto {

	private Sort sort;

	private ContractType contractType;

	private List<String> keywords = new ArrayList<>();

	private Integer workloadPercentageMin;

	private Integer workloadPercentageMax;

	private String companyName;

	private Integer onlineSince;

	private Boolean displayRestricted;

	private Boolean euresDisplay;

	@Valid
	private List<OccupationFilterDto> occupationFilters = new ArrayList<>();

	@Valid
	private List<LocalityFilterDto> localityFilters = new ArrayList<>();

	@Valid
	private List<CantonFilterDto> cantonFilters = new ArrayList<>();

	private Integer distance;

	public SearchFilterDto() {
	}

	public Sort getSort() {
		return sort;
	}

	public SearchFilterDto setSort(Sort sort) {
		this.sort = sort;
		return this;
	}

	public ContractType getContractType() {
		return contractType;
	}

	public SearchFilterDto setContractType(ContractType contractType) {
		this.contractType = contractType;
		return this;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public SearchFilterDto setKeywords(List<String> keywords) {
		this.keywords = keywords;
		return this;
	}

	public Integer getWorkloadPercentageMin() {
		return workloadPercentageMin;
	}

	public SearchFilterDto setWorkloadPercentageMin(Integer workloadPercentageMin) {
		this.workloadPercentageMin = workloadPercentageMin;
		return this;
	}

	public Integer getWorkloadPercentageMax() {
		return workloadPercentageMax;
	}

	public SearchFilterDto setWorkloadPercentageMax(Integer workloadPercentageMax) {
		this.workloadPercentageMax = workloadPercentageMax;
		return this;
	}

	public String getCompanyName() {
		return companyName;
	}

	public SearchFilterDto setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}

	public Integer getOnlineSince() {
		return onlineSince;
	}

	public SearchFilterDto setOnlineSince(Integer onlineSince) {
		this.onlineSince = onlineSince;
		return this;
	}

	public Boolean getDisplayRestricted() {
		return displayRestricted;
	}

	public SearchFilterDto setDisplayRestricted(Boolean displayRestricted) {
		this.displayRestricted = displayRestricted;
		return this;
	}

	public Boolean getEuresDisplay() {
		return euresDisplay;
	}

	public SearchFilterDto setEuresDisplay(Boolean euresDisplay) {
		this.euresDisplay = euresDisplay;
		return this;
	}

	public List<OccupationFilterDto> getOccupationFilters() {
		return occupationFilters;
	}

	public SearchFilterDto setOccupationFilters(List<OccupationFilterDto> occupationFilters) {
		this.occupationFilters = occupationFilters;
		return this;
	}

	public List<LocalityFilterDto> getLocalityFilters() {
		return localityFilters;
	}

	public SearchFilterDto setLocalityFilters(List<LocalityFilterDto> localityFilters) {
		this.localityFilters = localityFilters;
		return this;
	}

	public List<CantonFilterDto> getCantonFilters() {
		return cantonFilters;
	}

	public SearchFilterDto setCantonFilters(List<CantonFilterDto> cantonFilters) {
		this.cantonFilters = cantonFilters;
		return this;
	}

	public Integer getDistance() {
		return distance;
	}

	public SearchFilterDto setDistance(Integer distance) {
		this.distance = distance;
		return this;
	}

	public static SearchFilterDto toDto(SearchFilter searchFilter) {
		return new SearchFilterDto()
				.setSort(searchFilter.getSort())
				.setContractType(searchFilter.getContractType())
				.setKeywords(new ArrayList<>(searchFilter.getKeywords()))
				.setWorkloadPercentageMin(searchFilter.getWorkloadPercentageMin())
				.setWorkloadPercentageMax(searchFilter.getWorkloadPercentageMax())
				.setCompanyName(searchFilter.getCompanyName())
				.setOnlineSince(searchFilter.getOnlineSince())
				.setDisplayRestricted(searchFilter.getDisplayRestricted())
				.setEuresDisplay(searchFilter.getEuresDisplay())
				.setOccupationFilters(OccupationFilterDto.toDto(searchFilter.getOccupationFilters()))
				.setLocalityFilters(LocalityFilterDto.toDto(searchFilter.getLocalityFilters()))
				.setCantonFilters(CantonFilterDto.toDto(searchFilter.getCantonFilters()))
				.setDistance(searchFilter.getDistance());
	}
}
