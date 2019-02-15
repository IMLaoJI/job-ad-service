package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.GeoPoint;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;

import java.util.List;
import java.util.stream.Stream;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job01;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job02;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job03;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job04;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job05;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementIdFixture.job06;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobAdvertisementTestFixture.createJobWithLocation;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.LocationFixture.testLocation;
import static java.util.stream.Collectors.toList;

public class JobAdvertisementWithLocationsFixture {

    public static List<JobAdvertisement> listOfJobAdsForGeoDistanceTests() {
        return Stream.of(
                createJobWithLocation(job01.id(),
                        testLocation()
                                .setCity("Bern")
                                .setCommunalCode("351")
                                .setRegionCode("BE01")
                                .setCantonCode("BE")
                                .setPostalCode("3000")
                                .setCountryIsoCode("CH")
                                .setCoordinates(new GeoPoint(7.441,46.948))
                                .build()),
                createJobWithLocation(job02.id(),
                        testLocation()
                                .setCity("Zürich")
                                .setCommunalCode("261")
                                .setRegionCode("ZH12")
                                .setCantonCode("ZH12")
                                .setPostalCode("8000")
                                .setCountryIsoCode("CH")
                                .setCoordinates(new GeoPoint(8.47, 47.360508))
                                .build()),
                createJobWithLocation(job03.id(),
                        testLocation()
                                .setCity("Sion")
                                .setCommunalCode("6266")
                                .setRegionCode("VS06")
                                .setCantonCode("VS")
                                .setPostalCode("1950")
                                .setCountryIsoCode("CH")
                                .setCoordinates(new GeoPoint(7.359, 46.234))
                                .build()),
                createJobWithLocation(job04.id(),
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
        return Stream.of(
                createJobWithLocation(job01.id(),
                        testLocation()
                                .setCity("Bern")
                                .setCommunalCode("351")
                                .setRegionCode("BE01")
                                .setCantonCode("BE")
                                .setPostalCode("3000")
                                .setCountryIsoCode("CH")
                                .build()),
                createJobWithLocation(job02.id(),
                        testLocation()
                                .setCity("Ausland")
                                .setCommunalCode("7001")
                                .setRegionCode("A")
                                .setCantonCode("FL")
                                .setPostalCode("9490")
                                .setCountryIsoCode("LI")
                                .build()),
                createJobWithLocation(job03.id(),
                        testLocation()
                                .setCity("Ausland")
                                .setCommunalCode(null)
                                .setRegionCode(null)
                                .setCantonCode(null)
                                .setPostalCode("91244")
                                .setCountryIsoCode("FR")
                                .build()),
                createJobWithLocation(job04.id(),
                        testLocation()
                                .setCity("Ausland")
                                .setCommunalCode(null)
                                .setRegionCode(null)
                                .setCantonCode(null)
                                .setPostalCode("94541")
                                .setCountryIsoCode("DE")
                                .build()),
                createJobWithLocation(job05.id(),
                        testLocation()
                                .setCity("Zürich")
                                .setCommunalCode("261")
                                .setRegionCode("ZH01")
                                .setCantonCode("ZH")
                                .setPostalCode("8000")
                                .setCountryIsoCode("CH")
                                .build()),
                createJobWithLocation(job06.id(),
                        testLocation()
                                .setCity("Lausanne")
                                .setCommunalCode(null)
                                .setRegionCode(null)
                                .setCantonCode(null)
                                .setPostalCode("1000")
                                .setCountryIsoCode(null)
                                .build())).collect(toList());
    }
}
