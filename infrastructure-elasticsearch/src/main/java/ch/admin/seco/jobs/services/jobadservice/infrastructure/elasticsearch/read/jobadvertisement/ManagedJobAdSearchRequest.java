package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.read.jobadvertisement;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class ManagedJobAdSearchRequest {

	private String searchString;

	@Min(7)
	@Max(365)
	private Integer onlineSinceDays;

	private String ownerUserId;

	@NotBlank
	private String companyId;

	public String getSearchString() {
		return searchString;
	}

	public ManagedJobAdSearchRequest setSearchString(String searchString) {
		this.searchString = searchString;
		return this;
	}

	public Integer getOnlineSinceDays() {
		return onlineSinceDays;
	}

	public ManagedJobAdSearchRequest setOnlineSinceDays(Integer onlineSinceDays) {
		this.onlineSinceDays = onlineSinceDays;
		return this;
	}

	public String getOwnerUserId() {
		return ownerUserId;
	}

	public ManagedJobAdSearchRequest setOwnerUserId(String ownerUserId) {
		this.ownerUserId = ownerUserId;
		return this;
	}

	public String getCompanyId() {
		return companyId;
	}

	public ManagedJobAdSearchRequest setCompanyId(String companyId) {
		this.companyId = companyId;
		return this;
	}
}
