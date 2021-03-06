package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem;

import ch.admin.seco.alv.shared.logger.business.BusinessLogData;
import ch.admin.seco.alv.shared.logger.business.BusinessLogger;
import ch.admin.seco.jobs.services.jobadservice.application.IsSysAdmin;
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
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static ch.admin.seco.jobs.services.jobadservice.application.common.logging.BusinessLogConstants.STATUS_ADDITIONAL_DATA;
import static ch.admin.seco.jobs.services.jobadservice.application.common.logging.BusinessLogEventType.JOB_ADVERTISEMENT_FAVORITE_EVENT;
import static ch.admin.seco.jobs.services.jobadservice.application.common.logging.BusinessLogObjectType.JOB_ADVERTISEMENT_LOG;

@Service
@Transactional
public class FavouriteItemApplicationService {
    private static final Logger LOG = LoggerFactory.getLogger(FavouriteItemApplicationService.class);

    private final FavouriteItemRepository favouriteItemRepository;

    private final JobAdvertisementRepository jobAdvertisementRepository;

    private final BusinessLogger businessLogger;

    public FavouriteItemApplicationService(
            FavouriteItemRepository favouriteItemRepository,
            JobAdvertisementRepository jobAdvertisementRepository,
            BusinessLogger businessLogger) {
        this.favouriteItemRepository = favouriteItemRepository;
        this.jobAdvertisementRepository = jobAdvertisementRepository;
        this.businessLogger = businessLogger;
    }

    @PreAuthorize("isAuthenticated() and @favouriteItemAuthorizationService.matchesCurrentUserId(#createFavouriteItemDto.ownerUserId)")
    public FavouriteItemDto create(CreateFavouriteItemDto createFavouriteItemDto) {
        Condition.notNull(createFavouriteItemDto, "CreateFavouriteItemDto can't be null");
        if (!this.jobAdvertisementRepository.existsById(createFavouriteItemDto.getJobAdvertisementId())) {
            throw new AggregateNotFoundException(JobAdvertisement.class, createFavouriteItemDto.getJobAdvertisementId().getValue());
        }

        this.favouriteItemRepository.findByJobAdvertisementIdAndOwnerId(createFavouriteItemDto.getJobAdvertisementId(), createFavouriteItemDto.getOwnerUserId())
                .ifPresent(favouriteItem -> {
                    throw new FavouriteItemAlreadyExists(favouriteItem.getId(), favouriteItem.getJobAdvertisementId(), favouriteItem.getOwnerUserId());
                });

        FavouriteItem favouriteItem = new FavouriteItem.Builder()
                .setId(new FavouriteItemId())
                .setNote(createFavouriteItemDto.getNote())
                .setOwnerUserId(createFavouriteItemDto.getOwnerUserId())
                .setJobAdvertisementId(createFavouriteItemDto.getJobAdvertisementId()).build();

        FavouriteItem newFavouriteItem = this.favouriteItemRepository.save(favouriteItem);
        LOG.debug("Favourite Item {} has been created for user {}.", newFavouriteItem.getId().getValue(), newFavouriteItem.getOwnerUserId());

        createBusinessLogEntry(newFavouriteItem);

        DomainEventPublisher.publish(new FavouriteItemCreatedEvent(newFavouriteItem));
        return FavouriteItemDto.toDto(newFavouriteItem);
    }

    private void createBusinessLogEntry(FavouriteItem newFavouriteItem) {
        JobAdvertisementId jobAdvertisementId = newFavouriteItem.getJobAdvertisementId();
        JobAdvertisementStatus jobAdStatus = getJobAdvertisement(jobAdvertisementId).getStatus();
        BusinessLogData logData = BusinessLogData.of(JOB_ADVERTISEMENT_FAVORITE_EVENT)
                .withObjectType(JOB_ADVERTISEMENT_LOG)
                .withObjectId(jobAdvertisementId.getValue())
                .withAdditionalData(STATUS_ADDITIONAL_DATA, jobAdStatus);
        this.businessLogger.log(logData);
    }

    @PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.isCurrentUserOwner(#updateFavouriteItemDto.id)")
    public FavouriteItemDto update(UpdateFavouriteItemDto updateFavouriteItemDto) {
        Condition.notNull(updateFavouriteItemDto, "UpdateFavouriteItemDto can't be null");
        FavouriteItem favouriteItem = getFavouriteItem(updateFavouriteItemDto.getId());
        favouriteItem.update(updateFavouriteItemDto.getNote());
        LOG.debug("{} has been updated.", favouriteItem.toString());
        return FavouriteItemDto.toDto(favouriteItem);
}

    @PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.isCurrentUserOwner(#favouriteItemId)")
    public void delete(FavouriteItemId favouriteItemId) {
        Condition.notNull(favouriteItemId, "FavouriteItemId can't be null");
        FavouriteItem favouriteItem = getFavouriteItem(favouriteItemId);
        DomainEventPublisher.publish(new FavouriteItemDeletedEvent(favouriteItem));
        this.favouriteItemRepository.delete(favouriteItem);
        LOG.debug("Favourite Item {} has been deleted for user {}.", favouriteItem.getId().getValue(), favouriteItem.getOwnerUserId());
    }

    @PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.matchesCurrentUserId(#ownerId)")
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

    @IsSysAdmin
    public void deleteUserFavouriteItems(String ownerUserId) {
        Condition.notNull(ownerUserId, "OwnerUserId can't be null");
        List<FavouriteItem> favouriteItems = this.favouriteItemRepository.findAllByOwnerUserId(ownerUserId);
        favouriteItems.forEach(favouriteItem -> {
            this.favouriteItemRepository.delete(favouriteItem);
            DomainEventPublisher.publish(new FavouriteItemDeletedEvent(favouriteItem));
        });
    }

    private FavouriteItem getFavouriteItem(FavouriteItemId id) {
        return this.favouriteItemRepository.findById(id)
                .orElseThrow(() -> new FavoriteItemNotExitsException(id));
    }

    private JobAdvertisement getJobAdvertisement(JobAdvertisementId jobAdvertisementId) {
        Optional<JobAdvertisement> jobAdvertisement = jobAdvertisementRepository.findById(jobAdvertisementId);
        return jobAdvertisement.orElseThrow(() -> new AggregateNotFoundException(JobAdvertisement.class, jobAdvertisementId.getValue()));
    }
}