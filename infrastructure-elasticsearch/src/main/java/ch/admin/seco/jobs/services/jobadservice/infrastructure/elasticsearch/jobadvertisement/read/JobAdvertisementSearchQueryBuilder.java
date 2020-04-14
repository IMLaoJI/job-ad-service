package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementSearchService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.ProfessionCode;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.RadiusSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.search.JobAdvertisementSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.application.security.Role;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.GaussDecayFunctionBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_PUBLIC;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_RESTRICTED;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.TYPE_DOC;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.apache.commons.lang3.ArrayUtils.toStringArray;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.elasticsearch.common.unit.DistanceUnit.KILOMETERS;
import static org.elasticsearch.index.query.Operator.AND;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders.gaussDecayFunction;
import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;

@Component
public class JobAdvertisementSearchQueryBuilder extends SearchQueryBuilder {

	private static final String PATH_CREATED_TIME = "jobAdvertisement.createdTime";
	private static final String PATH_WORKLOAD_PERCENTAGE_MAX = PATH_CTX + "jobContent.employment.workloadPercentageMax";
	private static final String PATH_WORKLOAD_TIME_PERCENTAGE_MIN = PATH_CTX + "jobContent.employment.workloadPercentageMin";
	private static final String PATH_COMPANY_NAME = PATH_CTX + "jobContent.displayCompany.name";
	private static final String PATH_PERMANENT = PATH_CTX + "jobContent.employment.permanent";
	private static final String RELEVANCE = "_score";
	private static final int ONLINE_SINCE_DAYS = 60;
	private static final String PATH_LOCATION_CANTON_CODE = PATH_CTX + "jobContent.location.cantonCode";
	private static final String PATH_LOCATION_COMMUNAL_CODE = PATH_CTX + "jobContent.location.communalCode";
	private static final String PATH_LOCATION_COUNTRY_ISO_CODE = PATH_CTX + "jobContent.location.countryIsoCode";
	private static final String PATH_LOCATION_COORDINATES = PATH_CTX + "jobContent.location.coordinates";
	private static final String PATH_OCCUPATIONS = PATH_CTX + "jobContent.occupations";
	private static final String PATH_OCCUPATIONS_AVAM_OCCUPATION_CODE = PATH_OCCUPATIONS + ".avamOccupationCode";
	private static final String PATH_OCCUPATIONS_BFS_CODE = PATH_OCCUPATIONS + ".bfsCode";
	private static final String PATH_OCCUPATIONS_CHISCO3_CODE = PATH_OCCUPATIONS + ".chIsco3Code";
	private static final String PATH_OCCUPATIONS_CHISCO5_CODE = PATH_OCCUPATIONS + ".chIsco5Code";
	private static final String PATH_X28_CODE = PATH_CTX + "jobContent.x28OccupationCodes";
	private static final String PATH_PUBLICATION_RESTRICTED_DISPLAY = PATH_CTX + "publication.restrictedDisplay";
	private static final String PATH_PUBLICATION_PUBLIC_DISPLAY = PATH_CTX + "publication.publicDisplay";
	private static final String PATH_PUBLICATION_EURES_DISPLAY = PATH_CTX + "publication.euresDisplay";
	private static final String PATH_SOURCE_SYSTEM = PATH_CTX + "sourceSystem";
	private static final String PATH_LANGUAGE_SKILL_CODE = PATH_CTX + "jobContent.languageSkills.languageIsoCode";
	private static final String COMMUNAL_CODE_ABROAD = "9999";
	private static final String SWITZERLAND_COUNTRY_ISO_CODE = "CH";
	private static final String SCALE_IN_KM = "150km";
	private static final String OFFSET_IN_KM = "2km";
	private static final double DECAY_RATE = 0.5;

	@Value("${alv.feature.toggle.isGaussDecayEnabled}")
	boolean isGaussDecayEnabled;

	private final CurrentUserContext currentUserContext;

	public JobAdvertisementSearchQueryBuilder(CurrentUserContext currentUserContext) {
		this.currentUserContext = currentUserContext;
	}

