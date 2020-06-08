package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter.ResolvedSearchFilterDto;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.Interval;

import java.time.LocalDateTime;

public class ResolvedSearchProfileDto {

    private String id;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String name;

    private String ownerUserId;

    private Interval interval;

    private ResolvedSearchFilterDto searchFilter;

    public String getId() {
        return id;
    }

    public ResolvedSearchProfileDto setId(String id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public ResolvedSearchProfileDto setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public ResolvedSearchProfileDto setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    public String getName() {
        return name;
    }

    public ResolvedSearchProfileDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public ResolvedSearchProfileDto setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public ResolvedSearchFilterDto getSearchFilter() {
        return searchFilter;
    }

    public ResolvedSearchProfileDto setSearchFilter(ResolvedSearchFilterDto searchFilter) {
        this.searchFilter = searchFilter;
        return this;
    }

    public Interval getInterval() {
        return interval;
    }

    public ResolvedSearchProfileDto setInterval(Interval interval) {
        this.interval = interval;
        return this;
    }
}
