package ch.admin.seco.jobs.services.jobadservice.domain.apiuser;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.Aggregate;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventPublisher;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.apiuser.events.ApiUserUpdatedDetailsEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.apiuser.events.ApiUserUpdatedPasswordEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.apiuser.events.ApiUserUpdatedStatusEvent;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class ApiUser implements Aggregate<ApiUser, ApiUserId> {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "ID"))
    @Valid
    private ApiUserId id;

    @NotEmpty
    @Column(unique = true)
    private String username;

    @NotEmpty
    private String password;

    @NotEmpty
    private String companyName;

    @NotEmpty
    private String companyEmail;

    @NotEmpty
    private String technicalContactName;

    @NotEmpty
    private String technicalContactEmail;

    private boolean active;

    @NotNull
    private LocalDateTime createDate;

    private LocalDateTime lastAccessDate;

    private int loginFailureCount;

    protected ApiUser() {
        // For reflection libs
    }

    public ApiUser(Builder builder) {
        this.id = Condition.notNull(builder.id);
        this.username = Condition.notBlank(builder.username);
        this.password = Condition.notBlank(builder.password);
        this.companyName = Condition.notBlank(builder.companyName);
        this.companyEmail = Condition.notBlank(builder.companyEmail);
        this.technicalContactName = Condition.notBlank(builder.technicalContactName);
        this.technicalContactEmail = Condition.notBlank(builder.technicalContactEmail);
        this.active = builder.active;
        this.createDate = TimeMachine.now();
        this.lastAccessDate = TimeMachine.now();
        this.loginFailureCount = 0;
    }

    public ApiUserId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public String getTechnicalContactName() {
        return technicalContactName;
    }

    public String getTechnicalContactEmail() {
        return technicalContactEmail;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getLastAccessDate() {
        return lastAccessDate;
    }

    public int getLoginFailureCount() {
        return loginFailureCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiUser that = (ApiUser) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void updateDetails(Builder builder) {
        this.username = Condition.notBlank(builder.username);
        this.companyName = Condition.notBlank(builder.companyName);
        this.companyEmail = Condition.notBlank(builder.companyEmail);
        this.technicalContactName = Condition.notBlank(builder.technicalContactName);
        this.technicalContactEmail = Condition.notBlank(builder.technicalContactEmail);
        DomainEventPublisher.publish(new ApiUserUpdatedDetailsEvent(this));
    }

    public void changeStatus(boolean active) {
        this.active = active;
        if (active) {
            resetCountLoginFailure();
        }
        DomainEventPublisher.publish(new ApiUserUpdatedStatusEvent(this));
    }

    public void changePassword(String password) {
        this.password = Condition.notBlank(password);
        DomainEventPublisher.publish(new ApiUserUpdatedPasswordEvent(this));
    }

    public void touch() {
        this.lastAccessDate = TimeMachine.now();
    }

    public void resetCountLoginFailure() {
        this.loginFailureCount = 0;
        touch();
    }

    public void incrementCountLoginFailure() {
        this.loginFailureCount++;
        touch();
    }

    public static final class Builder {
        private ApiUserId id;
        private String username;
        private String password;
        private String companyName;
        private String companyEmail;
        private String technicalContactName;
        private String technicalContactEmail;
        private boolean active;

        public Builder() {
        }

        public ApiUser build() {
            return new ApiUser(this);
        }

        public Builder setId(ApiUserId id) {
            this.id = id;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder setCompanyEmail(String companyEmail) {
            this.companyEmail = companyEmail;
            return this;
        }

        public Builder setTechnicalContactName(String technicalContactName) {
            this.technicalContactName = technicalContactName;
            return this;
        }

        public Builder setTechnicalContactEmail(String technicalContactEmail) {
            this.technicalContactEmail = technicalContactEmail;
            return this;
        }

        public Builder setActive(boolean active) {
            this.active = active;
            return this;
        }

    }
}