	public NativeSearchQuery createSearchQuery(JobAdvertisementSearchRequest jobSearchRequestDto, Pageable pageRequest) {
		return createSearchQueryBuilder(jobSearchRequestDto)
				.withPageable(pageRequest)
				.withHighlightFields(new HighlightBuilder.Field("*").fragmentSize(300).numOfFragments(1))
				.withIndices(INDEX_NAME_JOB_ADVERTISEMENT)
				.withTypes(TYPE_DOC).build();
	}
	Pageable createPageRequest(int page, int size, JobAdvertisementSearchService.SearchSort sort) {
		return PageRequest.of(page, size, createSort(sort));
	}


	private NativeSearchQueryBuilder createSearchQueryBuilder(JobAdvertisementSearchRequest jobSearchRequest) {
		BoolQueryBuilder boolQueryBuilder = createQuery(jobSearchRequest);
		BoolQueryBuilder filterQueryBuilder = createFilter(jobSearchRequest);

		if (isRadiusNeeded(jobSearchRequest) && isGaussDecayEnabled) {
			boolQueryBuilder.must(prepareRadiusQuery(jobSearchRequest.getRadiusSearchRequest()));
		} else {
			filterQueryBuilder.must(localityFilter(jobSearchRequest));
		}

		return new NativeSearchQueryBuilder()
				.withQuery(boolQueryBuilder)
				.withFilter(filterQueryBuilder);
	}

	private BoolQueryBuilder createQuery(JobAdvertisementSearchRequest jobSearchRequest) {
		if (isEmpty(jobSearchRequest.getKeywords()) && isEmpty(jobSearchRequest.getProfessionCodes())) {
			return boolQuery().must(matchAllQuery());
		} else {
			return mustAll(createKeywordQuery(jobSearchRequest), createOccupationQuery(jobSearchRequest));
		}
	}


	private BoolQueryBuilder createFilter(JobAdvertisementSearchRequest jobSearchRequest) {
		Integer onlineSinceDays = Optional.ofNullable(jobSearchRequest.getOnlineSince()).orElse(ONLINE_SINCE_DAYS);
		return mustAll(
				statusFilter(jobSearchRequest),
				displayFilter(jobSearchRequest),
				publicationStartDateFilter(onlineSinceDays),
				workingTimeFilter(jobSearchRequest),
				contractTypeFilter(jobSearchRequest),
				companyFilter(jobSearchRequest.getCompanyName())
		);
	}


	private Sort createSort(JobAdvertisementSearchService.SearchSort sort) {
		switch (sort) {
			case date_asc:
				return Sort.by(asc(PATH_PUBLICATION_START_DATE), asc(PATH_CREATED_TIME));
			case date_desc:
				return Sort.by(desc(PATH_PUBLICATION_START_DATE), desc(PATH_CREATED_TIME));
			default:
				return Sort.by(
						desc(RELEVANCE),
						desc(PATH_PUBLICATION_START_DATE),
						desc(PATH_CREATED_TIME)
				);
		}
	}

	private BoolQueryBuilder contractTypeFilter(JobAdvertisementSearchRequest jobSearchRequest) {
		BoolQueryBuilder contractTypeFilter = boolQuery();

		if (jobSearchRequest.isPermanent() != null) {
			contractTypeFilter.must(termsQuery(PATH_PERMANENT, jobSearchRequest.isPermanent()));
		}

		return contractTypeFilter;
	}

	private BoolQueryBuilder workingTimeFilter(JobAdvertisementSearchRequest jobSearchRequest) {
		BoolQueryBuilder workingTimeFilter = boolQuery();

		if (jobSearchRequest.getWorkloadPercentageMin() != null) {
			workingTimeFilter.must(rangeQuery(PATH_WORKLOAD_PERCENTAGE_MAX).gte(jobSearchRequest.getWorkloadPercentageMin()));
		}

		if (jobSearchRequest.getWorkloadPercentageMax() != null) {
			workingTimeFilter.must(rangeQuery(PATH_WORKLOAD_TIME_PERCENTAGE_MIN).lte(jobSearchRequest.getWorkloadPercentageMax()));
		}

		return workingTimeFilter;
	}

	private BoolQueryBuilder createOccupationQuery(JobAdvertisementSearchRequest jobSearchRequest) {
		if (isNotEmpty(jobSearchRequest.getProfessionCodes())) {
			return Stream.of(jobSearchRequest.getProfessionCodes())
					.map(this::createOccupationCodeQuery)
					.reduce(boolQuery(), BoolQueryBuilder::should);
		} else {
			return boolQuery();
		}
	}

