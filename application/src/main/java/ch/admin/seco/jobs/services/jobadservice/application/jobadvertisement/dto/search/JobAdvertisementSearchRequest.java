package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.search;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.ProfessionCode;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.RadiusSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.ValidWorkingTimeRange;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Arrays;

@ValidWorkingTimeRange
public class JobAdvertisementSearchRequest {

    private String language;

    @Valid
    private ProfessionCode[] professionCodes;

    private String[] keywords;

    private String[] communalCodes;

    private String[] cantonCodes;

    @Valid
    private RadiusSearchRequest radiusSearchRequest;

    @Range(min = 0, max = 100)
    private Integer workloadPercentageMin;

    @Range(min = 0, max = 100)
    private Integer workloadPercentageMax;

    private Boolean permanent;

    private String companyName;

    @Min(1)
    @Max(60)
    private Integer onlineSince;

    private Boolean displayRestricted;

    private Boolean euresDisplay;

    public String getLanguage() {
        return language;
    }

    public JobAdvertisementSearchRequest setLanguage(String language) {
        this.language = language;
        return this;
    }

    public ProfessionCode[] getProfessionCodes() {
        return professionCodes;
    }

    public JobAdvertisementSearchRequest setProfessionCodes(ProfessionCode[] professionCodes) {
        this.professionCodes = professionCodes;
        return this;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public JobAdvertisementSearchRequest setKeywords(String[] keywords) {
        this.keywords = keywords;
        return this;
    }

    public String[] getCommunalCodes() {
        return communalCodes;
    }

    public JobAdvertisementSearchRequest setCommunalCodes(String[] communalCodes) {
        this.communalCodes = communalCodes;
        return this;
    }

    public String[] getCantonCodes() {
        return cantonCodes;
    }

    public JobAdvertisementSearchRequest setCantonCodes(String[] cantonCodes) {
        this.cantonCodes = cantonCodes;
        return this;
    }

    public RadiusSearchRequest getRadiusSearchRequest() {
        return radiusSearchRequest;
    }

    public JobAdvertisementSearchRequest setRadiusSearchRequest(RadiusSearchRequest radiusSearchRequest) {
        this.radiusSearchRequest = radiusSearchRequest;
        return this;
    }

    public Integer getWorkloadPercentageMin() {
        return workloadPercentageMin;
    }

    public JobAdvertisementSearchRequest setWorkloadPercentageMin(Integer workloadPercentageMin) {
        this.workloadPercentageMin = workloadPercentageMin;
        return this;
    }

    public Integer getWorkloadPercentageMax() {
        return workloadPercentageMax;
    }

    public JobAdvertisementSearchRequest setWorkloadPercentageMax(Integer workloadPercentageMax) {
        this.workloadPercentageMax = workloadPercentageMax;
        return this;
    }

    public Boolean isPermanent() {
        return permanent;
    }

    public JobAdvertisementSearchRequest setPermanent(Boolean permanent) {
        this.permanent = permanent;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public JobAdvertisementSearchRequest setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public Integer getOnlineSince() {
        return onlineSince;
    }

    public JobAdvertisementSearchRequest setOnlineSince(Integer onlineSince) {
        this.onlineSince = onlineSince;
        return this;
    }

    public Boolean getDisplayRestricted() {
        return displayRestricted;
    }

    public JobAdvertisementSearchRequest setDisplayRestricted(Boolean displayRestricted) {
        this.displayRestricted = displayRestricted;
        return this;
    }

    public Boolean getEuresDisplay() {
        return euresDisplay;
    }

    public JobAdvertisementSearchRequest setEuresDisplay(Boolean euresDisplay) {
        this.euresDisplay = euresDisplay;
        return this;
    }

    @Override
    public String toString() {
        return "JobAdvertisementSearchRequest{" +
                "language='" + language + '\'' +
                ", professionCodes=" + Arrays.toString(professionCodes) +
                ", keywords=" + Arrays.toString(keywords) +
                ", communalCodes=" + Arrays.toString(communalCodes) +
                ", cantonCodes=" + Arrays.toString(cantonCodes) +
                ", radiusSearchRequest=" + radiusSearchRequest +
                ", workloadPercentageMin=" + workloadPercentageMin +
                ", workloadPercentageMax=" + workloadPercentageMax +
                ", permanent=" + permanent +
                ", companyName='" + companyName + '\'' +
                ", onlineSince=" + onlineSince +
                ", displayRestricted=" + displayRestricted +
                ", euresDisplay=" + euresDisplay +
                '}';
    }

}
