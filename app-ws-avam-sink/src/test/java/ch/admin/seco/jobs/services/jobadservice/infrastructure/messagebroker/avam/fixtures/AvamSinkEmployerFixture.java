package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Employer;

public class AvamSinkEmployerFixture {

	public static Employer testEmployer(){
		return new Employer.Builder()
				.setName("employerName")
				.setPostalCode("employerPostalCode")
				.setCity("employerCity")
				.setCountryIsoCode("ch")
				.build();
	}

}