	private BoolQueryBuilder createOccupationCodeQuery(ProfessionCode code) {
		String path = null;
		switch (code.getType()) {
			case AVAM:
				path = PATH_OCCUPATIONS_AVAM_OCCUPATION_CODE;
				break;
			case BFS:
				path = PATH_OCCUPATIONS_BFS_CODE;
				break;
			case CHISCO3:
				path = PATH_OCCUPATIONS_CHISCO3_CODE;
				break;
			case CHISCO5:
				path = PATH_OCCUPATIONS_CHISCO5_CODE;
				break;
			case X28:
				path = PATH_X28_CODE;
				break;
		}
		return boolQuery().must(termQuery(path, code.getValue()));
	}

	private BoolQueryBuilder createKeywordQuery(JobAdvertisementSearchRequest jobSearchRequest) {
		BoolQueryBuilder keywordQuery = boolQuery();

		if (isNotEmpty(jobSearchRequest.getKeywords())) {
			Stream.of(jobSearchRequest.getKeywords())
					.flatMap(keyword -> keyword.startsWith("*")
							? Stream.of(termQuery(PATH_SOURCE_SYSTEM, keyword.substring(1).toUpperCase()).boost(2f))
							: Stream.of
							(
									multiMatchQuery(keyword, PATH_DESCRIPTION, PATH_LANGUAGE_SKILL_CODE)
											.field(PATH_TITLE, 1.5f)
											.type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX),
									termQuery(PATH_AVAM_JOB_ID, keyword).boost(10f),
									termQuery(PATH_EGOV_JOB_ID, keyword).boost(10f)
							)
					)
					.forEach(keywordQuery::should);

			String allKeywords = Stream.of(jobSearchRequest.getKeywords())
					.filter(keyword -> !keyword.startsWith("*"))
					.collect(Collectors.joining(" "));

			if (isNotBlank(allKeywords)) {
				keywordQuery.should(multiMatchQuery(allKeywords, PATH_DESCRIPTION, PATH_TITLE, PATH_LANGUAGE_SKILL_CODE)
						.boost(2f)
						.operator(AND));
			}
		}

