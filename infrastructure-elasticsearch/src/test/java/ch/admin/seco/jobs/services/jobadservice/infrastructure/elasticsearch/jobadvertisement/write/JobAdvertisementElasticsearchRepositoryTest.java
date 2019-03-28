package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write;

import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItem;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemDocument;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemElasticsearchRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.*;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJob;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JobAdvertisementElasticsearchRepositoryTest {

    @Autowired
    private FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository;

    @Autowired
    private JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

    @Autowired
    private ElasticsearchIndexService elasticsearchIndexService;

    @Test
    public void testFindX() {
        // given
        index(createJob(job01.id()));
        index(createJob(job02.id()));
        index(createJob(job04.id()));

        indexChildDocument(createFavouriteItem("child-01", job01.id(), "John"));

        Optional<FavouriteItemDocument> byIdAndParent = this.favouriteItemElasticsearchRepository.findByIdAndParent(job01.id().getValue(), "child-01");
        System.out.println(byIdAndParent);
    }


    private void index(JobAdvertisement jobAdvertisement) {
        this.jobAdvertisementElasticsearchRepository.save(new JobAdvertisementDocument(jobAdvertisement));
    }

    private void indexChildDocument(FavouriteItem favouriteItem) {
        this.elasticsearchIndexService.saveChildWithUpdateRequest(new FavouriteItemDocument(favouriteItem),
                favouriteItem.getJobAdvertisementId().getValue(), INDEX_NAME_JOB_ADVERTISEMENT);
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