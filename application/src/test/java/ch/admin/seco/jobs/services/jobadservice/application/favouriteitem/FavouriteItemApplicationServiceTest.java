package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.FavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.create.CreateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.update.UpdateFavouriteItemDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class FavouriteItemApplicationServiceTest {

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
        createFavouriteItemDto.setJobAdvertisementId(new JobAdvertisementId("JOB_AD_ID_1"));
        createFavouriteItemDto.setNote("My Note");
        createFavouriteItemDto.setOwnerUserId("USER-1");

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

    }

    @Test
    public void testCanNotCreateFavouriteThatAlreadyExists() {
        // given
        FavouriteItemId favouriteItemId = new FavouriteItemId("FAV_ID_1");
        JobAdvertisementId jobAdvertisementId = new JobAdvertisementId("JOB_AD_ID_1");
        String ownerId = "USER-1";
        String note = "My note";

        when(jobAdvertisementRepository.existsById(any())).thenReturn(true);

        createAndSaveToDBFavouriteItem(favouriteItemId, jobAdvertisementId, note, ownerId);

        CreateFavouriteItemDto createFavouriteItemDto = new CreateFavouriteItemDto();
        createFavouriteItemDto.setJobAdvertisementId(jobAdvertisementId);
        createFavouriteItemDto.setNote("My other note");
        createFavouriteItemDto.setOwnerUserId(ownerId);

        // then
        assertThatThrownBy(() -> this.sut.create(createFavouriteItemDto))
                .isInstanceOf(FavouriteItemAlreadyExists.class)
                .hasMessageContaining("FavouriteItem couldn't be created. User: 'USER-1' already has a FavouriteItem: 'FAV_ID_1' for JobAdvertismentId: 'JOB_AD_ID_1'");
    }

    @Test
    public void testFindExistingFavouriteItemFindById() {
        // given
        FavouriteItemId favouriteItemId = new FavouriteItemId("FAV_ID_1");
        JobAdvertisementId jobAdvertisementId = new JobAdvertisementId("JOB_AD_ID_1");
        String ownerId = "USER-1";
        String note = "My note";

        createAndSaveToDBFavouriteItem(favouriteItemId, jobAdvertisementId, note, ownerId);

        // when
        FavouriteItemDto favouriteItem = this.sut.findById(favouriteItemId);

        // then
        assertThat(favouriteItem.getId()).isEqualTo(favouriteItemId.getValue());
    }

    @Test
    public void testFindNonExistingFavouriteItemFindById() {
        // given
        FavouriteItemId favouriteItemId = new FavouriteItemId("FAV_ID_1");

        // then
        assertThatThrownBy(() -> this.sut.findById(favouriteItemId))
                .isInstanceOf(FavoriteItemNotExitsException.class)
                .hasMessageContaining("Aggregate was not found");
    }

    @Test
    public void testUpdateExistingFavouriteItem() {
        // given
        FavouriteItemId favouriteItemId = new FavouriteItemId("FAV_ID_1");
        JobAdvertisementId jobAdvertisementId = new JobAdvertisementId("JOB_AD_ID_1");
        String ownerId = "USER-1";
        String originalNote = "My note";
        String adjustedNote = "My new note";


        createAndSaveToDBFavouriteItem(favouriteItemId, jobAdvertisementId, originalNote, ownerId);
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
        FavouriteItemId favouriteItemId = new FavouriteItemId("FAV_ID_1");
        JobAdvertisementId jobAdvertisementId = new JobAdvertisementId("JOB_AD_ID_1");
        String ownerId = "USER-1";
        String note = "My note";

        createAndSaveToDBFavouriteItem(favouriteItemId, jobAdvertisementId, note, ownerId);

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
        FavouriteItemId favouriteItemId = new FavouriteItemId("FAV_ID_1");
        JobAdvertisementId jobAdvertisementId = new JobAdvertisementId("JOB_AD_ID_1");
        String ownerId = "USER-1";
        String originalNote = "My note";

        createAndSaveToDBFavouriteItem(favouriteItemId, jobAdvertisementId, originalNote, ownerId);

        // when
        Optional<FavouriteItemDto> optionalFavouriteItemDto = this.sut.findByJobAdvertisementIdAndUserId(jobAdvertisementId, ownerId);

        // then
        assertThat(optionalFavouriteItemDto).isPresent();
    }

    private void createAndSaveToDBFavouriteItem(FavouriteItemId favouriteItemId, JobAdvertisementId jobAdvertisementId, String note, String ownerId) {
        FavouriteItem favouriteItem = new FavouriteItem.Builder()
                .setId(favouriteItemId)
                .setJobAdvertismentId(jobAdvertisementId)
                .setNote(note)
                .setOwnerId(ownerId)
                .build();
        this.favouriteItemRepository.save(favouriteItem);
    }
}