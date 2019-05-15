package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.SearchFilterDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdateSearchProfileDto {

    private String id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @Valid
    @NotNull
    private SearchFilterDto searchFilter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
