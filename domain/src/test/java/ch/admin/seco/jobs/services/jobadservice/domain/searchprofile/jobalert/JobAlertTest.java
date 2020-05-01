package ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture;
import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.JobAlert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.fixture.JobAlertFixture.testJobAlert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JobAlertTest {

	private static final List<JobAdvertisementId> JOB_AD_IDS = Arrays.asList(
			JobAdvertisementIdFixture.job01.id(),
			JobAdvertisementIdFixture.job02.id(),
			JobAdvertisementIdFixture.job03.id(),
			JobAdvertisementIdFixture.job04.id(),
			JobAdvertisementIdFixture.job05.id(),
			JobAdvertisementIdFixture.job06.id(),
			JobAdvertisementIdFixture.job07.id(),
			JobAdvertisementIdFixture.job08.id(),
			JobAdvertisementIdFixture.job09.id(),
			JobAdvertisementIdFixture.job10.id(),
		    new JobAdvertisementId("job11"),
		    new JobAdvertisementId("job12"),
		    new JobAdvertisementId("job13")
	);

	@Test
	public void addSingleEntry() {
		//given
		final JobAlert jobAlert = testJobAlert();
		final JobAdvertisementId jobadId = new JobAdvertisementId("jobadId");

		//when
		jobAlert.add(jobadId);
		assertTrue(jobAlert.getMatchedJobAdvertisementIds().contains(jobadId));

		//then
		assertEquals(1, jobAlert.getMatchedJobAdvertisementIdsForRelease().size());
		assertTrue(jobAlert.getMatchedJobAdvertisementIdsForRelease().contains(jobadId));
	}


	@Test
	public void add11EntriesToMatchedIdsAndAssertEldestEntriesAreRemoved() {
		//given
		final JobAlert jobAlert = testJobAlert();
		final JobAdvertisementId jobAdIdToBeRemoved1 = new JobAdvertisementId("JobAdIdToBeRemoved1");

		jobAlert.add(jobAdIdToBeRemoved1);
		assertTrue(jobAlert.getMatchedJobAdvertisementIds().contains(jobAdIdToBeRemoved1));

		//when
		JOB_AD_IDS.forEach(jobAlert::add);

		//then
		assertEquals(10, jobAlert.getMatchedJobAdvertisementIdsForRelease().size());
		assertFalse(jobAlert.getMatchedJobAdvertisementIds().contains(jobAdIdToBeRemoved1));
	}

	@Test
	public void add12EntriesToMatchedIdsAndAssertEldestEntriesAreRemoved() {
		//given
		final JobAlert jobAlert = testJobAlert();
		final JobAdvertisementId jobAdIdToBeRemoved1 = new JobAdvertisementId("JobAdIdToBeRemoved1");
		final JobAdvertisementId jobAdIdToBeRemoved2 = new JobAdvertisementId("JobAdIdToBeRemoved2");

		jobAlert.add(jobAdIdToBeRemoved1);
		jobAlert.add(jobAdIdToBeRemoved2);
		assertTrue(jobAlert.getMatchedJobAdvertisementIds().contains(jobAdIdToBeRemoved1));
		assertTrue(jobAlert.getMatchedJobAdvertisementIds().contains(jobAdIdToBeRemoved2));

		//when
		JOB_AD_IDS.forEach(jobAlert::add);

		//then
		assertEquals(10, jobAlert.getMatchedJobAdvertisementIdsForRelease().size());
		assertFalse(jobAlert.getMatchedJobAdvertisementIds().contains(jobAdIdToBeRemoved1));
		assertFalse(jobAlert.getMatchedJobAdvertisementIds().contains(jobAdIdToBeRemoved2));
	}

	@Test
	public void add13EntriesToMatchedIdsAndAssertEldestEntriesAreRemoved() {
		//given
		final JobAlert jobAlert = testJobAlert();
		final JobAdvertisementId jobAdIdToBeRemoved1 = new JobAdvertisementId("JobAdIdToBeRemoved1");
		final JobAdvertisementId jobAdIdToBeRemoved2 = new JobAdvertisementId("JobAdIdToBeRemoved2");
		final JobAdvertisementId jobAdIdToBeRemoved3 = new JobAdvertisementId("JobAdIdToBeRemoved3");

		jobAlert.add(jobAdIdToBeRemoved1);
		jobAlert.add(jobAdIdToBeRemoved2);
		jobAlert.add(jobAdIdToBeRemoved3);
		assertTrue(jobAlert.getMatchedJobAdvertisementIdsForRelease().contains(jobAdIdToBeRemoved1));
		assertTrue(jobAlert.getMatchedJobAdvertisementIdsForRelease().contains(jobAdIdToBeRemoved2));
		assertTrue(jobAlert.getMatchedJobAdvertisementIdsForRelease().contains(jobAdIdToBeRemoved3));

		//when
		JOB_AD_IDS.forEach(jobAlert::add);

		//then
		assertEquals(10, jobAlert.getMatchedJobAdvertisementIdsForRelease().size());
		assertFalse(jobAlert.getMatchedJobAdvertisementIdsForRelease().contains(jobAdIdToBeRemoved1));
		assertFalse(jobAlert.getMatchedJobAdvertisementIdsForRelease().contains(jobAdIdToBeRemoved2));
		assertFalse(jobAlert.getMatchedJobAdvertisementIdsForRelease().contains(jobAdIdToBeRemoved3));
	}

}
