package ch.admin.seco.jobs.services.jobadservice.application.searchprofile.dto;

import ch.admin.seco.jobs.services.jobadservice.domain.searchprofile.jobalert.Interval;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class JobAlertDto {

	@NotNull
	private Interval interval;

	@NotNull
	@NotBlank
	private String email;

	public Interval getInterval() {
		return interval;
	}

	public JobAlertDto setInterval(Interval interval) {
		this.interval = interval;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public JobAlertDto setEmail(String email) {
		this.email = email;
		return this;
	}

}
