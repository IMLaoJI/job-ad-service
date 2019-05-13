package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.update;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.SearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileId;

import javax.validation.constraints.NotBlank;

public class UpdateSearchProfileDto {

    private SearchProfileId id;

    private String name;

    private SearchFilterDto searchFilter;

    @NotBlank
    private String ownerUserId;

    public UpdateSearchProfileDto(SearchProfileId id, String name, SearchFilterDto searchFilter, String ownerUserId) {
        this.id = id;
        this.name = name;
        this.searchFilter = searchFilter;
        this.ownerUserId = ownerUserId;
    }

    public SearchProfileId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public SearchFilterDto getSearchFilter() {
        return searchFilter;
    }
}
