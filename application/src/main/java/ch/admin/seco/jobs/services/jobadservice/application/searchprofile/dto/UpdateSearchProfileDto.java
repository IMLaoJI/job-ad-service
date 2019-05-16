package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.SearchFilterDto;

public class UpdateSearchProfileDto {

    @NotBlank
    @Size(max = 50)
    private String name;

    @Valid
    @NotNull
    private SearchFilterDto searchFilter;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SearchFilterDto getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(SearchFilterDto searchFilter) {
        this.searchFilter = searchFilter;
    }
}
