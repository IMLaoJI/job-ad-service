package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.read.jobadvertisement;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class PeaJobAdvertisementSearchRequest {

	private String jobTitle;

	@Min(7)
	@Max(365)
	private Integer onlineSinceDays;

	@NotBlank
	private String companyName;

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public Integer getOnlineSinceDays() {
		return onlineSinceDays;
	}

	public void setOnlineSinceDays(Integer onlineSinceDays) {
		this.onlineSinceDays = onlineSinceDays;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
}
