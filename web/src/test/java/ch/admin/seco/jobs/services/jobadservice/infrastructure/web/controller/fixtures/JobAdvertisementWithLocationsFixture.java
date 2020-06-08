package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.GeoPoint;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job02;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job03;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job04;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job05;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job06;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job07;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithLocationAndPublicationStartDate;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.testJobAdvertisementWithContentAndLocation;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobContentFixture.testJobContent;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobDescriptionFixture.testJobDescription;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.LocationFixture.testLocation;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class JobAdvertisementWithLocationsFixture {

	private static final String EXTERNAL_CODE_KOCH = "11000411";

    public static List<JobAdvertisement> listOfJobAdsForGeoDistanceTests() {
        return Stream.of(
                createJobWithLocationAndPublicationStartDate(job01.id(), LocalDate.now(),
                        testLocation()
                                .setCity("Bern")
                                .setCommunalCode("351")
                                .setRegionCode("BE01")
                                .setCantonCode("BE")
                                .setPostalCode("3000")
                                .setCountryIsoCode("CH")
                                .setCoordinates(new GeoPoint(7.441, 46.948))
                                .build()),
                createJobWithLocationAndPublicationStartDate(job02.id(), LocalDate.now().plusDays(1),
                        testLocation()
                                .setCity("Zürich")
                                .setCommunalCode("261")
                                .setRegionCode("ZH12")
                                .setCantonCode("ZH12")
                                .setPostalCode("8000")
                                .setCountryIsoCode("CH")
                                .setCoordinates(new GeoPoint(8.47, 47.360508))
                                .build()),
                createJobWithLocationAndPublicationStartDate(job03.id(), LocalDate.now().plusDays(2),
                        testLocation()
                                .setCity("Sion")
                                .setCommunalCode("6266")
                                .setRegionCode("VS06")
                                .setCantonCode("VS")
                                .setPostalCode("1950")
                                .setCountryIsoCode("CH")
                                .setCoordinates(new GeoPoint(7.359, 46.234))
                                .build()),
                createJobWithLocationAndPublicationStartDate(job04.id(), LocalDate.now().plusDays(3),
                        testLocation()
                                .setCity("Lausanne")
                                .setCommunalCode("5586")
                                .setRegionCode("VD01")
                                .setCantonCode("VD")
                                .setPostalCode("1000")
                                .setCountryIsoCode("CH")
                                .setCoordinates(new GeoPoint(6.6523078, 46.552043))
                                .build())).collect(toList());
    }

    public static List<JobAdvertisement> listOfJobAdsForAbroadSearchTests() {
        int noOfDays = 1;
        return Stream.of(
                createJobWithLocationAndPublicationStartDate(job01.id(), LocalDate.now(),
                        testLocation()
                                .setCity("Bern")
                                .setCommunalCode("351")
                                .setRegionCode("BE01")
                                .setCantonCode("BE")
                                .setPostalCode("3000")
                                .setCountryIsoCode("CH")
                                .build()),
                createJobWithLocationAndPublicationStartDate(job02.id(), LocalDate.now().plusDays(noOfDays++),
                        testLocation()
                                .setCity("Ausland")
                                .setCommunalCode("9999")
                                .setRegionCode("A")
                                .setCantonCode("FL")
                                .setPostalCode("9490")
                                .setCountryIsoCode("LI")
                                .build()),
                createJobWithLocationAndPublicationStartDate(job03.id(), LocalDate.now().plusDays(noOfDays++),
                        testLocation()
                                .setCity("Ausland")
                                .setCommunalCode("9999")
                                .setRegionCode(null)
                                .setCantonCode(null)
                                .setPostalCode("91244")
                                .setCountryIsoCode("FR")
                                .build()),
                createJobWithLocationAndPublicationStartDate(job04.id(), LocalDate.now().plusDays(noOfDays++),
                        testLocation()
                                .setCity("Ausland")
                                .setCommunalCode("9999")
                                .setRegionCode(null)
                                .setCantonCode(null)
                                .setPostalCode("94541")
                                .setCountryIsoCode("DE")
                                .build()),
                createJobWithLocationAndPublicationStartDate(job05.id(), LocalDate.now().plusDays(noOfDays++),
                        testLocation()
                                .setCity("Zürich")
                                .setCommunalCode("261")
                                .setRegionCode("ZH01")
                                .setCantonCode("ZH")
                                .setPostalCode("8000")
                                .setCountryIsoCode("CH")
                                .build()),
                createJobWithLocationAndPublicationStartDate(job06.id(), LocalDate.now().plusDays(noOfDays),
                        testLocation()
                                .setCity("Lausanne")
                                .setCommunalCode("5586")
                                .setRegionCode(null)
                                .setCantonCode(null)
                                .setPostalCode("1000")
                                .setCountryIsoCode("CH")
                                .build())).collect(toList());
    }

    public static List<JobAdvertisement> listOfJobAdsForDecayingScoreSearchTests() {
        return Stream.of(testJobAdvertisementWithContentAndLocation(job05.id(),
                testJobContent()
                        .setJobDescriptions(singletonList(testJobDescription().setTitle("Koch").build()))
                        .setX28OccupationCodes("11000411")
                        .setLocation(
                        testLocation()
                                .setCity("Lausanne")
                                .setCommunalCode("5586")
                                .setRegionCode("VD01")
                                .setCantonCode("VD")
                                .setPostalCode("1000")
                                .setCountryIsoCode("CH")
                                .setCoordinates(new GeoPoint(6.6523078, 46.552043))
                                .build()).build()),
        testJobAdvertisementWithContentAndLocation(job06.id(),
                testJobContent()
                        .setJobDescriptions(singletonList(testJobDescription().setTitle("Koch").build()))
                        .setX28OccupationCodes("11000411")
                        .setLocation(
                        testLocation()
                                .setCity("Bern")
                                .setCommunalCode("351")
                                .setRegionCode("BE01")
                                .setCantonCode("BE")
                                .setPostalCode("3000")
                                .setCountryIsoCode("CH")
                                .setCoordinates(new GeoPoint(7.441, 46.948))
                                .build()).build()),
        testJobAdvertisementWithContentAndLocation(job07.id(),
                testJobContent()
                        .setJobDescriptions(singletonList(testJobDescription().setTitle("Koch").build()))
                        .setX28OccupationCodes("11000411")
                        .setLocation(
                        testLocation()
                                .setCity("Sion")
                                .setCommunalCode("6266")
                                .setRegionCode("VS06")
                                .setCantonCode("VS")
                                .setPostalCode("1950")
                                .setCountryIsoCode("CH")
                                .setCoordinates(new GeoPoint(7.359, 46.234))
                                .build()).build())).collect(toList());
    }

	public static List<JobAdvertisement> listOfJobAdsForCloseRangeDistanceTests() {
		return Stream.of(
				testJobAdvertisementWithContentAndLocation(job01.id(),
				testJobContent()
						.setJobDescriptions(singletonList(testJobDescription().setTitle("Koch").build()))
						.setX28OccupationCodes(EXTERNAL_CODE_KOCH)
						.setLocation(
								testLocation()
										.setCity("Köniz")
										.setCommunalCode("355")
										.setRegionCode("BE01")
										.setCantonCode("BE")
										.setPostalCode("3098")
										.setCountryIsoCode("CH")
										.setCoordinates(new GeoPoint(7.413, 46.921))
										.build()).build()),
		testJobAdvertisementWithContentAndLocation(job02.id(),
				testJobContent()
						.setJobDescriptions(singletonList(testJobDescription().setTitle("Koch").build()))
						.setX28OccupationCodes(EXTERNAL_CODE_KOCH)
						.setLocation(
								testLocation()
										.setCity("Ostermundigen")
										.setCommunalCode("363")
										.setRegionCode("BE01")
										.setCantonCode("BE")
										.setPostalCode("3072")
										.setCountryIsoCode("CH")
										.setCoordinates(new GeoPoint(7.498, 46.958))
										.build()).build()),
		testJobAdvertisementWithContentAndLocation(job03.id(),
				testJobContent()
						.setJobDescriptions(singletonList(testJobDescription()
								.setTitle("Koch")
								.build()))
						.setX28OccupationCodes(EXTERNAL_CODE_KOCH)
						.setLocation(
								testLocation()
										.setCity("Bern")
										.setCommunalCode("351")
										.setRegionCode("BE01")
										.setCantonCode("BE")
										.setPostalCode("3000")
										.setCountryIsoCode("CH")
										.setCoordinates(new GeoPoint(7.441, 46.948))
										.build()).build())).collect(toList());
	}
}
