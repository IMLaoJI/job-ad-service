package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read;

import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.searchprofile.JobAlertPercolatorEventListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read.fixtures.JobAdvertisementSearchRequestFixture.testJobAdvertisementSearchRequest;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JobAdvertisementSearchQueryBuilderTest {

	@Autowired
	JobAdvertisementSearchQueryBuilder jobAdvertisementSearchQueryBuilder;

	@MockBean
	ElasticsearchIndexService elasticsearchIndexService;

	@MockBean
	JobAlertPercolatorEventListener jobAlertPercolatorEventListener;

	@Test
	public void createRadiusSearchQuery() {
		final NativeSearchQuery nativeSearchQuery = jobAdvertisementSearchQueryBuilder.createSearchQuery(testJobAdvertisementSearchRequest(), PageRequest.of(0, 20, Sort.by(Sort.Order.desc("onlineSince"))));
		assertNotNull(nativeSearchQuery);
	}

}
