package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.FavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.create.CreateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.update.UpdateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventMockUtils;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events.FavouriteItemEvents;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class FavouriteItemApplicationServiceTest {

    private final JobAdvertisementId jobAdvertisementId = new JobAdvertisementId("JOB_AD_ID_1");

    private final FavouriteItemId favouriteItemId = new FavouriteItemId("FAV_ID_1");

    private final String userId = "USER-1";

    private final String note = "My note";

    @Autowired
    private FavouriteItemApplicationService sut;

    @MockBean
    private JobAdvertisementRepository jobAdvertisementRepository;

    @Autowired
    private FavouriteItemRepository favouriteItemRepository;

    private DomainEventMockUtils domainEventMockUtils;

    @Before
    public void setUp() {
        this.domainEventMockUtils = new DomainEventMockUtils();
    }

    @After
    public void tearDown() {
        this.jobAdvertisementRepository.deleteAll();
        this.domainEventMockUtils.clearEvents();
    }

    @Test
    public void testCreate() {
        // given
        CreateFavouriteItemDto createFavouriteItemDto = new CreateFavouriteItemDto();
        createFavouriteItemDto.setJobAdvertisementId(jobAdvertisementId);
        createFavouriteItemDto.setNote(note);
        createFavouriteItemDto.setOwnerUserId(userId);

        when(jobAdvertisementRepository.existsById(any())).thenReturn(true);

        //when
        FavouriteItemId favouriteItemId = this.sut.create(createFavouriteItemDto);

        //then
        Optional<FavouriteItem> createdFavouriteItem = favouriteItemRepository.findById(favouriteItemId);
        assertThat(createdFavouriteItem).isPresent();
        assertThat(createdFavouriteItem.get().getNote()).isEqualTo(createFavouriteItemDto.getNote());
        domainEventMockUtils.assertSingleDomainEventPublished(FavouriteItemEvents.FAVOURITE_ITEM_CREATED.getDomainEventType());
    }

    @Test
    public void testCanNotCreateFavouriteWithNonExistingJobAdId() {
        // given
        CreateFavouriteItemDto createFavouriteItemDto = new CreateFavouriteItemDto();
        createFavouriteItemDto.setJobAdvertisementId(jobAdvertisementId);
        createFavouriteItemDto.setNote(note);
        createFavouriteItemDto.setOwnerUserId(userId);

        // when
        when(jobAdvertisementRepository.existsById(jobAdvertisementId)).thenReturn(false);

        // then
        assertThatThrownBy(() -> this.sut.create(createFavouriteItemDto))
                .isInstanceOf(AggregateNotFoundException.class)
                .hasMessageContaining("Aggregate was not found");
    }

    @Test
    public void testCanNotCreateFavouriteThatAlreadyExists() {
        // given
        when(jobAdvertisementRepository.existsById(any())).thenReturn(true);

        createAndSaveToDBFavouriteItem(favouriteItemId, jobAdvertisementId, note, userId);

        CreateFavouriteItemDto createFavouriteItemDto = new CreateFavouriteItemDto();
        createFavouriteItemDto.setJobAdvertisementId(jobAdvertisementId);
        createFavouriteItemDto.setNote("My other note");
        createFavouriteItemDto.setOwnerUserId(userId);

        // then
        assertThatThrownBy(() -> this.sut.create(createFavouriteItemDto))
                .isInstanceOf(FavouriteItemAlreadyExists.class)
                .hasMessageContaining("FavouriteItem couldn't be created. User: 'USER-1' already has a FavouriteItem: 'FAV_ID_1' for JobAdvertismentId: 'JOB_AD_ID_1'");
    }

    @Test
    public void testFindExistingFavouriteItemFindById() {
        // given
        createAndSaveToDBFavouriteItem(favouriteItemId, jobAdvertisementId, note, userId);

        // when
        FavouriteItemDto favouriteItem = this.sut.findById(favouriteItemId);

        // then
        assertThat(favouriteItem.getId()).isEqualTo(favouriteItemId.getValue());
    }

    @Test
    public void testFindNonExistingFavouriteItemFindById() {
        // then
        assertThatThrownBy(() -> this.sut.findById(favouriteItemId))
                .isInstanceOf(FavoriteItemNotExitsException.class)
                .hasMessageContaining("Aggregate was not found");
    }

    @Test
    public void testUpdateExistingFavouriteItem() {
        // given
        String adjustedNote = "My new note";


        createAndSaveToDBFavouriteItem(favouriteItemId, jobAdvertisementId, note, userId);
        UpdateFavouriteItemDto updateFavouriteItemDto = new UpdateFavouriteItemDto(favouriteItemId, adjustedNote);

        // when
        this.sut.update(updateFavouriteItemDto);
        FavouriteItemDto favouriteItemDto = this.sut.findById(favouriteItemId);

        // then
        assertThat(favouriteItemDto.getNote()).isEqualTo(adjustedNote);
    }

    @Test
    public void testDeleteExistingFavouriteItem() {
        // given
        createAndSaveToDBFavouriteItem(favouriteItemId, jobAdvertisementId, note, userId);

        // when
        assertThat(this.sut.findById(favouriteItemId).getJobAdvertisementId()).isEqualTo(jobAdvertisementId.getValue());
        this.sut.delete(favouriteItemId);
        // then
        assertThatThrownBy(() -> this.sut.findById(favouriteItemId))
                .isInstanceOf(FavoriteItemNotExitsException.class)
                .hasMessageContaining("Aggregate was not found");
    }

    @Test
    public void testFindByJobAdvertisementIdAndOwnerId() {
        // given
        createAndSaveToDBFavouriteItem(favouriteItemId, jobAdvertisementId, note, userId);

        // when
        Optional<FavouriteItemDto> optionalFavouriteItemDto = this.sut.findByJobAdvertisementIdAndUserId(jobAdvertisementId, userId);

        // then
        assertThat(optionalFavouriteItemDto).isPresent();
    }

    private void createAndSaveToDBFavouriteItem(FavouriteItemId favouriteItemId, JobAdvertisementId jobAdvertisementId, String note, String ownerId) {
        FavouriteItem favouriteItem = new FavouriteItem.Builder()
                .setId(favouriteItemId)
                .setJobAdvertisementId(jobAdvertisementId)
                .setNote(note)
                .setOwnerId(ownerId)
                .build();
        this.favouriteItemRepository.save(favouriteItem);
    }
}