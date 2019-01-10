package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.read.jobadvertisement;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class ManagedJobAdSearchRequest {

    @Min(7)
    @Max(365)
    private Integer onlineSinceDays;

    @NotBlank
    private String ownerUserId;

    private String[] keywords;

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public Integer getOnlineSinceDays() {
        return onlineSinceDays;
    }

    public ManagedJobAdSearchRequest setOnlineSinceDays(Integer onlineSinceDays) {
        this.onlineSinceDays = onlineSinceDays;
        return this;
    }

    public ManagedJobAdSearchRequest setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public ManagedJobAdSearchRequest setKeywords(String[] keywords) {
        this.keywords = keywords;
        return this;
    }
}
