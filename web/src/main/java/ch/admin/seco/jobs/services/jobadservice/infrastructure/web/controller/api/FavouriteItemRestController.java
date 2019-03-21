package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.FavoriteItemNotExitsException;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.FavouriteItemApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.FavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.create.CreateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.read.ReadFavouriteItemByJobAdvertisementIdDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.update.UpdateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/favourite-items")
public class FavouriteItemRestController {

    private final FavouriteItemApplicationService favouriteItemApplicationService;


    public FavouriteItemRestController(FavouriteItemApplicationService favouriteItemApplicationService) {
        this.favouriteItemApplicationService = favouriteItemApplicationService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public FavouriteItemId createFavouriteItem(@RequestBody @Valid CreateFavouriteItemDto createFavouriteItemDto) throws AggregateNotFoundException {
        return favouriteItemApplicationService.create(createFavouriteItemDto);
    }

    @PutMapping("/{id}/{note}")
    public void updateFavouriteItem(@PathVariable String id, @PathVariable String note) throws FavoriteItemNotExitsException {
        FavouriteItemId favouriteItemId = new FavouriteItemId(id);
        UpdateFavouriteItemDto updateFavouriteItemDto = new UpdateFavouriteItemDto(favouriteItemId, note);
        favouriteItemApplicationService.update(updateFavouriteItemDto);
    }

    @DeleteMapping("/{id}")
    public void deleteFavouriteItem(@PathVariable String id) throws FavoriteItemNotExitsException {
        FavouriteItemId favouriteItemId = new FavouriteItemId(id);
        favouriteItemApplicationService.delete(favouriteItemId);
    }

    @GetMapping("/{id}")
    public FavouriteItemDto findById(@PathVariable String id) {
        FavouriteItemId favouriteItemId = new FavouriteItemId(id);
        return favouriteItemApplicationService.findById(favouriteItemId);
    }

    @GetMapping("/find-by-owner-id/{ownerId}")
    public Page<FavouriteItemDto> findByOwnerId(Pageable pageable, @PathVariable String ownerId) {
        return favouriteItemApplicationService.findByOwnerId(pageable, ownerId);
    }

    @GetMapping("/find-by-job-advertisement-id/{ownerId}")
    public Page<FavouriteItemDto> findByJobAdvertisementId(Pageable pageable, @PathVariable String ownerId, @RequestParam String jobAdvertisementId) {
        JobAdvertisementId searchedJobAdvertisementId = new JobAdvertisementId(jobAdvertisementId);
        ReadFavouriteItemByJobAdvertisementIdDto readFavouriteItemByJobAdvertisementIdDto = new ReadFavouriteItemByJobAdvertisementIdDto(ownerId, searchedJobAdvertisementId);
        return favouriteItemApplicationService.findByJobAdvertisementId(pageable, readFavouriteItemByJobAdvertisementIdDto);
    }
}
