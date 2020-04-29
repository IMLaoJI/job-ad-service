package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Company.Builder;
import static java.lang.String.format;

public class CompanyFixture {

    public static Builder of(JobAdvertisementId id){
        return testCompany()
                .setName(format("name-%s", id.getValue()));
    }

    public static Builder testCompanyEmpty() {
        return new Builder();
    }

    public static Builder testCompany() {
        return testCompanyEmpty()
                .setName("name")
                .setStreet("street")
                .setPostalCode("postalCode")
                .setCity("city")
                .setCountryIsoCode("CH");
    }

    public static Builder testDisplayCompany(JobCenter jobCenter) {
        return new Builder(jobCenter);
    }
}
