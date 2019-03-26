package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Address;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.JobCenter;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.ApplyChannel.Builder;

public class ApplyChannelFixture {

    public static Builder of(JobAdvertisementId id) {
        return testApplyChannel();
    }

    public static Builder testApplyChannelEmpty() {
        return new Builder();
    }

    public static Builder testApplyChannel() {
        return testApplyChannelEmpty()
                .setRawPostAddress("rawPostAddress")
                .setPostAddress(new Address.Builder()
                        .setName("postAddressName")
                        .setStreet("postAddressStreet")
                        .setHouseNumber("123")
                        .setPostalCode("3000")
                        .setCity("postAddressCity")
                        .setPostOfficeBoxNumber("200")
                        .setPostOfficeBoxPostalCode("3000")
                        .setPostOfficeBoxCity("postAddressPostOfficeBoxCity")
                        .setCountryIsoCode("CH")
                        .build())
                .setEmailAddress("emailAddress")
                .setPhoneNumber("phoneNumber")
                .setFormUrl("formUrl")
                .setAdditionalInfo("additionalInfo");
    }

    public static Builder testDisplayApplyChannel(JobCenter jobCenter) {
        return new Builder(jobCenter);
    }
}
