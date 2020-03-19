package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.users;

public class UserEventDto {

    private String userInfoId;

    private String userInfoRole;

    public String getUserInfoId() {
        return userInfoId;
    }

    public UserEventDto setUserInfoId(String userInfoId) {
        this.userInfoId = userInfoId;
        return this;
    }

    public String getUserInfoRole() {
        return userInfoRole;
    }

    public UserEventDto setUserInfoRole(String userInfoRole) {
        this.userInfoRole = userInfoRole;
        return this;
    }

}
