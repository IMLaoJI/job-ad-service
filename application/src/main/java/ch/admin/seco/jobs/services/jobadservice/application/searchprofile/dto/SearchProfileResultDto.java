package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.Interval;

import java.time.LocalDateTime;

public class SearchProfileResultDto {

    private String id;

    private LocalDateTime createdTime;

    private String name;

    private String ownerUserId;

    private Interval interval;

    public String getId() {
        return id;
    }

    public SearchProfileResultDto setId(String id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public SearchProfileResultDto setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public String getName() {
        return name;
    }

    public SearchProfileResultDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public SearchProfileResultDto setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public Interval getInterval() {
        return interval;
    }

    public SearchProfileResultDto setInterval(Interval interval) {
        this.interval = interval;
        return this;
    }
}
