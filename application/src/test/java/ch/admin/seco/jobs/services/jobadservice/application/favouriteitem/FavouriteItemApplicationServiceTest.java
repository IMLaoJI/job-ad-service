package ch.admin.seco.jobs.services.jobadservice.application.favouriteitem;

import ch.admin.seco.alv.shared.logger.business.BusinessLogData;
import ch.admin.seco.alv.shared.logger.business.BusinessLogger;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.FavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.create.CreateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.update.UpdateFavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.core.domain.AggregateNotFoundException;
import ch.admin.seco.jobs.services.jobadservice.core.domain.events.DomainEventMockUtils;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.events.FavouriteItemEvents;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.FavouriteItemIdFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture;
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

import static ch.admin.seco.jobs.services.jobadservice.application.common.logging.BusinessLogConstants.STATUS_ADDITIONAL_DATA;
import static ch.admin.seco.jobs.services.jobadservice.application.common.logging.BusinessLogEventType.JOB_ADVERTISEMENT_FAVORITE_EVENT;
import static ch.admin.seco.jobs.services.jobadservice.application.common.logging.BusinessLogObjectType.JOB_ADVERTISEMENT_LOG;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.FavouriteItemIdFixture.FAV_ID_5;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.FavouriteItemIdFixture.UNKNOWN;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.LocationFixture.testLocation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class FavouriteItemApplicationServiceTest {
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
        final JobAdvertisementId jobAdId = JobAdvertisementIdFixture.job01.id();

        CreateFavouriteItemDto createFavouriteItemDto = new CreateFavouriteItemDto();
        createFavouriteItemDto.setJobAdvertisementId(jobAdId);
        createFavouriteItemDto.setNote(NOTE);
        createFavouriteItemDto.setOwnerUserId(USER_ID);
        JobAdvertisement jobAdvertisement = JobAdvertisementFixture.of(jobAdId)
                .setJobContent(
                        JobContentFixture.of(jobAdId)
                                .setLocation(testLocation().setCity("ZurichA").build())
                                .build()).build();

        when(jobAdvertisementRepository.existsById(any())).thenReturn(true);
        when(jobAdvertisementRepository.findById(eq(jobAdId))).thenReturn(Optional.of(jobAdvertisement));

        //when
        FavouriteItemDto favouriteItemDto = this.sut.create(createFavouriteItemDto);

        //then
        Optional<FavouriteItem> createdFavouriteItem = favouriteItemRepository.findById(new FavouriteItemId(favouriteItemDto.getId()));
        assertThat(createdFavouriteItem).isPresent();
        assertThat(createdFavouriteItem.get().getNote()).isEqualTo(createFavouriteItemDto.getNote());
        domainEventMockUtils.assertSingleDomainEventPublished(FavouriteItemEvents.FAVOURITE_ITEM_CREATED.getDomainEventType());

        ArgumentCaptor<BusinessLogData> argumentCaptor = ArgumentCaptor.forClass(BusinessLogData.class);
        verify(businessLogger, times(1)).log(argumentCaptor.capture());
        BusinessLogData businessLogData = argumentCaptor.getValue();
        assertThat(businessLogData.getEventType()).isEqualTo(JOB_ADVERTISEMENT_FAVORITE_EVENT.enumName());
        assertThat(businessLogData.getObjectType()).isEqualTo(JOB_ADVERTISEMENT_LOG.enumName());
        assertThat(businessLogData.getObjectId()).isEqualTo(jobAdId.getValue());
        assertThat(businessLogData.getAdditionalData().get(STATUS_ADDITIONAL_DATA)).isEqualTo(jobAdvertisement.getStatus());
        // Testing the user role is not straight forward:
        // the user role is inserted by the LogstashBusinessLogger, based on the Spring context.
        // There's an integration test for this case, please see FavouriteItemRestControllerIntTest
    }

    @Test
    public void testCanNotCreateFavouriteWithNonExistingJobAdId() {
        // given
        final JobAdvertisementId jobAdId = JobAdvertisementIdFixture.job02.id();
        CreateFavouriteItemDto createFavouriteItemDto = new CreateFavouriteItemDto();
        createFavouriteItemDto.setJobAdvertisementId(jobAdId);
        createFavouriteItemDto.setNote(NOTE);
        createFavouriteItemDto.setOwnerUserId(USER_ID);

        // when
        when(jobAdvertisementRepository.existsById(jobAdId)).thenReturn(false);

        // then
        assertThatThrownBy(() -> this.sut.create(createFavouriteItemDto))
                .isInstanceOf(AggregateNotFoundException.class)
                .hasMessageContaining("Aggregate with ID " + jobAdId.getValue() +" not found");
    }

    @Test
    public void testCanNotCreateFavouriteThatAlreadyExists() {
        // given
        final JobAdvertisementId jobAdId = JobAdvertisementIdFixture.job03.id();
        when(jobAdvertisementRepository.existsById(any())).thenReturn(true);

        createAndSaveToDBFavouriteItem(FavouriteItemIdFixture.FAV_ID_1.id(), jobAdId, NOTE, USER_ID);

        CreateFavouriteItemDto createFavouriteItemDto = new CreateFavouriteItemDto();
        createFavouriteItemDto.setJobAdvertisementId(jobAdId);
        createFavouriteItemDto.setNote("My other note");
        createFavouriteItemDto.setOwnerUserId(USER_ID);

        // then
        assertThatThrownBy(() -> this.sut.create(createFavouriteItemDto))
                .isInstanceOf(FavouriteItemAlreadyExists.class)
                .hasMessageContaining("FavouriteItem couldn't be created. User: 'USER-1' already has a FavouriteItem: 'FAV_ID_1' for JobAdvertismentId: '"
                        + jobAdId.getValue()+ "'");
    }

    @Test
    public void testFindExistingFavouriteItemFindById() {
        // given
        final FavouriteItemId favouriteItemId = FavouriteItemIdFixture.FAV_ID_2.id();
        createAndSaveToDBFavouriteItem(favouriteItemId, JobAdvertisementIdFixture.job04.id(), NOTE, USER_ID);

        // when
        FavouriteItemDto favouriteItem = this.sut.findById(favouriteItemId);

        // then
        assertThat(favouriteItem.getId()).isEqualTo(favouriteItemId.getValue());
    }

    @Test
    public void testFindNonExistingFavouriteItemFindById() {
        // then
        assertThatThrownBy(() -> this.sut.findById(UNKNOWN.id()))
                .isInstanceOf(FavoriteItemNotExitsException.class)
                .hasMessageContaining("Aggregate with ID " + UNKNOWN.id().getValue() +" not found");
    }

    @Test
    public void testUpdateExistingFavouriteItem() {
        // given
        String adjustedNote = "My new note";
        final FavouriteItemId favouriteItemId = FavouriteItemIdFixture.FAV_ID_3.id();

        createAndSaveToDBFavouriteItem(favouriteItemId, JobAdvertisementIdFixture.job05.id(), NOTE, USER_ID);
        UpdateFavouriteItemDto updateFavouriteItemDto = new UpdateFavouriteItemDto(favouriteItemId, adjustedNote);

        // when
        FavouriteItemDto favouriteItemDto = this.sut.update(updateFavouriteItemDto);

        // then
        assertThat(favouriteItemDto.getNote()).isEqualTo(adjustedNote);
    }

    @Test
    public void testDeleteExistingFavouriteItem() {
        // given
        final JobAdvertisementId jobAdId = JobAdvertisementIdFixture.job06.id();
        final FavouriteItemId favouriteItemId = FavouriteItemIdFixture.FAV_ID_4.id();
        createAndSaveToDBFavouriteItem(favouriteItemId, jobAdId, NOTE, USER_ID);

        // when
        assertThat(this.sut.findById(favouriteItemId).getJobAdvertisementId()).isEqualTo(jobAdId.getValue());
        this.sut.delete(favouriteItemId);
        // then
        assertThatThrownBy(() -> this.sut.findById(favouriteItemId))
                .isInstanceOf(FavoriteItemNotExitsException.class)
                .hasMessageContaining("Aggregate with ID " + favouriteItemId.getValue() + " not found");
    }

    @Test
    public void testFindByJobAdvertisementIdAndOwnerId() {
        // given
        final JobAdvertisementId jobAdId = JobAdvertisementIdFixture.job07.id();
        createAndSaveToDBFavouriteItem(FAV_ID_5.id(), jobAdId, NOTE, USER_ID);

        // when
        Optional<FavouriteItemDto> optionalFavouriteItemDto = this.sut.findByJobAdvertisementIdAndUserId(jobAdId, USER_ID);

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