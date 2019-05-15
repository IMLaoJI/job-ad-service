package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.SearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class SearchProfileDto {

    private String id;

    @NotNull
    private LocalDateTime createdTime;

    @NotNull
    private LocalDateTime updatedTime;

    @NotBlank
    @Size(max = 50)
    private String name;

    private String ownerUserId;

    @Valid
    private SearchFilterDto searchFilter;

    public SearchProfileDto() {}

    public String getId() {
        return id;
    }

    public SearchProfileDto setId(String id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public SearchProfileDto setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public SearchProfileDto setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    public String getName() {
        return name;
    }

    public SearchProfileDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public SearchProfileDto setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public SearchFilterDto getSearchFilter() {
        return searchFilter;
    }

    public SearchProfileDto setSearchFilter(SearchFilterDto searchFilter) {
        this.searchFilter = searchFilter;
        return this;
    }

    public static SearchProfileDto toDto(SearchProfile searchProfile) {
        return new SearchProfileDto()
                .setId(searchProfile.getId().getValue())
                .setCreatedTime(searchProfile.getCreatedTime())
                .setUpdatedTime(searchProfile.getUpdatedTime())
                .setOwnerUserId(searchProfile.getOwnerUserId())
                .setName(searchProfile.getName())
                .setSearchFilter(SearchFilterDto.toDto(searchProfile.getSearchFilter()));
    }

}
