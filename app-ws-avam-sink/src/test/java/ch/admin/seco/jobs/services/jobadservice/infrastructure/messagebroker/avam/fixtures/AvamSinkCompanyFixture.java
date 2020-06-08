package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.avam.fixtures;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Company;

public class AvamSinkCompanyFixture {

	public static Company testCompany() {
		return new Company.Builder<>()
				.setName("companyName")
				.setStreet("companyStreet")
				.setPostalCode("companyPostalCode")
				.setCity("companyCity")
				.setCountryIsoCode("ch")
				.setSurrogate(false)
				.build();
	}

	public static Company testCompanyWithSurrogate() {
		return new Company.Builder<>()
				.setName("companyName")
				.setStreet("companyStreet")
				.setPostalCode("companyPostalCode")
				.setCity("companyCity")
				.setCountryIsoCode("ch")
				.setSurrogate(true)
				.build();
	}
}
