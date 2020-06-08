package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures;

import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.webform.WebformCreateCompanyDto;

public class WebformCreateCompanyDtoFixture {

    public static WebformCreateCompanyDto testWebformCreateCompanyDtoWithOnlyFilledAddressFields() {
        return webformCreateCompanyDtoWithAddressAndPOBoxEmpty()
                .setStreet("street")
                .setHouseNumber("houseNumber")
                .setPostalCode("postalCode")
                .setCity("city");
    }

    public static WebformCreateCompanyDto testWebformCreateCompanyDtoWithOnlyFilledPOBoxFields() {
        return webformCreateCompanyDtoWithAddressAndPOBoxEmpty()
                .setPostOfficeBoxNumber("postOfficeBoxNumber")
                .setPostalCode("postalCode")
                .setCity("city");
    }

    public static WebformCreateCompanyDto testWebformCreateCompanyDtoWithBothAddressAndPOBoxFields() {
        return webformCreateCompanyDtoWithAddressAndPOBoxEmpty()
                .setStreet("street")
                .setHouseNumber("houseNumber")
                .setPostOfficeBoxNumber("postOfficeBoxNumber")
                .setPostalCode("postalCode")
                .setCity("city");
    }

    public static WebformCreateCompanyDto webformCreateCompanyDtoWithAddressAndPOBoxEmpty() {
        return new WebformCreateCompanyDto()
                .setName("name")
                .setCountryIsoCode("CH")
                .setSurrogate(false);
    }

}
