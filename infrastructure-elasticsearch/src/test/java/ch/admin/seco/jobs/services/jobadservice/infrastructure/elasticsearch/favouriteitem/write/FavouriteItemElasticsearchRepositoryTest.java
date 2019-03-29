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

        indexChildDocument(createFavouriteItem("child-01", job01.id(), "John"));
        indexChildDocument(createFavouriteItem("child-02", job01.id(), "Paul"));
        indexChildDocument(createFavouriteItem("child-03", job01.id(), "Lisa"));

        indexChildDocument(createFavouriteItem("child-04", job02.id(), "Lisa"));

        await().until(() -> favouriteItemElasticsearchRepository.count() >= 4);

        // then
        assertThat(this.favouriteItemElasticsearchRepository.findById(job01.id().getValue(), "child-01")).isPresent();
        assertThat(this.favouriteItemElasticsearchRepository.findById(job01.id().getValue(), "child-02")).isPresent();
        assertThat(this.favouriteItemElasticsearchRepository.findById(job01.id().getValue(), "child-03")).isPresent();
        assertThat(this.favouriteItemElasticsearchRepository.findById(job01.id().getValue(), "child-04")).isNotPresent();
        assertThat(this.favouriteItemElasticsearchRepository.findById(job02.id().getValue(), "child-04")).isPresent();
    }

    @Test
    public void testFindByOwnerAndParentIds() {
        // given
        index(createJob(job01.id()));
        index(createJob(job02.id()));
        index(createJob(job04.id()));

        indexChildDocument(createFavouriteItem("child-01", job01.id(), "John"));
        indexChildDocument(createFavouriteItem("child-02", job02.id(), "John"));
        indexChildDocument(createFavouriteItem("child-05", job02.id(), "Emma"));
        indexChildDocument(createFavouriteItem("child-06", job04.id(), "John"));
        indexChildDocument(createFavouriteItem("child-07", job01.id(), "Jane"));
        indexChildDocument(createFavouriteItem("child-10", job04.id(), "Paul"));

        await().until(() -> favouriteItemElasticsearchRepository.count() >= 6);

        // then
        assertThat(this.favouriteItemElasticsearchRepository.findByOwnerAndParentIds(
                asList(job04.id().getValue(), job02.id().getValue()),
                "Paul")
        ).hasSize(1);

        assertThat(this.favouriteItemElasticsearchRepository.findByOwnerAndParentIds(
                asList(job01.id().getValue(), job02.id().getValue()),
                "John")
        ).hasSize(2);

        assertThat(this.favouriteItemElasticsearchRepository.findByOwnerAndParentIds(
                asList(job01.id().getValue(), job02.id().getValue(), job04.id().getValue()),
                "John")
        ).hasSize(3);

        assertThat(this.favouriteItemElasticsearchRepository.findByOwnerAndParentIds(
                asList(job01.id().getValue(), job04.id().getValue()),
                "Emma")
        ).hasSize(0);

    }

    @Test
    public void testFindByParentId() {
        // given
        index(createJob(job01.id()));
        index(createJob(job02.id()));
        index(createJob(job03.id()));

        indexChildDocument(createFavouriteItem("child-01", job01.id(), "John"));
        indexChildDocument(createFavouriteItem("child-02", job01.id(), "Emma"));
        indexChildDocument(createFavouriteItem("child-03", job01.id(), "Jane"));

        await().until(() -> favouriteItemElasticsearchRepository.count() >= 3);

        // then
        assertThat(this.favouriteItemElasticsearchRepository.findByParent(job01.id().getValue())).hasSize(3);
        assertThat(this.favouriteItemElasticsearchRepository.findByParent(job02.id().getValue())).hasSize(0);
        assertThat(this.favouriteItemElasticsearchRepository.findByParent(job03.id().getValue())).hasSize(0);
    }

    @Test
    public void testDeleteById() {
        // given
        index(createJob(job01.id()));
        index(createJob(job02.id()));


        indexChildDocument(createFavouriteItem("child-01", job01.id(), "John"));
        indexChildDocument(createFavouriteItem("child-02", job01.id(), "Emma"));

        indexChildDocument(createFavouriteItem("child-03", job02.id(), "Jane"));

        await().until(() -> favouriteItemElasticsearchRepository.count() == 3);

        // when
        favouriteItemElasticsearchRepository.deleteById(job01.id().getValue(), "child-01");

        // then
        await().until(() -> favouriteItemElasticsearchRepository.count() == 2);
    }

    @Test
    public void testDeleteByParentId() {
        // given
        index(createJob(job01.id()));
        index(createJob(job02.id()));

        indexChildDocument(createFavouriteItem("child-01", job01.id(), "John"));
        indexChildDocument(createFavouriteItem("child-02", job01.id(), "Emma"));

        indexChildDocument(createFavouriteItem("child-03", job02.id(), "Jane"));

        await().until(() -> favouriteItemElasticsearchRepository.count() >= 3);

        // when
        favouriteItemElasticsearchRepository.deleteByParentId(job01.id().getValue());

        // then
        await().until(() -> favouriteItemElasticsearchRepository.count() == 1);
    }

    @Test
    public void testFindByOwnerId() {
        // given
        index(createJob(job01.id()));
        index(createJob(job02.id()));
        index(createJob(job03.id()));
        index(createJob(job04.id()));
        index(createJob(job05.id()));


        indexChildDocument(createFavouriteItem("child-01", job01.id(), "John"));
        indexChildDocument(createFavouriteItem("child-02", job01.id(), "Emma"));
        indexChildDocument(createFavouriteItem("child-03", job02.id(), "John"));
        indexChildDocument(createFavouriteItem("child-04", job02.id(), "Jane"));

        await().until(() -> favouriteItemElasticsearchRepository.count() >= 4);

        assertThat(this.favouriteItemElasticsearchRepository.findByOwnerId("John")).hasSize(2);

    }


    private void index(JobAdvertisement jobAdvertisement) {
        this.jobAdvertisementElasticsearchRepository.save(new JobAdvertisementDocument(jobAdvertisement));
    }

    private void indexChildDocument(FavouriteItem favouriteItem) {
        this.favouriteItemElasticsearchRepository.save(new FavouriteItemDocument(favouriteItem));
    }

    private FavouriteItem createFavouriteItem(String favouriteItemId, JobAdvertisementId id, String ownerId) {
        return new FavouriteItem.Builder()
                .setId(new FavouriteItemId(favouriteItemId))
                .setJobAdvertisementId(id)
                .setOwnerId(ownerId)
                .setNote("Favourite Item Note")
                .build();
    }
}