package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.FavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;

import java.util.List;

public class JobAdvertisementSearchResult {

    private JobAdvertisementDto jobAdvertisementDto;

    private FavouriteItemDto favouriteItemDto;

    public JobAdvertisementSearchResult(JobAdvertisementDto jobAdvertisementDto, FavouriteItemDto favouriteItemDto) {
        this.jobAdvertisementDto = jobAdvertisementDto;
        this.favouriteItemDto = favouriteItemDto;
    }
    public static JobAdvertisementSearchResult mapToSearchResult(
            JobAdvertisementDto jobAdvertisementDto, List<FavouriteItemDto> favouriteItemDtoList) {

        // TODO different approach?
        return new JobAdvertisementSearchResult(jobAdvertisementDto, favouriteItemDtoList.stream()
                .filter(favItem -> favItem.getJobAdvertisementId().equals(jobAdvertisementDto.getId()))
                .findFirst().orElse(null));
    }

    public JobAdvertisementDto getJobAdvertisementDto() {
        return jobAdvertisementDto;
    }

    public FavouriteItemDto getFavouriteItemDto() {
        return favouriteItemDto;
    }
}
