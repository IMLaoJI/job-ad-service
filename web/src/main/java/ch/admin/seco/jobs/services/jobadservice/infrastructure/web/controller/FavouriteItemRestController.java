package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.FavoriteItemNotExitsException;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.FavouriteItemApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.FavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.create.CreateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.update.UpdateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementSearchResult;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementSearchService;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/api/favourite-items")
public class FavouriteItemRestController {

    private final FavouriteItemApplicationService favouriteItemApplicationService;

    private final JobAdvertisementSearchService jobAdvertisementSearchService;

    public FavouriteItemRestController(FavouriteItemApplicationService favouriteItemApplicationService, JobAdvertisementSearchService jobAdvertisementSearchService) {
        this.favouriteItemApplicationService = favouriteItemApplicationService;
        this.jobAdvertisementSearchService = jobAdvertisementSearchService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FavouriteItemId createFavouriteItem(@RequestBody @Valid CreateFavouriteItemResource createFavouriteItemResource) throws AggregateNotFoundException {
        CreateFavouriteItemDto createFavouriteItemDto = new CreateFavouriteItemDto(createFavouriteItemResource.note, createFavouriteItemResource.userId, createFavouriteItemResource.jobAdvertisementId);
        return favouriteItemApplicationService.create(createFavouriteItemDto);
    }

    @PutMapping("/{id}/note")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFavouriteItem(@PathVariable FavouriteItemId id, @RequestBody FavouriteItemUpdateResource favouriteItemUpdateResource) throws FavoriteItemNotExitsException {
        favouriteItemApplicationService.update(new UpdateFavouriteItemDto(id, favouriteItemUpdateResource.note));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFavouriteItem(@PathVariable FavouriteItemId id) throws FavoriteItemNotExitsException {
        favouriteItemApplicationService.delete(id);
    }

    @GetMapping("/{id}")
    public FavouriteItemDto findById(@PathVariable FavouriteItemId id) {
        return favouriteItemApplicationService.findById(id);
    }

    @GetMapping("/_search/byJobAdvertisementIdAndUserId")
    public FavouriteItemDto findByJobAdIdAndUserId(@Valid FavouriteItemRestController.SearchByJobAdIdAndUserIdResource searchByJobAdIdAndUserIdResource) {
        return favouriteItemApplicationService.findByJobAdvertisementIdAndUserId(new JobAdvertisementId(searchByJobAdIdAndUserIdResource.jobAdvertisementId), searchByJobAdIdAndUserIdResource.userId)
                .orElse(null);
    }

    @GetMapping("/_search/byUserId")
    public ResponseEntity<List<JobAdvertisementSearchResult>> findByUserId(@RequestParam String userId,
                                                                           @RequestParam(name = "query", defaultValue = "") String query,
                                                                           @RequestParam(name = "page", defaultValue = "0") int page,
                                                                           @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Page<JobAdvertisementSearchResult> userFavorites = jobAdvertisementSearchService.searchFavouriteJobAds(userId, query, page, size);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(userFavorites, "/api/favourite-items/_search/managed");
        return new ResponseEntity<>(userFavorites.getContent(), headers, HttpStatus.OK);
    }

    static class CreateFavouriteItemResource {
        @Size(max = 1000)
        public String note;

        @NotBlank
        public String userId;

        @NotNull
        public JobAdvertisementId jobAdvertisementId;
    }

    static class SearchByJobAdIdAndUserIdResource {
        @NotBlank
        public String userId;

        @NotBlank
        public String jobAdvertisementId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getJobAdvertisementId() {
            return jobAdvertisementId;
        }

        public void setJobAdvertisementId(String jobAdvertisementId) {
            this.jobAdvertisementId = jobAdvertisementId;
        }
    }

    static class FavouriteItemUpdateResource {
        @Size(max = 1000)
        public String note;
    }
}
