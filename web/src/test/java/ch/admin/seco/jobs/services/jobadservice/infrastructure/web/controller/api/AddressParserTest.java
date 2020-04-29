package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.AddressDto;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressParserTest {

    private AddressTest[] addresses = {
            new AddressTest(
                    // null
                    null,
                    null
            ),
            new AddressTest(
                    // 2 words city
                    "A à Z emplois SA, 2301 La Chaux-de-Fonds",
                    createAddressDto("A à Z emplois SA", null, null, "2301", "La Chaux-de-Fonds", null, null, null, "CH")
            ),
            new AddressTest(
                    // Single word city
                    "Adecco Human Resources AG, 4410 Liestal",
                    createAddressDto("Adecco Human Resources AG", null, null, "4410", "Liestal", null, null, null, "CH")
            ),
            new AddressTest(
                    // 2 words street
                    "Adecco Ressources Humaines SA Watch Technology A041, Le Rocher 2, 1348 Le Brassus",
                    createAddressDto("Adecco Ressources Humaines SA Watch Technology A041", "Le Rocher", "2", "1348", "Le Brassus", null, null, null, "CH")
            ),
            new AddressTest(
                    // 3 word street
                    "Manpower SA, Rue de Vevey 11, 1630 Bulle",
                    createAddressDto("Manpower SA", "Rue de Vevey", "11", "1630", "Bulle", null, null, null, "CH")
            ),
            new AddressTest(
                    // 2 words and with apostrophe street
                    "Adecco Ressources Humaines SA, Rue d'Orbe 5, 1401 Yverdon-les-Bains",
                    createAddressDto("Adecco Ressources Humaines SA", "Rue d'Orbe", "5", "1401", "Yverdon-les-Bains", null, null, null, "CH")
            ),
            new AddressTest(
                    // Special character in name
                    "Art Furrer Resort ****, 3987 Riederalp",
                    createAddressDto("Art Furrer Resort ****", null, null, "3987", "Riederalp", null, null, null, "CH")
            ),
            new AddressTest(
                    // City in 2 language
                    "B profil interim SA, 2503 Biel/Bienne",
                    createAddressDto("B profil interim SA", null, null, "2503", "Biel/Bienne", null, null, null, "CH")
            ),
            new AddressTest(
                    // Address with no spaces after comma
                    "Beeworx GmbH,Steinengraben 40,4051 Basel",
                    createAddressDto("Beeworx GmbH", "Steinengraben", "40", "4051", "Basel", null, null, null, "CH")
            ),
            new AddressTest(
                    // Address with space before comma
                    "Berghotel Riederfurka, Riederalp , 3987 Riederalp",
                    createAddressDto("Berghotel Riederfurka", "Riederalp", null, "3987", "Riederalp", null, null, null, "CH")
            ),
            new AddressTest(
                    // City with canton
                    "G. Zehnder AG Gerüstbau, 5413 Birmenstorf AG",
                    createAddressDto("G. Zehnder AG Gerüstbau", null, null, "5413", "Birmenstorf AG", null, null, null, "CH")
            ),
            new AddressTest(
                    // Line break separator
                    "Gimmerz 42\n3283 Kallnach",
                    createAddressDto("Gimmerz 42", null, null, "3283", "Kallnach", null, null, null, "CH")
            ),
            new AddressTest(
                    // Wrongly email in post address
                    "karino.hueppin@arbeitskraft.ch",
                    null
            ),
            new AddressTest(
                    // Comma in name
                    "Dep. Bau, Verkehr & Umwelt (BVU) Abteilung Tiefbau, Entfelderstrasse 22, 5000 Aarau",
                    createAddressDto("Dep. Bau, Verkehr & Umwelt (BVU) Abteilung Tiefbau", "Entfelderstrasse", "22", "5000", "Aarau", null, null, null, "CH")
            ),
            new AddressTest(
                    // Characters in house number
                    "Muster AG, Musterstrasse 10B, 3000 Bern",
                    createAddressDto("Muster AG", "Musterstrasse", "10B", "3000", "Bern", null, null, null, "CH")
            ),
            new AddressTest(
                    // Line break separated and second name
                    "Muster AG\nz.H Muster Hans\nMusterstrasse 1\n3000 Bern",
                    createAddressDto("Muster AG,z.H Muster Hans", "Musterstrasse", "1", "3000", "Bern", null, null, null, "CH")
            ),
            new AddressTest(
                    // Line break separated and second name and spaces
                    "Muster AG \n  z.H Muster Hans  \n  Musterstrasse  1 \n3000 Bern",
                    createAddressDto("Muster AG ,  z.H Muster Hans", "Musterstrasse", "1", "3000", "Bern", null, null, null, "CH")
            ),
            new AddressTest(
                    // Country code Switzerland
                    "Muster AG, Musterstrasse 1, CH-3000 Bern",
                    createAddressDto("Muster AG", "Musterstrasse", "1", "3000", "Bern", null, null, null, "CH")
            ),
            new AddressTest(
                    // Country code Germany
                    "Muster AG, Musterstrasse 1, DE-12345 Freiburg",
                    createAddressDto("Muster AG", "Musterstrasse", "1", "12345", "Freiburg", null, null, null, "DE")
            ),
            new AddressTest(
                    // Post office box address
                    "Muster AG, Postfach 123, 3000 Bern",
                    createAddressDto("Muster AG", null, null, null, null, "123", "3000", "Bern", "CH")
            ),
		    new AddressTest(
				    // Post office box address
				    "Muster AG , PO Box 1023  , 3000 Bern",
				    createAddressDto("Muster AG", null, null, null, null, "1023", "3000", "Bern", "CH")
		    )
    };

    // FIXME Test should run correctly
    @Test
    public void shouldParseAddress() {
        for (AddressTest address : addresses) {
            System.out.println("Check address: " + address.getInput());
            AddressDto result = AddressParser.parse(address.getInput(), (address.getExpected() != null) ? address.getExpected().getName() : null);

            if (address.getExpected() == null) {
                assertThat(result).as("Check result is null").isNull();
            } else {
                assertThat(result).as("Check result is not null").isNotNull();
                AddressDto expected = address.getExpected();
                assertThat(result.getName()).as("Check name").isEqualTo(expected.getName());
                assertThat(result.getStreet()).as("Check street").isEqualTo(expected.getStreet());
                assertThat(result.getHouseNumber()).as("Check house nr.").isEqualTo(expected.getHouseNumber());
                assertThat(result.getPostalCode()).as("Check postal code").isEqualTo(expected.getPostalCode());
                assertThat(result.getCity()).as("Check city").isEqualTo(expected.getCity());
                assertThat(result.getPostOfficeBoxNumber()).as("Check POBox nr").isEqualTo(expected.getPostOfficeBoxNumber());
                assertThat(result.getPostOfficeBoxPostalCode()).as("Check POBox postal code").isEqualTo(expected.getPostOfficeBoxPostalCode());
                assertThat(result.getPostOfficeBoxCity()).as("Check POBox city").isEqualTo(expected.getPostOfficeBoxCity());
                assertThat(result.getCountryIsoCode()).as("Check country ISO code").isEqualTo(expected.getCountryIsoCode());
            }
        }
    }

    private static AddressDto createAddressDto(String name, String street, String houseNumber, String postalCode, String city, String postOfficeBoxNumber, String postOfficeBoxPostalCode, String postOfficeBoxCity, String countryIsoCode) {
        return new AddressDto()
                .setName(name)
                .setStreet(street)
                .setHouseNumber(houseNumber)
                .setPostalCode(postalCode)
                .setCity(city)
                .setPostOfficeBoxNumber(postOfficeBoxNumber)
                .setPostOfficeBoxPostalCode(postOfficeBoxPostalCode)
                .setPostOfficeBoxCity(postOfficeBoxCity)
                .setCountryIsoCode(countryIsoCode);
    }

    private static final class AddressTest {

        private String input;

        private AddressDto expected;

        AddressTest(String input, AddressDto expected) {
            this.input = input;
            this.expected = expected;
        }

        String getInput() {
            return input;
        }

        AddressDto getExpected() {
            return expected;
        }
    }
}
