package web.controller.fixtures;

import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.webform.WebformCreateAddressDto;

public class WebformCreateAddressDtoFixture {

    public static WebformCreateAddressDto webformCreateAddressDtoForNormalAddress() {
        return webformCreateAddressDtoWithoutNormalAddressAndPOBoxAddress()
                .setStreet("street")
                .setHouseNumber("houseNumber")
                .setPostalCode("postalCode")
                .setCity("city");
    }

    public static WebformCreateAddressDto webformCreateAddressDtoForPOBoxAddress() {
        return webformCreateAddressDtoWithoutNormalAddressAndPOBoxAddress()
                .setPostOfficeBoxNumber("postOfficeBoxNumber")
                .setPostalCode("postalCode")
                .setCity("city");
    }

    public static WebformCreateAddressDto webformCreateAddressDtoForNormalAddressAndPOBoxAddress() {
        return webformCreateAddressDtoWithoutNormalAddressAndPOBoxAddress()
                .setStreet("street")
                .setHouseNumber("houseNumber")
                .setPostOfficeBoxNumber("postOfficeBoxNumber")
                .setPostalCode("postalCode")
                .setCity("city");
    }

    public static WebformCreateAddressDto webformCreateAddressDtoWithoutNormalAddressAndPOBoxAddress() {
        return new WebformCreateAddressDto()
                .setName("name")
                .setCountryIsoCode("CH");
    }
}
