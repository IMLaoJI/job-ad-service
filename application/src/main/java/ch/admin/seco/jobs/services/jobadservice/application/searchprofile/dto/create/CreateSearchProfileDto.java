package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.create;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.SearchFilterDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateSearchProfileDto {

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    private String ownerUserId;

    @NotNull
    private SearchFilterDto searchFilter;

    public CreateSearchProfileDto(String name, String ownerUserId, SearchFilterDto searchFilter) {
        this.name = name;
        this.ownerUserId = ownerUserId;
        this.searchFilter = searchFilter;
    }

    public CreateSearchProfileDto() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public SearchFilterDto getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(SearchFilterDto searchFilter) {
        this.searchFilter = searchFilter;
    }
}
