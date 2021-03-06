package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;

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

    private String keywordsText;

    private JobAdvertisementStatus state;

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

    public String getKeywordsText() {
        return keywordsText;
    }

    public ManagedJobAdSearchRequest setKeywordsText(String keywordsText) {
        this.keywordsText = keywordsText;
        return this;
    }

    public JobAdvertisementStatus getState() {
        return state;
    }

    public ManagedJobAdSearchRequest setState(JobAdvertisementStatus state) {
        this.state = state;
        return this;
    }
}
