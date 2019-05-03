package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.SearchProfileResult;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SearchProfileResultDto {

    private String id;

    private LocalDateTime updatedTime;

    private String name;

    private String userOwnerId;

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

    public static SearchProfileResultDto toDto(SearchProfileResult searchProfileResult) {
        if (searchProfileResult == null) {
            return null;
        }
        return new SearchProfileResultDto()
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
