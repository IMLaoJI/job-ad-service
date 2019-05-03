package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.update;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.SearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;

public class UpdateSearchProfileDto {

    private SearchProfileId id;

    private String name;

    private SearchFilterDto searchFilter;

    public UpdateSearchProfileDto(SearchProfileId id, String name, SearchFilterDto searchFilter) {
        this.id = id;
        this.name = name;
        this.searchFilter = searchFilter;
    }

    public SearchProfileId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SearchFilterDto getSearchFilter() {
        return searchFilter;
    }
}
