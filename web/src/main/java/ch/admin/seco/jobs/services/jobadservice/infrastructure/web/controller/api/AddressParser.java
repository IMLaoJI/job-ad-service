package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.AddressDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.hasText;

class AddressParser {

	private static final Pattern ADDRESS_PATTERN = Pattern.compile("(.*)[,][ ]*([A-Z]{2}-)?(\\d{4,5})[ ]+(.*)");
	private static final Pattern ADDRESSLINE_PATTERN = Pattern.compile("(.*)[,][ ]*(.*)");
	private static final Pattern POBOX_PATTERN = Pattern.compile("(Postfach|Case postale|PO Box|Casella postale)[ ]+(\\d+)");
	private static final Pattern STREET_PATTERN = Pattern.compile("(.*?)[ ]+(\\d.*+)");

	private static final Logger LOG = LoggerFactory.getLogger(AddressParser.class);

	/*
	 * This simplified parser treats addresses as Swiss addresses unless a country code is given (E.g. DE-80120).
	 * If the address is incomplete, the business name can be supplemented as address name.
	 */
	static AddressDto parse(String rawAddress, String companyName) {
		if (!hasText(rawAddress)) {
			return null;
		}

		// First we check whether this is a valid address
		Matcher addressMatcher = ADDRESS_PATTERN.matcher(rawAddress.trim().replace('\n', ','));
		if (!addressMatcher.find()) {
			LOG.info("Unable to parse address: '" + rawAddress + "'");
			return null;
		}

		AddressDto address = new AddressDto();
		String addr = addressMatcher.group(1).trim();
		address.setCountryIsoCode(addressMatcher.group(2) == null ? "CH" : addressMatcher.group(2).substring(0, 2));
		String postalCode = addressMatcher.group(3);
		String city = addressMatcher.group(4);

		// Now we check whether the address is complete
		String streetOrPoBox = "";
		Matcher addressLineMatcher = ADDRESSLINE_PATTERN.matcher(addr);
		if (addressLineMatcher.find()) {
			address.setName(addressLineMatcher.group(1).trim());
			streetOrPoBox = addressLineMatcher.group(2);
		} else {
			address.setName(companyName);
			if (!addr.equalsIgnoreCase(companyName)) {
				streetOrPoBox = addr;
			}
		}

		// Now check for PO Box and Street number
		Matcher poBoxMatcher = POBOX_PATTERN.matcher(streetOrPoBox);
		if (poBoxMatcher.find()) {
			address.setPostOfficeBoxNumber(poBoxMatcher.group(2));
			address.setPostOfficeBoxPostalCode(postalCode);
			address.setPostOfficeBoxCity(city);
		} else {
			address.setPostalCode(postalCode);
			address.setCity(city);
			Matcher streetMatcher = STREET_PATTERN.matcher(streetOrPoBox);
			if (streetMatcher.find()) {
				address.setStreet(streetMatcher.group(1));
				address.setHouseNumber(streetMatcher.group(2));
			} else {
				address.setStreet(hasText(streetOrPoBox) ? streetOrPoBox : null);
			}
		}

		return address;
	}
}
