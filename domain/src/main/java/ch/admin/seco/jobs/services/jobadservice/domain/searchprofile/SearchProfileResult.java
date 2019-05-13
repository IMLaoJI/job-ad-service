package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class SearchProfileResult {

    private String id;

    @NotNull
    private LocalDateTime updatedTime;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
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
