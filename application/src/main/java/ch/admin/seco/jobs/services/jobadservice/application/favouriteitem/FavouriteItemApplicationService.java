package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.FavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.create.CreateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.read.ReadFavouriteItemByJobAdvertisementIdDto;
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

        FavouriteItem favouriteItem = new FavouriteItem.Builder()
                .setId(new FavouriteItemId())
                .setNote(createFavouriteItemDto.getNote())
                .setOwnerId(createFavouriteItemDto.getOwnerId())
                .setJobAdvertismentId(createFavouriteItemDto.getJobAdvertisementId()).build();
        DomainEventPublisher.publish(new FavouriteItemCreatedEvent(favouriteItem));

        //check that the given owner hasn't yet created a FavouriteItem for the given JobAdvertisementId
        ReadFavouriteItemByJobAdvertisementIdDto readFavouriteItemByJobAdvertisementIdDto = new ReadFavouriteItemByJobAdvertisementIdDto(createFavouriteItemDto.getOwnerId(), createFavouriteItemDto.getJobAdvertisementId());
        Optional<FavouriteItem> byJobAdvertisementIdAndOwnerId = this.findByJobAdvertisementIdAndOwnerId(readFavouriteItemByJobAdvertisementIdDto);
        if (byJobAdvertisementIdAndOwnerId.isPresent()) {
            throw new FavouriteItemAlreadyExistsForJobAdvertisementId(byJobAdvertisementIdAndOwnerId.get().getId(), favouriteItem.getJobAdvertisementId(), favouriteItem.getOwnerId());
        }

        this.favouriteItemRepository.save(favouriteItem);
        LOG.info("Favourite Item " + favouriteItem.getId() + " has been created for user " + favouriteItem.getOwnerId() + ".");
        return favouriteItem.getId();
    }

    @PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.isCurrentUserOwner(#updateFavouriteItemDto.id)")
    public void update(UpdateFavouriteItemDto updateFavouriteItemDto) {
        Condition.notNull(updateFavouriteItemDto, "UpdateFavouriteItemDto can't be null");
        FavouriteItem favouriteItem = this.favouriteItemRepository.findById(updateFavouriteItemDto.getId())
                .orElseThrow(() -> new FavoriteItemNotExitsException(updateFavouriteItemDto.getId()));
        favouriteItem.update(updateFavouriteItemDto.getNote());
        LOG.info("Favourite Item " + favouriteItem.getId() + " has been updated for user " + favouriteItem.getOwnerId() + " with note " + favouriteItem.getNote() + ".");
    }

    @PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.isCurrentUserOwner(#favouriteItemId)")
    public void delete(FavouriteItemId favouriteItemId) {
        Condition.notNull(favouriteItemId, "FavouriteItemId can't be null");
        FavouriteItem favouriteItem = this.favouriteItemRepository.findById(favouriteItemId)
                .orElseThrow(() -> new FavoriteItemNotExitsException(favouriteItemId));
        DomainEventPublisher.publish(new FavouriteItemDeletedEvent(favouriteItem));
        this.favouriteItemRepository.delete(favouriteItem);
        LOG.info("Favourite Item " + favouriteItem.getId() + " has been deleted for user " + favouriteItem.getOwnerId() + ".");
    }

    @PreAuthorize("isAuthenticated() and favouriteItemAuthorizationService.matchesCurrentUserId(#readFavouriteItemByJobAdvertismentIdDto.ownerId)")
    public Optional<FavouriteItem> findByJobAdvertisementIdAndOwnerId(ReadFavouriteItemByJobAdvertisementIdDto readFavouriteItemByJobAdvertismentIdDto) {
        Condition.notNull(readFavouriteItemByJobAdvertismentIdDto, "FavouriteItemId can't be null");
        return this.favouriteItemRepository.findByJobAdvertisementIdAndOwnerId(readFavouriteItemByJobAdvertismentIdDto.getJobAdvertisementId(), readFavouriteItemByJobAdvertismentIdDto.getOwnerId());
    }

    @PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.isCurrentUserOwner(#favouriteItemId)")
    public FavouriteItemDto findById(FavouriteItemId favouriteItemId) {
        Condition.notNull(favouriteItemId, "FavouriteItemId can't be null");
        return this.favouriteItemRepository.findById(favouriteItemId).map(FavouriteItemDto::toDto)
                .orElse(null);
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