package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.FavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.create.CreateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.read.ReadFavouriteItemByJobAdvertisementIdDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.update.UpdateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUser;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventPublisher;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events.FavouriteItemCreatedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events.FavouriteItemDeletedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class FavouriteItemApplicationService {

    private static Logger LOG = LoggerFactory.getLogger(FavouriteItemApplicationService.class);

    private final FavouriteItemRepository favouriteItemRepository;

    private final JobAdvertisementRepository jobAdvertisementRepository;

    private final CurrentUserContext currentUserContext;

    public FavouriteItemApplicationService(FavouriteItemRepository favouriteItemRepository, JobAdvertisementRepository jobAdvertisementRepository, CurrentUserContext currentUserContext) {
        this.favouriteItemRepository = favouriteItemRepository;
        this.jobAdvertisementRepository = jobAdvertisementRepository;
        this.currentUserContext = currentUserContext;
    }

    // TODO WEB
    // POST -> /api/favourite-items
    @PreAuthorize("isAuthenticated()")
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
        LOG.info("Favourite Item " + favouriteItem.getId() + " has been created for user " + favouriteItem.getOwnerId() + ".");
        return favouriteItem.getId();
    }

    // PUT/POST -> /api/favourite-items/<id>/note
    @PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.canUpdate(#updateFavouriteItemDto.id)")
    public void update(UpdateFavouriteItemDto updateFavouriteItemDto) {
        Condition.notNull(updateFavouriteItemDto, "UpdateFavouriteItemDto can't be null");
        FavouriteItem favouriteItem = this.favouriteItemRepository.findById(updateFavouriteItemDto.getId())
                .orElseThrow(() -> new FavoriteItemNotExitsException(updateFavouriteItemDto.getId()));
        favouriteItem.update(updateFavouriteItemDto.getNote());
        LOG.info("Favourite Item " + favouriteItem.getId() + " has been updated for user " + favouriteItem.getOwnerId() + ".");
    }

    // DELETE  -> /api/favourite-items/<id>
    @PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.canUpdate(#favouriteItemId)")
    public void delete(FavouriteItemId favouriteItemId) {
        Condition.notNull(favouriteItemId, "FavouriteItemId can't be null");
        FavouriteItem favouriteItem = this.favouriteItemRepository.findById(favouriteItemId)
                .orElseThrow(() -> new FavoriteItemNotExitsException(favouriteItemId));
        DomainEventPublisher.publish(new FavouriteItemDeletedEvent(favouriteItem));
        this.favouriteItemRepository.delete(favouriteItem);
        LOG.info("Favourite Item " + favouriteItem.getId() + " has been deleted for user " + favouriteItem.getOwnerId() + ".");
    }

    // GET  -> /api/favourite-items/<id>
    @PreAuthorize("isAuthenticated()")
    public FavouriteItemDto findById(FavouriteItemId favouriteItemId) {
        Condition.notNull(favouriteItemId, "FavouriteItemId can't be null");
        return this.favouriteItemRepository.findById(favouriteItemId).map(FavouriteItemDto::toDto)
                .orElse(null);
    }


    // GET -> /api/favourite-items/?ownerid=<ownerid>
    @PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.matchesCurrentUserId(#ownerId)")
    public Page<FavouriteItemDto> findByOwnerId(Pageable pageable, String ownerId) {
        CurrentUser currentUser = currentUserContext.getCurrentUser();
        Condition.notNull(currentUser, "CurrentUser can't be null");
        Page<FavouriteItem> jobAdvertisements = favouriteItemRepository.findByIdOwnerId(pageable, ownerId);
        return new PageImpl<>(
                jobAdvertisements.getContent().stream().map(FavouriteItemDto::toDto).collect(toList()),
                jobAdvertisements.getPageable(),
                jobAdvertisements.getTotalElements()
        );
    }

    // GET -> /api/favourite-items/?ownerid=<ownerid>
    @PreAuthorize("isAuthenticated()&& @favouriteItemAuthorizationService.matchesCurrentUserId(#readFavouriteItemByJobAdvertismentIdDto.ownerId)")
    public Page<FavouriteItemDto> findByJobAdvertisementId(Pageable pageable, ReadFavouriteItemByJobAdvertisementIdDto readFavouriteItemByJobAdvertismentIdDto) {
        Condition.notNull(readFavouriteItemByJobAdvertismentIdDto.getJobAdvertisementId(), "ReadFavouriteItemByJobAdvertisementIdDto can't be null");
        if (!jobAdvertisementRepository.existsById(readFavouriteItemByJobAdvertismentIdDto.getJobAdvertisementId())) {
            throw new AggregateNotFoundException(JobAdvertisement.class, readFavouriteItemByJobAdvertismentIdDto.getJobAdvertisementId().getValue());
        }
        Page<FavouriteItem> jobAdvertisements = favouriteItemRepository.findByJobAdvertisementId(pageable, readFavouriteItemByJobAdvertismentIdDto.getJobAdvertisementId(), readFavouriteItemByJobAdvertismentIdDto.getOwnerId());
        return new PageImpl<>(
                jobAdvertisements.getContent().stream().map(FavouriteItemDto::toDto).collect(toList()),
                jobAdvertisements.getPageable(),
                jobAdvertisements.getTotalElements()
        );
    }
}
