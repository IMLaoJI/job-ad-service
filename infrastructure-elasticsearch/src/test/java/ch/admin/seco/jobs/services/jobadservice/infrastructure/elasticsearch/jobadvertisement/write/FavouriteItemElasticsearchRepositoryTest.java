package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemElasticsearchRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJob;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FavouriteItemElasticsearchRepositoryTest {

    @Autowired
    private FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository;

    @Autowired
    private JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

    @Test
    public void testFindByIdAndParent() throws InterruptedException {
        // given
        index(createJob(job01.id()));
        index(createJob(job02.id()));
        index(createJob(job04.id()));

        indexChildDocument(createFavouriteItem("child01", job01.id(), "John"));
        indexChildDocument(createFavouriteItem("child02", job01.id(), "Paul"));
        indexChildDocument(createFavouriteItem("child03", job01.id(), "Lisa"));

        indexChildDocument(createFavouriteItem("child04", job02.id(), "Lisa"));

        // TODO replace with awaitility
        Thread.sleep(1000);

        // then
        assertThat(this.favouriteItemElasticsearchRepository.findByIdAndParent(job01.id().getValue(), "child01")).isPresent();
        assertThat(this.favouriteItemElasticsearchRepository.findByIdAndParent(job01.id().getValue(), "child02")).isPresent();
        assertThat(this.favouriteItemElasticsearchRepository.findByIdAndParent(job01.id().getValue(), "child03")).isPresent();
        assertThat(this.favouriteItemElasticsearchRepository.findByIdAndParent(job01.id().getValue(), "child04")).isNotPresent();
        assertThat(this.favouriteItemElasticsearchRepository.findByIdAndParent(job02.id().getValue(), "child04")).isPresent();
    }

    private void index(JobAdvertisement jobAdvertisement) {
        this.jobAdvertisementElasticsearchRepository.save(new JobAdvertisementDocument(jobAdvertisement));
    }

    private void indexChildDocument(FavouriteItem favouriteItem) {
        this.favouriteItemElasticsearchRepository.customSave(new FavouriteItemDocument(favouriteItem));
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