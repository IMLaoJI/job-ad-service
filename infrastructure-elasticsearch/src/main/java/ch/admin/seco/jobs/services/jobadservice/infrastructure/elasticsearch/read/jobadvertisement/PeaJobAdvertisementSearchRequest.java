package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.read.jobadvertisement;

public class PeaJobAdvertisementSearchRequest extends ManagedJobAdSearchRequest {

	private String jobTitle;

	public String getJobTitle() {
		return jobTitle;
	}

	public PeaJobAdvertisementSearchRequest setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
		return this;
	}
}
