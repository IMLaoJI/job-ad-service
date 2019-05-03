package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile;

import java.time.LocalDateTime;

public class SearchProfileResult {

    private String id;

    private LocalDateTime updatedTime;

    private String name;

    private String userOwnerId;

    public SearchProfileResult() {}

    public SearchProfileResult(String id, LocalDateTime updatedTime, String name, String userOwnerId) {
        this.id = id;
        this.updatedTime = updatedTime;
        this.name = name;
        this.userOwnerId = userOwnerId;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public String getName() {
        return name;
    }

    public String getUserOwnerId() {
        return userOwnerId;
    }
}
