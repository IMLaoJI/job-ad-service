package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem;

import ch.admin.seco.jobs.services.jobadservice.application.BusinessLogEvent;
import ch.admin.seco.jobs.services.jobadservice.application.BusinessLogger;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.FavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.create.CreateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.update.UpdateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventMockUtils;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events.FavouriteItemEvents;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.*;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobContentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static ch.admin.seco.jobs.services.jobadservice.application.BusinessLogEvent.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.LocationFixture.testLocation;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class FavouriteItemApplicationServiceTest {

    private final static JobAdvertisementId JOB_ADVERTISEMENT_ID = new JobAdvertisementId("JOB_AD_ID_1");

    private final static FavouriteItemId FAVOURITE_ITEM_ID = new FavouriteItemId("FAV_ID_1");

    private final static String USER_ID = "USER-1";

    private final static String NOTE = "My note";

    @Autowired
    private FavouriteItemApplicationService sut;

    @MockBean
    private JobAdvertisementRepository jobAdvertisementRepository;

    @Autowired
    private FavouriteItemRepository favouriteItemRepository;

    private DomainEventMockUtils domainEventMockUtils;
    @Autowired
    private BusinessLogger businessLogger;

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
        createFavouriteItemDto.setJobAdvertisementId(JOB_ADVERTISEMENT_ID);
        createFavouriteItemDto.setNote(NOTE);
        createFavouriteItemDto.setOwnerUserId(USER_ID);
        JobAdvertisement jobAdvertisement = JobAdvertisementFixture.of(JOB_ADVERTISEMENT_ID)
                .setJobContent(
                        JobContentFixture.of(JOB_ADVERTISEMENT_ID)
                                .setLocation(testLocation().setCity("ZurichA").build())
                                .build()).build();

        when(jobAdvertisementRepository.existsById(any())).thenReturn(true);
        when(jobAdvertisementRepository.getOne(any())).thenReturn(jobAdvertisement);

        //when
        FavouriteItemDto favouriteItemDto = this.sut.create(createFavouriteItemDto);

        //then
        Optional<FavouriteItem> createdFavouriteItem = favouriteItemRepository.findById(new FavouriteItemId(favouriteItemDto.getId()));
        assertThat(createdFavouriteItem).isPresent();
        assertThat(createdFavouriteItem.get().getNote()).isEqualTo(createFavouriteItemDto.getNote());
        domainEventMockUtils.assertSingleDomainEventPublished(FavouriteItemEvents.FAVOURITE_ITEM_CREATED.getDomainEventType());

        ArgumentCaptor<BusinessLogEvent> argumentCaptor = ArgumentCaptor.forClass(BusinessLogEvent.class);
        verify(businessLogger, times(1)).log(argumentCaptor.capture());
        BusinessLogEvent businessLogEvent = argumentCaptor.getValue();
        assertThat(businessLogEvent.getEventType()).isEqualTo(JOB_ADVERTISEMENT_FAVORITE_EVENT);
        assertThat(businessLogEvent.getObjectType()).isEqualTo(JOB_ADVERTISEMENT);
        assertThat(businessLogEvent.getObjectId()).isEqualTo(JOB_ADVERTISEMENT_ID.getValue());
        assertThat(businessLogEvent.getAdditionalData().get(OBJECT_TYPE_STATUS)).isEqualTo(jobAdvertisement.getStatus());
        // Testing the user role is not straight forward:
        // the user role is inserted by the LogstashBusinessLogger, based on the Spring context.
        // There's an integration test for this case, please see FavouriteItemRestControllerIntTest
    }

    @Test
    public void testCanNotCreateFavouriteWithNonExistingJobAdId() {
        // given
        CreateFavouriteItemDto createFavouriteItemDto = new CreateFavouriteItemDto();
        createFavouriteItemDto.setJobAdvertisementId(JOB_ADVERTISEMENT_ID);
        createFavouriteItemDto.setNote(NOTE);
        createFavouriteItemDto.setOwnerUserId(USER_ID);

        // when
        when(jobAdvertisementRepository.existsById(JOB_ADVERTISEMENT_ID)).thenReturn(false);

        // then
        assertThatThrownBy(() -> this.sut.create(createFavouriteItemDto))
                .isInstanceOf(AggregateNotFoundException.class)
                .hasMessageContaining("Aggregate with ID " + JOB_ADVERTISEMENT_ID.getValue() +" not found");
    }

    @Test
    public void testCanNotCreateFavouriteThatAlreadyExists() {
        // given
        when(jobAdvertisementRepository.existsById(any())).thenReturn(true);

        createAndSaveToDBFavouriteItem(FAVOURITE_ITEM_ID, JOB_ADVERTISEMENT_ID, NOTE, USER_ID);

        CreateFavouriteItemDto createFavouriteItemDto = new CreateFavouriteItemDto();
        createFavouriteItemDto.setJobAdvertisementId(JOB_ADVERTISEMENT_ID);
        createFavouriteItemDto.setNote("My other note");
        createFavouriteItemDto.setOwnerUserId(USER_ID);

        // then
        assertThatThrownBy(() -> this.sut.create(createFavouriteItemDto))
                .isInstanceOf(FavouriteItemAlreadyExists.class)
                .hasMessageContaining("FavouriteItem couldn't be created. User: 'USER-1' already has a FavouriteItem: 'FAV_ID_1' for JobAdvertismentId: 'JOB_AD_ID_1'");
    }

    @Test
    public void testFindExistingFavouriteItemFindById() {
        // given
        createAndSaveToDBFavouriteItem(FAVOURITE_ITEM_ID, JOB_ADVERTISEMENT_ID, NOTE, USER_ID);

        // when
        FavouriteItemDto favouriteItem = this.sut.findById(FAVOURITE_ITEM_ID);

        // then
        assertThat(favouriteItem.getId()).isEqualTo(FAVOURITE_ITEM_ID.getValue());
    }

    @Test
    public void testFindNonExistingFavouriteItemFindById() {
        // then
        assertThatThrownBy(() -> this.sut.findById(FAVOURITE_ITEM_ID))
                .isInstanceOf(FavoriteItemNotExitsException.class)
                .hasMessageContaining("Aggregate with ID " + FAVOURITE_ITEM_ID.getValue() +" not found");
    }

    @Test
    public void testUpdateExistingFavouriteItem() {
        // given
        String adjustedNote = "My new note";


        createAndSaveToDBFavouriteItem(FAVOURITE_ITEM_ID, JOB_ADVERTISEMENT_ID, NOTE, USER_ID);
        UpdateFavouriteItemDto updateFavouriteItemDto = new UpdateFavouriteItemDto(FAVOURITE_ITEM_ID, adjustedNote);

        // when
        FavouriteItemDto favouriteItemDto = this.sut.update(updateFavouriteItemDto);

        // then
        assertThat(favouriteItemDto.getNote()).isEqualTo(adjustedNote);
    }

    @Test
    public void testDeleteExistingFavouriteItem() {
        // given
        createAndSaveToDBFavouriteItem(FAVOURITE_ITEM_ID, JOB_ADVERTISEMENT_ID, NOTE, USER_ID);

        // when
        assertThat(this.sut.findById(FAVOURITE_ITEM_ID).getJobAdvertisementId()).isEqualTo(JOB_ADVERTISEMENT_ID.getValue());
        this.sut.delete(FAVOURITE_ITEM_ID);
        // then
        assertThatThrownBy(() -> this.sut.findById(FAVOURITE_ITEM_ID))
                .isInstanceOf(FavoriteItemNotExitsException.class)
                .hasMessageContaining("Aggregate with ID " + FAVOURITE_ITEM_ID.getValue() + " not found");
    }

    @Test
    public void testFindByJobAdvertisementIdAndOwnerId() {
        // given
        createAndSaveToDBFavouriteItem(FAVOURITE_ITEM_ID, JOB_ADVERTISEMENT_ID, NOTE, USER_ID);

        // when
        Optional<FavouriteItemDto> optionalFavouriteItemDto = this.sut.findByJobAdvertisementIdAndUserId(JOB_ADVERTISEMENT_ID, USER_ID);

        // then
        assertThat(optionalFavouriteItemDto).isPresent();
    }

    private void createAndSaveToDBFavouriteItem(FavouriteItemId favouriteItemId, JobAdvertisementId jobAdvertisementId, String note, String ownerId) {
        FavouriteItem favouriteItem = new FavouriteItem.Builder()
                .setId(favouriteItemId)
                .setJobAdvertisementId(jobAdvertisementId)
                .setNote(note)
                .setOwnerUserId(ownerId)
                .build();
        this.favouriteItemRepository.save(favouriteItem);
    }
}