package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Owner;

public class OwnerDto {

    private String userId;

    private String userDisplayName;

    private String companyId;

    private String accessToken;

    public OwnerDto() {
        // For reflection libs
    }

    public String getUserId() {
        return userId;
    }

    public OwnerDto setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public OwnerDto setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
        return this;
    }

    public String getCompanyId() {
        return companyId;
    }

    public OwnerDto setCompanyId(String companyId) {
        this.companyId = companyId;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public OwnerDto setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public static OwnerDto toDto(Owner owner) {
        if (owner == null) {
            return null;
        }
        return new OwnerDto()
                .setUserId(owner.getUserId())
                .setUserDisplayName(owner.getUserDisplayName())
                .setCompanyId(owner.getCompanyId())
                .setAccessToken(owner.getAccessToken());
    }
}
