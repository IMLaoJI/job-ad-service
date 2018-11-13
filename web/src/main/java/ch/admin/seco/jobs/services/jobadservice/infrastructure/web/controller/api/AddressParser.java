package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.AddressDto;

import static org.springframework.util.StringUtils.hasText;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressParser {

    private static final Pattern ADDRESS_PATTERN = Pattern.compile("(.*)[,][ ]*(.*?)[,][ ]*(\\d{4})[ ]+(\\p{Alpha}.*)");
    private static final Pattern ADDRESS_PATTERN_SHORT = Pattern.compile("(.*)[,][ ]*(\\d{4})[ ]+(\\p{Alpha}.*)");

    private static final Logger LOG = LoggerFactory.getLogger(AddressParser.class);

    /*
     * This simplified parser assumes only Swiss street addresses. PO Box addresses are treated like street addresses.
     * If the address is incomplete, the business name can be supplemented as address name.
     */
    public static AddressDto parse(String rawAddress, String companyName) {
        if (!hasText(rawAddress)) {
            return null;
        }
        Matcher m = ADDRESS_PATTERN.matcher(rawAddress);
        AddressDto address = new AddressDto();
        address.setCountryIsoCode("CH");
        if (m.find()) {
            address.setName(m.group(1).trim()).setStreet(m.group(2).trim()).setPostalCode(m.group(3)).setCity(m.group(4).trim());
        } else {
            m = ADDRESS_PATTERN_SHORT.matcher(rawAddress);
            if (m.find()) {
                // The address lacks either the company name or street address
                address.setName(m.group(1).trim()).setStreet(m.group(1).trim()).setPostalCode(m.group(2)).setCity(m.group(3).trim());
                if (address.getName().equalsIgnoreCase(companyName)) {
                    // setStreet?
                } else {
                    address.setName(companyName);
                }
            } else {
                LOG.warn("Unable to parse apply channel address: " + rawAddress);
                return null;
            }
        }
        return address;
    }

}
