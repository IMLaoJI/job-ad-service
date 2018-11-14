package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.AddressDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressParser {

	private static final Pattern ADDRESS_PATTERN = Pattern.compile("(.*)[,][ ]*([A-Z]{2}-)?(\\d{4,5})[ ]+(.*)");
	private static final Pattern ADDRESSLINE_PATTERN = Pattern.compile("(.*)[,][ ]*(.*)");
	private static final Pattern POBOX_PATTERN = Pattern.compile("(Postfach|Case postale|PO Box|Casella postale)[ ]+(\\d+)");
	private static final Pattern STREET_PATTERN = Pattern.compile("(.*?)[ ]+(\\d.*+)");

	private static final Logger LOG = LoggerFactory.getLogger(AddressParser.class);

	/*
	 * This simplified parser treats addresses as Swiss addresses unless a country code is given (E.g. DE-80120).
	 * If the address is incomplete, the business name can be supplemented as address name.
	 */
	public static AddressDto parse(String rawAddress, String companyName) {
		if (rawAddress == null || rawAddress.isEmpty()) {
			return null;
		}

		// First we check whether this is a valid address
		Matcher m = ADDRESS_PATTERN.matcher(rawAddress.trim().replace('\n', ','));
		if (!m.find()) {
			LOG.info("Unable to parse address: '" + rawAddress + "'");
			return null;
		}

		AddressDto address = new AddressDto();
		String addr = m.group(1).trim();
		address.setCountryIsoCode(m.group(2) == null ? "CH" : m.group(2).substring(0, 2));
		String postalCode = m.group(3);
		String city = m.group(4);

		// Now we check whether the address is complete
		String streetOrPoBox = "";
		m = ADDRESSLINE_PATTERN.matcher(addr);
		if (m.find()) {
			address.setName(m.group(1).trim());
			streetOrPoBox = m.group(2);
		} else {
			address.setName(companyName);
			if (!addr.equalsIgnoreCase(companyName)) {
				streetOrPoBox = addr;
			}
		}

		// Now check for PO Box and Street number
		m = POBOX_PATTERN.matcher(streetOrPoBox);
		if (m.find()) {
			address.setPostOfficeBoxNumber(m.group(2));
			address.setPostOfficeBoxPostalCode(postalCode);
			address.setPostOfficeBoxCity(city);
		} else {
			address.setPostalCode(postalCode);
			address.setCity(city);
			m = STREET_PATTERN.matcher(streetOrPoBox);
			if (m.find()) {
				address.setStreet(m.group(1));
				address.setHouseNumber(m.group(2));
			} else {
				address.setStreet(streetOrPoBox);
			}
		}

		return address;
	}
}
