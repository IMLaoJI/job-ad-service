package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Embeddable
@Access(AccessType.FIELD)
public class SearchFilter {

    @Enumerated(EnumType.STRING)
    private Sort sort;

    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    @Convert(converter = StringSetConverter.class)
    private Set<String> keywords;

    private Integer workloadPercentageMin;

    private Integer workloadPercentageMax;

    private String companyName;

    private Integer onlineSince;

    private Boolean displayRestricted;

    private Boolean euresDisplay;

    @OrderColumn(name = "occupation_order")
    @ElementCollection
    private List<OccupationFilter> occupationFilters = new ArrayList<>();

    @OrderColumn(name = "locality_order")
    @ElementCollection
    private List<LocalityFilter> localityFilters = new ArrayList<>();

    @OrderColumn(name = "canton_order")
    @ElementCollection
    private List<CantonFilter> cantonFilters = new ArrayList<>();

    @OrderColumn(name = "radio_order")
    @ElementCollection
    private List<RadiusSearchFilter> radiusSearchFilters = new ArrayList<>();

    private SearchFilter(Builder builder) {
        this.sort = builder.sort;
        this.contractType = builder.contractType;
        this.keywords = builder.keywords;
        this.workloadPercentageMin = builder.workloadPercentageMin;
        this.workloadPercentageMax = builder.workloadPercentageMax;
        this.companyName = builder.companyName;
        this.onlineSince = builder.onlineSince;
        this.displayRestricted = builder.displayRestricted;
        this.euresDisplay = builder.euresDisplay;
        this.occupationFilters.addAll(builder.occupationFilters);
        this.localityFilters.addAll(builder.localityFilters);
        this.cantonFilters.addAll(builder.cantonFilters);
        this.radiusSearchFilters.addAll(builder.radiusSearchFilters);
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

        private Set<String> keywords;

        private Integer workloadPercentageMin;

        private Integer workloadPercentageMax;

        private String companyName;

        private Integer onlineSince;

        private Boolean displayRestricted;

        private Boolean euresDisplay;

        private List<OccupationFilter> occupationFilters;

        private List<CantonFilter> cantonFilters;

        private List<LocalityFilter> localityFilters;

        private List<RadiusSearchFilter> radiusSearchFilters;

        public Builder setSort(Sort sort) {
            this.sort = sort;
            return this;
        }

        public Builder setContractType(ContractType contractType) {
            this.contractType = contractType;
            return this;
        }

        public Builder setKeywords(Set<String> keywords) {
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

        public Builder setRadiusSearchFilters(List<RadiusSearchFilter> radiusSearchFilters) {
            this.radiusSearchFilters = radiusSearchFilters;
            return this;
        }

        public SearchFilter build() {
            return new SearchFilter(this);
        }
    }
}
