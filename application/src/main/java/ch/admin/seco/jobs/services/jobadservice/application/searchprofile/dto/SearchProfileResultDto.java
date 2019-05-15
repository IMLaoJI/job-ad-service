package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.LocationDto;
import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.OccupationSuggestionDto;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileResult;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SearchProfileResultDto {

    private String id;

    @NotNull
    private LocalDateTime createdTime;

    @NotNull
    private LocalDateTime updatedTime;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    private String userOwnerId;

    List<LocationDto> locations;

    List<OccupationSuggestionDto> occupationSuggestions;

    public SearchProfileResultDto() {}

    public String getId() {
        return id;
    }

    public SearchProfileResultDto setId(String id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public SearchProfileResultDto setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    public String getName() {
        return name;
    }

    public SearchProfileResultDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getUserOwnerId() {
        return userOwnerId;
    }

    public SearchProfileResultDto setUserOwnerId(String userOwnerId) {
        this.userOwnerId = userOwnerId;
        return this;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public SearchProfileResultDto setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public List<LocationDto> getLocations() {
        return locations;
    }

    public SearchProfileResultDto setLocations(List<LocationDto> locations) {
        this.locations = locations;
        return this;
    }

    public List<OccupationSuggestionDto> getOccupationSuggestions() {
        return occupationSuggestions;
    }

    public SearchProfileResultDto setOccupationSuggestions(List<OccupationSuggestionDto> occupationSuggestions) {
        this.occupationSuggestions = occupationSuggestions;
        return this;
    }

    public static SearchProfileResultDto toDto(SearchProfileResult searchProfileResult) {
        if (searchProfileResult == null) {
            return null;
        }
        return new SearchProfileResultDto()
                .setCreatedTime(searchProfileResult.getCreatedTime())
                .setId(searchProfileResult.getId())
                .setUpdatedTime(searchProfileResult.getUpdatedTime())
                .setName(searchProfileResult.getName())
                .setUserOwnerId(searchProfileResult.getUserOwnerId());
    }

    public static List<SearchProfileResultDto> toDto(List<SearchProfileResult> searchProfileResults) {
        if (CollectionUtils.isEmpty(searchProfileResults)) {
            return null;
        }
        return searchProfileResults.stream().map(SearchProfileResultDto::toDto).collect(Collectors.toList());
    }
}
