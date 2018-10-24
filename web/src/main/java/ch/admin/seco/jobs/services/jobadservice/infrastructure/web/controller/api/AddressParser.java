package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.AddressDto;

import static org.springframework.util.StringUtils.hasText;

public class AddressParser {

    public static AddressDto parse(String rawAddress) {
        if (!hasText(rawAddress)) {
            return null;
        }

        // FIXME parse the raw address to an address object!
        return null;
    }

}
