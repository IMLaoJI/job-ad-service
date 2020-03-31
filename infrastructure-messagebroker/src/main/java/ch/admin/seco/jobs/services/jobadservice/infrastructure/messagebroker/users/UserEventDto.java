package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.users;

public class UserEventDto {

    private String userInfoId;

    private String userInfoRole;

    private String eventType;

    private String personNumber;

    private String firstName;

    private String lastName;

    private String email;

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

    public String getEventType() {
        return eventType;
    }

    public UserEventDto setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public String getPersonNumber() {
        return personNumber;
    }

    public UserEventDto setPersonNumber(String personNumber) {
        this.personNumber = personNumber;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserEventDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserEventDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserEventDto setEmail(String email) {
        this.email = email;
        return this;
    }
}
