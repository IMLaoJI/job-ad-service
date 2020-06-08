package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read;

import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.stream.Stream;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

class SearchQueryBuilder {

	static final String PATH_CTX = "jobAdvertisement.";
	static final String PATH_PUBLICATION_START_DATE = PATH_CTX + "publication.startDate";
	static final String PATH_AVAM_JOB_ID = PATH_CTX + "stellennummerAvam";
	static final String PATH_EGOV_JOB_ID = PATH_CTX + "stellennummerEgov";
	static final String PATH_OWNER_USER_DISPLAY_NAME = PATH_CTX + "owner.userDisplayName";
	static final String PATH_DESCRIPTION = PATH_CTX + "jobContent.jobDescriptions.description";
	static final String PATH_LOCATION_CITY = PATH_CTX + "jobContent.location.city";
	static final String PATH_TITLE = PATH_CTX + "jobContent.jobDescriptions.title";
	static final String PATH_STATUS = PATH_CTX + "status";

	static BoolQueryBuilder mustAll(BoolQueryBuilder... queryBuilders) {
		return Stream.of(queryBuilders)
				.filter(BoolQueryBuilder::hasClauses)
				.reduce(boolQuery(), BoolQueryBuilder::must);
	}

	BoolQueryBuilder publicationStartDateFilter(Integer onlineSinceDays) {
		if (onlineSinceDays == null) {
			return boolQuery();
		}
		return boolQuery().must(
				rangeQuery(PATH_PUBLICATION_START_DATE).gte(String.format("now-%sd/d", onlineSinceDays)));
	}
}
