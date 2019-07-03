package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobContent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Owner;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Publication;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.INSPECTING;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures.AvamSinkJobContentFixture.testJobContentBuilder;

public class AvamSinkJobAdFixture {

	public static JobAdvertisement.Builder testJobAd() {
		return new JobAdvertisement.Builder()
				.setId(new JobAdvertisementId("id"))
				.setStatus(INSPECTING)
				.setSourceSystem(SourceSystem.JOBROOM)
				.setJobContent(testJobContentBuilder()
						.build())
				.setOwner(ownerWithAccessToken())
				.setPublication(emptyPublication());
	}

	public static JobAdvertisement.Builder testJobAdWithContent(JobContent jobContent) {
		return new JobAdvertisement.Builder()
				.setId(new JobAdvertisementId("id"))
				.setStatus(INSPECTING)
				.setSourceSystem(SourceSystem.JOBROOM)
				.setJobContent(jobContent)
				.setOwner(ownerWithAccessToken())
				.setPublication(emptyPublication());
	}

	private static Publication emptyPublication() {
		return new Publication.Builder()
				.build();
	}

	private static Owner ownerWithAccessToken() {
		return new Owner.Builder()
				.setAccessToken("accessToken")
				.build();
	}

}