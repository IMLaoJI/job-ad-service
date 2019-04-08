package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemIdFixture.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJob;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FavouriteItemElasticsearchRepositoryTest {

    @Autowired
    private FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository;

    @Autowired
    private JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

    @Before
    public void setUp() {
        deleteAllParentAndChildren();
    }

    private void deleteAllParentAndChildren() {
        this.favouriteItemElasticsearchRepository.deleteAll();
        this.jobAdvertisementElasticsearchRepository.deleteAll();
    }

    @Test
    public void testFindByIdAndParent() {
        // given
        index(createJob(job01.id()));
        index(createJob(job02.id()));
        index(createJob(job04.id()));

        indexChildDocument(createFavItem(favItem01.id(), job01.id(), "John"));
        indexChildDocument(createFavItem(favItem02.id(), job01.id(), "Paul"));
        indexChildDocument(createFavItem(favItem03.id(), job01.id(), "Lisa"));

        indexChildDocument(createFavItem(favItem04.id(), job02.id(), "Lisa"));

        await().until(() -> favouriteItemElasticsearchRepository.count() >= 4);

        // then
        assertThat(this.favouriteItemElasticsearchRepository.findById(job01.id(), favItem01.id())).isPresent();
        assertThat(this.favouriteItemElasticsearchRepository.findById(job01.id(), favItem02.id())).isPresent();
        assertThat(this.favouriteItemElasticsearchRepository.findById(job01.id(), favItem03.id())).isPresent();
        assertThat(this.favouriteItemElasticsearchRepository.findById(job01.id(), favItem04.id())).isNotPresent();
        assertThat(this.favouriteItemElasticsearchRepository.findById(job02.id(), favItem04.id())).isPresent();
    }

    @Test
    public void testFindByOwnerAndParentIds() {
        // given
        index(createJob(job01.id()));
        index(createJob(job02.id()));
        index(createJob(job04.id()));

        indexChildDocument(createFavItem(favItem01.id(), job01.id(), "John"));
        indexChildDocument(createFavItem(favItem02.id(), job02.id(), "John"));
        indexChildDocument(createFavItem(favItem03.id(), job02.id(), "Emma"));
        indexChildDocument(createFavItem(favItem04.id(), job04.id(), "John"));
        indexChildDocument(createFavItem(favItem05.id(), job01.id(), "Jane"));
        indexChildDocument(createFavItem(favItem06.id(), job04.id(), "Paul"));

        await().until(() -> favouriteItemElasticsearchRepository.count() >= 6);

        // then
        assertThat(this.favouriteItemElasticsearchRepository.findByOwnerAndParentIds(
                asList(job04.id(), job02.id()),
                "Paul")
        ).hasSize(1);

        assertThat(this.favouriteItemElasticsearchRepository.findByOwnerAndParentIds(
                asList(job01.id(), job02.id()),
                "John")
        ).hasSize(2);

        assertThat(this.favouriteItemElasticsearchRepository.findByOwnerAndParentIds(
                asList(job01.id(), job02.id(), job04.id()),
                "John")
        ).hasSize(3);

        assertThat(this.favouriteItemElasticsearchRepository.findByOwnerAndParentIds(
                asList(job01.id(), job04.id()),
                "Emma")
        ).hasSize(0);

    }

    @Test
    public void testFindByParentId() {
        // given
        index(createJob(job01.id()));
        index(createJob(job02.id()));
        index(createJob(job03.id()));

        indexChildDocument(createFavItem(favItem01.id(), job01.id(), "John"));
        indexChildDocument(createFavItem(favItem02.id(), job01.id(), "Emma"));
        indexChildDocument(createFavItem(favItem03.id(), job01.id(), "Jane"));

        await().until(() -> favouriteItemElasticsearchRepository.count() >= 3);

        // then
        assertThat(this.favouriteItemElasticsearchRepository.findByParent(job01.id())).hasSize(3);
        assertThat(this.favouriteItemElasticsearchRepository.findByParent(job02.id())).hasSize(0);
        assertThat(this.favouriteItemElasticsearchRepository.findByParent(job03.id())).hasSize(0);
    }

    @Test
    public void testDeleteById() {
        // given
        index(createJob(job01.id()));
        index(createJob(job02.id()));


        indexChildDocument(createFavItem(favItem01.id(), job01.id(), "John"));
        indexChildDocument(createFavItem(favItem02.id(), job01.id(), "Emma"));

        indexChildDocument(createFavItem(favItem03.id(), job02.id(), "Jane"));

        await().until(() -> favouriteItemElasticsearchRepository.count() == 3);

        // when
        favouriteItemElasticsearchRepository.deleteById(job01.id(), favItem01.id());

        // then
        await().until(() -> favouriteItemElasticsearchRepository.count() == 2);
    }

    @Test
    public void testDeleteByParentId() {
        // given
        index(createJob(job01.id()));
        index(createJob(job02.id()));

        indexChildDocument(createFavItem(favItem01.id(), job01.id(), "John"));
        indexChildDocument(createFavItem(favItem02.id(), job01.id(), "Emma"));

        indexChildDocument(createFavItem(favItem03.id(), job02.id(), "Jane"));

        await().until(() -> favouriteItemElasticsearchRepository.count() >= 3);

        // when
        favouriteItemElasticsearchRepository.deleteByParentId(job01.id());

        // then
        await().until(() -> favouriteItemElasticsearchRepository.count() == 1);
    }

    private void index(JobAdvertisement jobAdvertisement) {
        this.jobAdvertisementElasticsearchRepository.save(new JobAdvertisementDocument(jobAdvertisement));
    }

    private void indexChildDocument(FavouriteItem favouriteItem) {
        this.favouriteItemElasticsearchRepository.save(new FavouriteItemDocument(favouriteItem));
    }

    private FavouriteItem createFavItem(FavouriteItemId favouriteItemId, JobAdvertisementId id, String ownerUserId) {
        return new FavouriteItem.Builder()
                .setId(favouriteItemId)
                .setJobAdvertisementId(id)
                .setOwnerUserId(ownerUserId)
                .setNote("Favourite Item Note")
                .build();
    }
}