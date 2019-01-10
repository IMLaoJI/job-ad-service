package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.read.jobadvertisement;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class ManagedJobAdSearchRequest {

    @Min(7)
    @Max(365)
    private Integer onlineSinceDays;

    private String ownerUserId;

    @NotBlank
    private String companyId;

    private String[] keywords;

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public ManagedJobAdSearchRequest setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public String getCompanyId() {
        return companyId;
    }

    public ManagedJobAdSearchRequest setCompanyId(String companyId) {
        this.companyId = companyId;
        return this;
    }

    public Integer getOnlineSinceDays() {
        return onlineSinceDays;
    }

    public ManagedJobAdSearchRequest setOnlineSinceDays(Integer onlineSinceDays) {
        this.onlineSinceDays = onlineSinceDays;
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