		return keywordQuery;
	}

	private BoolQueryBuilder statusFilter(JobAdvertisementSearchRequest jobSearchRequest) {
		BoolQueryBuilder visibilityFilter = boolQuery();

		final JobAdvertisementStatus[] visibleStatuses;

		if (canViewRestrictedJobAds()) {
			if (Boolean.TRUE.equals(jobSearchRequest.getDisplayRestricted())) {
				visibleStatuses = toArray(PUBLISHED_RESTRICTED);
			} else {
				visibleStatuses = toArray(PUBLISHED_RESTRICTED, PUBLISHED_PUBLIC);
			}
		} else {
			visibleStatuses = toArray(PUBLISHED_PUBLIC);
		}

		visibilityFilter.must(termsQuery(PATH_STATUS, toStringArray(visibleStatuses)));

		return visibilityFilter;
	}

	private BoolQueryBuilder displayFilter(JobAdvertisementSearchRequest jobSearchRequest) {
		if (jobSearchRequest.getEuresDisplay() != null && jobSearchRequest.getEuresDisplay()) {
			return boolQuery()
					.must(termsQuery(PATH_PUBLICATION_EURES_DISPLAY, jobSearchRequest.getEuresDisplay()))
					.must(termQuery(PATH_STATUS, PUBLISHED_PUBLIC.name())
					);
		}

		if (canViewRestrictedJobAds()) {
			BoolQueryBuilder publishedPublicFilter = boolQuery()
					.must(termQuery(PATH_STATUS, PUBLISHED_PUBLIC.toString()))
					.must(boolQuery()
							.should(termQuery(PATH_PUBLICATION_PUBLIC_DISPLAY, true))
							.should(termQuery(PATH_PUBLICATION_RESTRICTED_DISPLAY, true))
					);
			BoolQueryBuilder publishedRestrictedFilter = boolQuery()
					.must(termQuery(PATH_STATUS, PUBLISHED_RESTRICTED.toString()));

			return boolQuery()
					.should(publishedPublicFilter)
					.should(publishedRestrictedFilter);
		}
		return boolQuery().must(termQuery(PATH_PUBLICATION_PUBLIC_DISPLAY, true));
	}


	private BoolQueryBuilder companyFilter(String companyName) {
		BoolQueryBuilder companyFilter = boolQuery();

		if (isNotBlank(companyName)) {
			companyFilter.must(matchPhrasePrefixQuery(PATH_COMPANY_NAME, companyName));
		}

		return companyFilter;
	}

	private BoolQueryBuilder localityFilter(JobAdvertisementSearchRequest jobSearchRequest) {
		BoolQueryBuilder localityFilter = boolQuery();

		if (isRadiusNeeded(jobSearchRequest) && !isGaussDecayEnabled) {
			RadiusSearchRequest radiusSearchRequest = jobSearchRequest.getRadiusSearchRequest();
			localityFilter.should(geoDistanceQuery(PATH_LOCATION_COORDINATES)
					.point(radiusSearchRequest.getGeoPoint().getLat(), radiusSearchRequest.getGeoPoint().getLon())
					.distance(radiusSearchRequest.getDistance(), DistanceUnit.KILOMETERS));
		}

		if (isCantonSearch(jobSearchRequest.getCantonCodes())) {
			localityFilter.should(termsQuery(PATH_LOCATION_CANTON_CODE, jobSearchRequest.getCantonCodes()));
		}

		if (isMultipleLocationSearch(jobSearchRequest.getCommunalCodes())) {
			localityFilter.should(termsQuery(PATH_LOCATION_COMMUNAL_CODE, jobSearchRequest.getCommunalCodes()));
		}

		if (isSingleLocationAbroadSearch(jobSearchRequest.getCommunalCodes())) {
			localityFilter.should(boolQuery()
					.must(existsQuery(PATH_LOCATION_COUNTRY_ISO_CODE))
					.mustNot(termsQuery(PATH_LOCATION_COUNTRY_ISO_CODE, SWITZERLAND_COUNTRY_ISO_CODE)));
		}
		return localityFilter;
	}

	private boolean isRadiusNeeded(JobAdvertisementSearchRequest jobSearchRequest) {
		return jobSearchRequest.getRadiusSearchRequest() != null && jobSearchRequest.getRadiusSearchRequest().getDistance() != null;
	}

	private boolean isMultipleLocationSearch(String[] communalCodes) {
		return isNotEmpty(communalCodes) && communalCodes.length > 1;
	}

	private boolean isSingleLocationAbroadSearch(String[] communalCodes) {
		return isNotEmpty(communalCodes)
				&& Arrays.asList(communalCodes).contains(COMMUNAL_CODE_ABROAD)
				&& communalCodes.length == 1;
	}

	private boolean isCantonSearch(String[] cantonCodes) {
		return isNotEmpty(cantonCodes);
	}

	private boolean canViewRestrictedJobAds() {
		return this.currentUserContext.hasAnyRoles(Role.JOBSEEKER_CLIENT, Role.SYSADMIN, Role.ADMIN);
	}

	private FunctionScoreQueryBuilder prepareRadiusQuery(RadiusSearchRequest radiusSearchRequest) {
		GeoDistanceQueryBuilder geoDistanceQueryBuilder = getDistanceQuery(radiusSearchRequest);
		GaussDecayFunctionBuilder gaussDecayFunctionBuilder = getGaussDecayFunctionBuilder(radiusSearchRequest);
		return functionScoreQuery(geoDistanceQueryBuilder, gaussDecayFunctionBuilder).boost(1.5f);
	}

	private GaussDecayFunctionBuilder getGaussDecayFunctionBuilder(RadiusSearchRequest radiusSearchRequest) {
		return gaussDecayFunction(PATH_LOCATION_COORDINATES, radiusSearchRequest.getGeoPoint().toString(), SCALE_IN_KM, OFFSET_IN_KM, DECAY_RATE);
	}

	private GeoDistanceQueryBuilder getDistanceQuery(RadiusSearchRequest radiusSearchRequest) {
		return geoDistanceQuery(PATH_LOCATION_COORDINATES)
				.point(radiusSearchRequest.getGeoPoint().getLat(), radiusSearchRequest.getGeoPoint().getLon())
				.distance(radiusSearchRequest.getDistance(), KILOMETERS);
	}

}
