package ch.admin.seco.jobs.services.jobadservice.infrastructure.database.eventstore;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.EventData;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.EventStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public class JpaBasedEventStore implements EventStore {

    private final StoredEventRepository storedEventRepository;

    JpaBasedEventStore(StoredEventRepository storedEventRepository) {
        this.storedEventRepository = storedEventRepository;
    }

    private static List<EventData> toEventDtos(List<StoredEvent> storedEvents) {
        return storedEvents.stream()
                .map(JpaBasedEventStore::toEventDto)
                .collect(Collectors.toList());
    }

    private static EventData toEventDto(StoredEvent storedEvent) {
        return new EventData.Builder()
                .setAggregateId(storedEvent.getAggregateId())
                .setAggregateType(storedEvent.getAggregateType())
                .setId(storedEvent.getId())
                .setDomainEventType(storedEvent.getDomainEventType().getValue())
                .setUserEmail(storedEvent.getUserEmail())
                .setUserId(storedEvent.getUserId())
                .setUserExternalId(storedEvent.getUserExternalId())
                .setDisplayName(storedEvent.getUserDisplayName())
                .setRegistrationTime(storedEvent.getRegistrationTime())
                .setPayload(storedEvent.getPayload()).build();
    }

    @Override
    public Page<EventData> findByAggregateId(String aggregateId, String aggregateType, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        final Page<StoredEvent> storedEvents = storedEventRepository.findByAggregateId(aggregateId, aggregateType, pageable);
        return new PageImpl<>(toEventDtos(storedEvents.getContent()), pageable, storedEvents.getTotalElements());
    }

}
