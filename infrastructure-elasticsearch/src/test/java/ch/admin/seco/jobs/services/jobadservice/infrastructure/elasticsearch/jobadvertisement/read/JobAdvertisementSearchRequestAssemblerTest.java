package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read;

import ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto.ResolvedSearchProfileDto;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.searchprofile.JobAlertPercolatorEventListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read.fixtures.ResolvedSearchProfileDtoFixture.createResolvedSearchProfileDtoFixture;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read.fixtures.ResolvedSearchProfileDtoFixture.createResolvedSearchProfileDtoFixtureWithOccupationCodes;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JobAdvertisementSearchRequestAssemblerTest {

	@Autowired
	JobAdvertisementSearchRequestAssembler jobAdvertisementSearchRequestAssembler;

	@MockBean
	ElasticsearchIndexService elasticsearchIndexService;

	@MockBean
	JobAlertPercolatorEventListener jobAlertPercolatorEventListener;

	@Test
	public void testAssemblingToSearchQuery() {
		final ResolvedSearchProfileDto resolvedSearchProfileDtoFixture = createResolvedSearchProfileDtoFixture();
		final NativeSearchQuery nativeSearchQuery = jobAdvertisementSearchRequestAssembler.toJobAdvertisementSearchRequest(resolvedSearchProfileDtoFixture);
		assertNotNull(nativeSearchQuery);
	}

	@Test
	public void testAssemblingToSearchQueryWithProfessionCodes() {
		final ResolvedSearchProfileDto resolvedSearchProfileDtoFixture = createResolvedSearchProfileDtoFixtureWithOccupationCodes();
		final NativeSearchQuery nativeSearchQuery = jobAdvertisementSearchRequestAssembler.toJobAdvertisementSearchRequest(resolvedSearchProfileDtoFixture);
		assertNotNull(nativeSearchQuery);
	}


}
