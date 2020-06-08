package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures;

import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.webform.WebformCreateApplyChannelDto;

public class WebformCreateApplyChannelDtoFixture {

    public static WebformCreateApplyChannelDto webformCreateApplyChannelDtoEmpty() {
        return new WebformCreateApplyChannelDto();
    }

    public static WebformCreateApplyChannelDto webformCreateApplyChannelDtoWithPostAddressEmpty() {
        return new WebformCreateApplyChannelDto()
                .setEmailAddress("emailAddress")
                .setPhoneNumber("phoneNumber")
                .setFormUrl("formUrl")
                .setAdditionalInfo("additionalInfo");
    }

}
