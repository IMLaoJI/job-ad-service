package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.*;

@Embeddable
@Access(AccessType.FIELD)
public class SearchFilter {

	@Enumerated(EnumType.STRING)
	@Column(name = "SEARCH_FILTER_SORT")
	private Sort sort;

	@Enumerated(EnumType.STRING)
	@Column(name = "SEARCH_FILTER_CONTRACT_TYPE")
	private ContractType contractType;

	@Convert(converter = StringSetConverter.class)
	@Column(name = "SEARCH_FILTER_KEYWORDS")
	@Size(max = 1000)
	private Set<String> keywords = new LinkedHashSet<>();

	@Column(name = "SEARCH_FILTER_WORKLOAD_PERCENTAGE_MIN")
	@Min(10)
	@Max(100)
	private Integer workloadPercentageMin;

	@Column(name = "SEARCH_FILTER_WORKLOAD_PERCENTAGE_MAX")
	@Min(10)
	@Max(100)
	private Integer workloadPercentageMax;

	@Column(name = "SEARCH_FILTER_COMPANY_NAME")
	private String companyName;

	@Column(name = "SEARCH_FILTER_ONLINE_SINCE")
	@Min(0)
	@Max(60)
	private Integer onlineSince;

	@Column(name = "SEARCH_FILTER_DISPLAY_RESTRICTED")
	private Boolean displayRestricted;

	@Column(name = "SEARCH_FILTER_EURES_DISPLAY")
	private Boolean euresDisplay;

	@OrderColumn(name = "OCCUPATION_ORDER")
	@ElementCollection
	@CollectionTable(name = "SEARCH_PROFILE_OCCUPATION_FILTER", joinColumns = @JoinColumn(name = "SEARCH_PROFILE_ID"))
	@Valid
	private List<OccupationFilter> occupationFilters = new ArrayList<>();

	@OrderColumn(name = "LOCALITY_ORDER")
	@ElementCollection
	@CollectionTable(name = "SEARCH_PROFILE_LOCALITY_FILTER", joinColumns = @JoinColumn(name = "SEARCH_PROFILE_ID"))
	@Valid
	private List<LocalityFilter> localityFilters = new ArrayList<>();

	@OrderColumn(name = "CANTON_ORDER")
	@ElementCollection
	@CollectionTable(name = "SEARCH_PROFILE_CANTON_FILTER", joinColumns = @JoinColumn(name = "SEARCH_PROFILE_ID"))
	@Valid
	private List<CantonFilter> cantonFilters = new ArrayList<>();

	@Column(name = "SEARCH_FILTER_RADIUS_DISTANCE")
	private Integer distance;

	private SearchFilter(Builder builder) {
		this.sort = builder.sort;
		this.contractType = builder.contractType;
		this.keywords.addAll(builder.keywords);
		this.workloadPercentageMin = builder.workloadPercentageMin;
		this.workloadPercentageMax = builder.workloadPercentageMax;
		this.companyName = builder.companyName;
		this.onlineSince = builder.onlineSince;
		this.displayRestricted = builder.displayRestricted;
		this.euresDisplay = builder.euresDisplay;
		this.occupationFilters.addAll(builder.occupationFilters);
		this.localityFilters.addAll(builder.localityFilters);
		this.cantonFilters.addAll(builder.cantonFilters);
		this.distance = builder.distance;
	}

	private SearchFilter() {
		// FOR JPA
	}

	public static Builder builder() {
		return new Builder();
	}

	public Sort getSort() {
		return sort;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public List<OccupationFilter> getOccupationFilters() {
		return Collections.unmodifiableList(this.occupationFilters);
	}

	public List<LocalityFilter> getLocalityFilters() {
		return Collections.unmodifiableList(this.localityFilters);
	}

	public List<CantonFilter> getCantonFilters() {
		return Collections.unmodifiableList(this.cantonFilters);
	}

	public Integer getDistance() {
		return distance;
	}

	public ContractType getContractType() {
		return contractType;
	}

	public Integer getWorkloadPercentageMin() {
		return workloadPercentageMin;
	}

	public Integer getWorkloadPercentageMax() {
		return workloadPercentageMax;
	}

	public String getCompanyName() {
		return companyName;
	}

	public Integer getOnlineSince() {
		return onlineSince;
	}

	public Boolean getDisplayRestricted() {
		return displayRestricted;
	}

	public Boolean getEuresDisplay() {
		return euresDisplay;
	}

	public static class Builder {

		private Sort sort;

		private ContractType contractType;

		private List<String> keywords;

		private Integer workloadPercentageMin;

		private Integer workloadPercentageMax;

		private String companyName;

		private Integer onlineSince;

		private Boolean displayRestricted;

		private Boolean euresDisplay;

		private List<OccupationFilter> occupationFilters;

		private List<CantonFilter> cantonFilters;

		private List<LocalityFilter> localityFilters;

		private Integer distance;

		public Builder setSort(Sort sort) {
			this.sort = sort;
			return this;
		}

		public Builder setContractType(ContractType contractType) {
			this.contractType = contractType;
			return this;
		}

		public Builder setKeywords(List<String> keywords) {
			this.keywords = keywords;
			return this;
		}

		public Builder setWorkloadPercentageMin(Integer workloadPercentageMin) {
			this.workloadPercentageMin = workloadPercentageMin;
			return this;
		}

		public Builder setWorkloadPercentageMax(Integer workloadPercentageMax) {
			this.workloadPercentageMax = workloadPercentageMax;
			return this;
		}

		public Builder setCompanyName(String companyName) {
			this.companyName = companyName;
			return this;
		}

		public Builder setOnlineSince(Integer onlineSince) {
			this.onlineSince = onlineSince;
			return this;
		}

		public Builder setDisplayRestricted(Boolean displayRestricted) {
			this.displayRestricted = displayRestricted;
			return this;
		}

		public Builder setEuresDisplay(Boolean euresDisplay) {
			this.euresDisplay = euresDisplay;
			return this;
		}

		public Builder setOccupationFilters(List<OccupationFilter> occupationFilters) {
			this.occupationFilters = occupationFilters;
			return this;
		}

		public Builder setCantonFilters(List<CantonFilter> cantonFilters) {
			this.cantonFilters = cantonFilters;
			return this;
		}

		public Builder setLocalityFilters(List<LocalityFilter> localityFilters) {
			this.localityFilters = localityFilters;
			return this;
		}

		public Builder setDistance(Integer distance) {
			this.distance = distance;
			return this;
		}

		public SearchFilter build() {
			return new SearchFilter(this);
		}
	}
}
