package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Employment;

import java.time.LocalDate;

import static ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine.now;

public class AvamSinkEmploymentFixture {

	public static Employment permanentEmployment() {
		return new Employment.Builder()
				.setPermanent(true)
				.setWorkloadPercentageMax(100)
				.setWorkloadPercentageMin(80)
				.build();
	}

	public static Employment fixedTermEmployment() {
		return new Employment.Builder()
				.setPermanent(false)
				.setShortEmployment(false)
				.setEndDate(LocalDate.from(now().plusDays(14)))
				.setWorkloadPercentageMax(100)
				.setWorkloadPercentageMin(80)
				.build();
	}

	public static Employment shortTermEmployment() {
		return new Employment.Builder()
				.setShortEmployment(true)
				.setWorkloadPercentageMax(100)
				.setWorkloadPercentageMin(80)
				.build();
	}
}
