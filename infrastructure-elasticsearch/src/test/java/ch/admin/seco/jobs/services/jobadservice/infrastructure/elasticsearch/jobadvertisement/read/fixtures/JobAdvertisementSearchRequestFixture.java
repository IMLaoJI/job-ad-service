package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read.fixtures;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.RadiusSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.GeoPointDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.search.JobAdvertisementSearchRequest;

public class JobAdvertisementSearchRequestFixture {


	public static JobAdvertisementSearchRequest testJobAdvertisementSearchRequest() {
		return new JobAdvertisementSearchRequest()
				.setRadiusSearchRequest(radiusSearchRequest());

	}

	private static RadiusSearchRequest radiusSearchRequest() {
		return new RadiusSearchRequest()
				.setGeoPoint(new GeoPointDto().setLon(46.1).setLat(7.6))
				.setDistance(null);
	}

}
