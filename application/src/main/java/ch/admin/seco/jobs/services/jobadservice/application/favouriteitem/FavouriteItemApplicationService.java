package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.FavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.create.CreateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.update.UpdateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventPublisher;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events.FavouriteItemCreatedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FavouriteItemApplicationService {

    private static Logger LOG = LoggerFactory.getLogger(FavouriteItemApplicationService.class);

    private final FavouriteItemRepository favouriteItemRepository;

    private final JobAdvertisementRepository jobAdvertisementRepository;

    public FavouriteItemApplicationService(FavouriteItemRepository favouriteItemRepository, JobAdvertisementRepository jobAdvertisementRepository) {
        this.favouriteItemRepository = favouriteItemRepository;
        this.jobAdvertisementRepository = jobAdvertisementRepository;
    }

    // TODO WEB
    // POST -> /api/favourite-items
    @PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.matchesCurrentUserId(#createFavouriteItemDto.ownerId)")
    public FavouriteItemId create(CreateFavouriteItemDto createFavouriteItemDto) {
        Condition.notNull(createFavouriteItemDto, "CreateFavouriteItemDto can't be null");
        if (!this.jobAdvertisementRepository.existsById(createFavouriteItemDto.getJobAdvertisementId())) {
            throw new AggregateNotFoundException(JobAdvertisement.class, createFavouriteItemDto.getJobAdvertisementId().getValue());
        }
        FavouriteItem favouriteItem = new FavouriteItem.Builder()
                .setId(new FavouriteItemId())
                .setNote(createFavouriteItemDto.getNote())
                .setOwnerId(createFavouriteItemDto.getOwnerId())
                .setJobAdvertismentId(createFavouriteItemDto.getJobAdvertisementId()).build();
        DomainEventPublisher.publish(new FavouriteItemCreatedEvent(favouriteItem));
        this.favouriteItemRepository.save(favouriteItem);
        return favouriteItem.getId();
    }

    // TODO WEB
    // PUT/POST -> /api/favourite-items/<id>/note
    @PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.canUpdate(#updateFavouriteItemDto.id)")
    public void update(UpdateFavouriteItemDto updateFavouriteItemDto) {
        Condition.notNull(updateFavouriteItemDto, "UpdateFavouriteItemDto can't be null");
        FavouriteItem favouriteItem = this.favouriteItemRepository.findById(updateFavouriteItemDto.getId())
                .orElseThrow(() -> new FavoriteItemNotExitsException(updateFavouriteItemDto.getId()));
        favouriteItem.update(updateFavouriteItemDto.getNote());
    }

    // TODO WEB
    // GET  -> /api/favourite-items/<id>
    // Authorization: only owner

    // TODO WEB
    // DELETE  -> /api/favourite-items/<id>
    // Authorization: only owner
}
