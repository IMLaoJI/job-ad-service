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
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events.FavouriteItemDeletedEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    @PreAuthorize("isAuthenticated() and @favouriteItemAuthorizationService.matchesCurrentUserId(#createFavouriteItemDto.ownerId)")
    public FavouriteItemId create(CreateFavouriteItemDto createFavouriteItemDto) {
        Condition.notNull(createFavouriteItemDto, "CreateFavouriteItemDto can't be null");
        if (!this.jobAdvertisementRepository.existsById(createFavouriteItemDto.getJobAdvertisementId())) {
            throw new AggregateNotFoundException(JobAdvertisement.class, createFavouriteItemDto.getJobAdvertisementId().getValue());
        }

        this.favouriteItemRepository.findByJobAdvertisementIdAndOwnerId(createFavouriteItemDto.getJobAdvertisementId(), createFavouriteItemDto.getOwnerUserId())
                .ifPresent(favouriteItem -> {
                    throw new FavouriteItemAlreadyExists(favouriteItem.getId(), favouriteItem.getJobAdvertisementId(), favouriteItem.getOwnerId());
                });

        FavouriteItem favouriteItem = new FavouriteItem.Builder()
                .setId(new FavouriteItemId())
                .setNote(createFavouriteItemDto.getNote())
                .setOwnerId(createFavouriteItemDto.getOwnerUserId())
                .setJobAdvertismentId(createFavouriteItemDto.getJobAdvertisementId()).build();

        FavouriteItem newFavouriteItem = this.favouriteItemRepository.save(favouriteItem);
        LOG.info("Favourite Item " + newFavouriteItem.getId().getValue() + " has been created for user " + newFavouriteItem.getOwnerId() + ".");
        DomainEventPublisher.publish(new FavouriteItemCreatedEvent(newFavouriteItem));
        return newFavouriteItem.getId();
    }

    @PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.isCurrentUserOwner(#updateFavouriteItemDto.id)")
    public void update(UpdateFavouriteItemDto updateFavouriteItemDto) {
        Condition.notNull(updateFavouriteItemDto, "UpdateFavouriteItemDto can't be null");
        FavouriteItem favouriteItem = getFavouriteItem(updateFavouriteItemDto.getId());
        favouriteItem.update(updateFavouriteItemDto.getNote());
        LOG.info("Favourite Item " + favouriteItem.getId().getValue() + " has been updated for user " + favouriteItem.getOwnerId() + " with note " + favouriteItem.getNote() + ".");
    }

    @PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.isCurrentUserOwner(#favouriteItemId)")
    public void delete(FavouriteItemId favouriteItemId) {
        Condition.notNull(favouriteItemId, "FavouriteItemId can't be null");
        FavouriteItem favouriteItem = getFavouriteItem(favouriteItemId);
        DomainEventPublisher.publish(new FavouriteItemDeletedEvent(favouriteItem));
        this.favouriteItemRepository.delete(favouriteItem);
        LOG.info("Favourite Item " + favouriteItem.getId().getValue() + " has been deleted for user " + favouriteItem.getOwnerId() + ".");
    }

    @PreAuthorize("isAuthenticated() and favouriteItemAuthorizationService.matchesCurrentUserId(#ownerId)")
    public Optional<FavouriteItemDto> findByJobAdvertisementIdAndUserId(JobAdvertisementId jobAdvertisementId, String ownerId) {
        return this.favouriteItemRepository.findByJobAdvertisementIdAndOwnerId(jobAdvertisementId, ownerId)
                .map(FavouriteItemDto::toDto);
    }

    @PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.isCurrentUserOwner(#favouriteItemId)")
    public FavouriteItemDto findById(FavouriteItemId favouriteItemId) throws FavoriteItemNotExitsException {
        Condition.notNull(favouriteItemId, "FavouriteItemId can't be null");
        return this.favouriteItemRepository.findById(favouriteItemId).map(FavouriteItemDto::toDto)
                .orElseThrow(() -> new FavoriteItemNotExitsException(favouriteItemId));
    }

    private FavouriteItem getFavouriteItem(FavouriteItemId id) {
        return this.favouriteItemRepository.findById(id)
                .orElseThrow(() -> new FavoriteItemNotExitsException(id));
    }
/*
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
    }*/

}