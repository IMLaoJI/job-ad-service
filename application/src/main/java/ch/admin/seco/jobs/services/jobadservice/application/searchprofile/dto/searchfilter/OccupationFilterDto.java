package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.searchfilter;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.OccupationFilter;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.searchfilter.OccupationFilterType;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OccupationFilterDto {

    private String labelId;

    private OccupationFilterType type;

    public OccupationFilterDto() {}

    public String getLabelId() {
        return labelId;
    }

    public OccupationFilterDto setLabelId(String labelId) {
        this.labelId = labelId;
        return this;
    }

    public OccupationFilterType getType() {
        return type;
    }

    public OccupationFilterDto setType(OccupationFilterType type) {
        this.type = type;
        return this;
    }

    public static OccupationFilterDto toDto(OccupationFilter occupationFilter) {
        if (occupationFilter == null) {
            return null;
        }
        return new OccupationFilterDto()
                .setLabelId(occupationFilter.getLabelId())
                .setType(occupationFilter.getType());
    }

    public static List<OccupationFilterDto> toDto(List<OccupationFilter> occupationFilters) {
        if (CollectionUtils.isEmpty(occupationFilters)) {
            return Collections.emptyList();
        }
        return occupationFilters.stream().map(OccupationFilterDto::toDto).collect(Collectors.toList());
    }
}
