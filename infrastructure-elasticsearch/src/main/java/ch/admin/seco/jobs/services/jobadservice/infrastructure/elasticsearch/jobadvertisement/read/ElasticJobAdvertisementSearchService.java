package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read;

import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.FavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementSearchResult;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementSearchService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.ManagedJobAdSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.ProfessionCode;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.RadiusSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobDescriptionDto;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUser;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.application.security.Role;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.ElasticsearchConfiguration;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.GaussDecayFunctionBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_PUBLIC;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus.PUBLISHED_RESTRICTED;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.TYPE_DOC;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemDocument.FAVOURITE_ITEM_RELATION_NAME;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.apache.commons.lang3.ArrayUtils.toStringArray;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.elasticsearch.common.unit.DistanceUnit.KILOMETERS;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders.gaussDecayFunction;
import static org.elasticsearch.join.query.JoinQueryBuilders.hasChildQuery;
import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.util.StringUtils.isEmpty;

@Service
public class ElasticJobAdvertisementSearchService implements JobAdvertisementSearchService {


	private static final String PATH_CREATED_TIME = "jobAdvertisement.createdTime";
	private static final String SCALE_IN_KM = "150km";
	private static final String OFFSET_IN_KM = "30km";
	private static final double DECAY_RATE = 0.5;

	private static Logger LOG = LoggerFactory.getLogger(ElasticJobAdvertisementSearchService.class);

	private static final String PATH_CTX = "jobAdvertisement.";
	private static final String PATH_AVAM_JOB_ID = PATH_CTX + "stellennummerAvam";
	private static final String PATH_EGOV_JOB_ID = PATH_CTX + "stellennummerEgov";
	private static final String PATH_COMPANY_NAME = PATH_CTX + "jobContent.displayCompany.name";
	private static final String PATH_OWNER_COMPANY_ID = PATH_CTX + "owner.companyId";
	private static final String PATH_OWNER_USER_ID = PATH_CTX + "owner.userId";
	private static final String PATH_OWNER_USER_DISPLAY_NAME = PATH_CTX + "owner.userDisplayName";
	private static final String PATH_DESCRIPTION = PATH_CTX + "jobContent.jobDescriptions.description";
	private static final String PATH_LOCATION_CANTON_CODE = PATH_CTX + "jobContent.location.cantonCode";
	private static final String PATH_LOCATION_COMMUNAL_CODE = PATH_CTX + "jobContent.location.communalCode";
	private static final String PATH_LOCATION_CITY = PATH_CTX + "jobContent.location.city";
	private static final String PATH_LOCATION_COUNTRY_ISO_CODE = PATH_CTX + "jobContent.location.countryIsoCode";
	private static final String PATH_LOCATION_COORDINATES = PATH_CTX + "jobContent.location.coordinates";
	private static final String PATH_OCCUPATIONS = PATH_CTX + "jobContent.occupations";
	private static final String PATH_OCCUPATIONS_AVAM_OCCUPATION_CODE = PATH_OCCUPATIONS + ".avamOccupationCode";
	private static final String PATH_OCCUPATIONS_BFS_CODE = PATH_OCCUPATIONS + ".bfsCode";
	private static final String PATH_OCCUPATIONS_SBN3_CODE = PATH_OCCUPATIONS + ".sbn3Code";
	private static final String PATH_OCCUPATIONS_SBN5_CODE = PATH_OCCUPATIONS + ".sbn5Code";
	private static final String PATH_X28_CODE = PATH_CTX + "jobContent.x28OccupationCodes";
	private static final String PATH_PERMANENT = PATH_CTX + "jobContent.employment.permanent";
	private static final String PATH_PUBLICATION_RESTRICTED_DISPLAY = PATH_CTX + "publication.restrictedDisplay";
	private static final String PATH_PUBLICATION_PUBLIC_DISPLAY = PATH_CTX + "publication.publicDisplay";
	private static final String PATH_PUBLICATION_START_DATE = PATH_CTX + "publication.startDate";
	private static final String PATH_PUBLICATION_EURES_DISPLAY = PATH_CTX + "publication.euresDisplay";
	private static final String PATH_TITLE = PATH_CTX + "jobContent.jobDescriptions.title";
	private static final String PATH_STATUS = PATH_CTX + "status";
	private static final String PATH_SOURCE_SYSTEM = PATH_CTX + "sourceSystem";
	private static final String PATH_WORKLOAD_PERCENTAGE_MAX = PATH_CTX + "jobContent.employment.workloadPercentageMax";
	private static final String PATH_WORKLOAD_TIME_PERCENTAGE_MIN = PATH_CTX + "jobContent.employment.workloadPercentageMin";
	private static final String PATH_LANGUAGE_SKILL_CODE = PATH_CTX + "jobContent.languageSkills.languageIsoCode";
	private static final String COMMUNAL_CODE_ABROAD = "9999";
	private static final String SWITZERLAND_COUNTRY_ISO_CODE = "CH";
	private static final String RELEVANCE = "_score";
	private static final int ONLINE_SINCE_DAYS = 60;
	private static final String MANAGED_JOB_AD_KEYWORD_DELIMITER = "\\s+";

	private final CurrentUserContext currentUserContext;

	private final ElasticsearchTemplate elasticsearchTemplate;

	private final ResultsMapper resultsMapper;

	private final FavouriteItemRepository favouriteItemRepository;

	public ElasticJobAdvertisementSearchService(CurrentUserContext currentUserContext,
												ElasticsearchTemplate elasticsearchTemplate,
												ElasticsearchConfiguration.CustomEntityMapper customEntityMapper,
												FavouriteItemRepository favouriteItemRepository) {
		this.currentUserContext = currentUserContext;
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.favouriteItemRepository = favouriteItemRepository;
		this.resultsMapper = new DefaultResultMapper(elasticsearchTemplate.getElasticsearchConverter().getMappingContext(), customEntityMapper);
	}

	@Override
	public Page<JobAdvertisementSearchResult> search(JobAdvertisementSearchRequest jobSearchRequest, int page, int size, SearchSort sort) {
		Pageable pageable = PageRequest.of(page, size, createSort(sort));
		SearchQuery searchQuery = createSearchQueryBuilder(jobSearchRequest)
				.withPageable(pageable)
				.withHighlightFields(new HighlightBuilder.Field("*").fragmentSize(300).numOfFragments(1))
				.withIndices(INDEX_NAME_JOB_ADVERTISEMENT)
				.withTypes(TYPE_DOC)
				.build();

		if (LOG.isTraceEnabled()) {
			LOG.trace("query: {}", searchQuery.getQuery());
			LOG.trace("filter: {}", searchQuery.getFilter());
		}

		return elasticsearchTemplate.query(searchQuery, response -> extractSearchResult(pageable, response));
	}

	@Override
	@PreAuthorize("isAuthenticated() && @favouriteItemAuthorizationService.matchesCurrentUserId(#ownerUserId)")
	public Page<JobAdvertisementSearchResult> searchFavouriteJobAds(String ownerUserId, String keyword, int page, int size) {
		Pageable pageable = PageRequest
				.of(page, size, Sort.by(
						desc(PATH_PUBLICATION_START_DATE),
						desc(PATH_CREATED_TIME)));
		NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
				.withFilter(hasChildQuery(
						FAVOURITE_ITEM_RELATION_NAME,
						termQuery("favouriteItem.ownerUserId", ownerUserId),
						ScoreMode.None
				))
				.withPageable(pageable);
		if (StringUtils.isNotBlank(keyword)) {
			searchQueryBuilder
					.withQuery(boolQuery()
							.should(multiMatchQuery(keyword, PATH_TITLE, PATH_DESCRIPTION).type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX))
							.should(hasChildQuery(FAVOURITE_ITEM_RELATION_NAME, matchPhrasePrefixQuery("favouriteItem.note", keyword), ScoreMode.None))
					)
			;
		}
		searchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("*").fragmentSize(300).numOfFragments(1));
		return elasticsearchTemplate.query(searchQueryBuilder.build(), response -> extractSearchResult(pageable, response));
	}

	@Override
	@PreAuthorize("@jobAdvertisementAuthorizationService.isCurrentUserMemberOfCompany(#searchRequest.companyId)")
	public Page<JobAdvertisementDto> searchManagedJobAds(
			ManagedJobAdSearchRequest searchRequest,
			Pageable pageable) {

		Pageable updatedPageable = appendUniqueSortingKey(pageable);

		SearchQuery query = new NativeSearchQueryBuilder()
				.withQuery(createManagedJobAdsKeywordsQuery(searchRequest.getKeywordsText()))
				.withFilter(mustAll(
						publicationStartDateFilter(searchRequest.getOnlineSinceDays()),
						ownerUserIdFilter(searchRequest.getOwnerUserId()),
						ownerCompanyIdFilter(searchRequest.getCompanyId()),
						stateFilter(searchRequest.getState())))
				.withPageable(updatedPageable)
				.withHighlightFields(new HighlightBuilder.Field("*").fragmentSize(300).numOfFragments(1))
				.build();

		if (LOG.isTraceEnabled()) {
			LOG.trace("query: {}", query.getQuery());
			LOG.trace("filter: {}", query.getFilter());
			LOG.trace("sort: {}", query.getSort());
		}

		return elasticsearchTemplate.query(query, response -> extractHighlightedResults(updatedPageable, response));
	}

	private Page<JobAdvertisementSearchResult> extractSearchResult(Pageable pageable, SearchResponse response) {
		AggregatedPage<JobAdvertisementDocument> searchResults = resultsMapper.mapResults(response, JobAdvertisementDocument.class, pageable);
		SearchHits searchHits = response.getHits();
		Iterator<SearchHit> searchHitIterator = searchHits.iterator();

		List<JobAdvertisementDto> jobAdvertisements = searchResults.getContent().stream()
				.map(JobAdvertisementDocument::getJobAdvertisement)
				.map(JobAdvertisementDto::toDto)
				.map(jobAdvertisementDto -> highlightFields(jobAdvertisementDto, searchHitIterator.next().getHighlightFields()))
				.collect(Collectors.toList());

		Map<String, FavouriteItemDto> favouriteItemsLookupMap = findFavouriteItemsMappedByJobAdId(extractJobAdIds(jobAdvertisements));
		List<JobAdvertisementSearchResult> jobAdvertisementSearchResults = mapToJobAdvertisementSearchResults(jobAdvertisements, favouriteItemsLookupMap);

		return new AggregatedPageImpl<>(jobAdvertisementSearchResults, pageable, searchHits.totalHits, searchResults.getAggregations(), searchResults.getScrollId());
	}

	private Map<String, FavouriteItemDto> findFavouriteItemsMappedByJobAdId(Set<JobAdvertisementId> jobAdvertisementIds) {
		CurrentUser currentUser = currentUserContext.getCurrentUser();
		if (currentUser == null || jobAdvertisementIds.isEmpty()) {
			return Collections.emptyMap();
		}
		String ownerUserId = currentUser.getUserId();
		return this.favouriteItemRepository
				.findByJobAdvertisementIdsAndOwnerId(jobAdvertisementIds, ownerUserId).stream()
				.map(FavouriteItemDto::toDto)
				.collect(Collectors.toMap(FavouriteItemDto::getJobAdvertisementId, Function.identity()));
	}

	private Set<JobAdvertisementId> extractJobAdIds(List<JobAdvertisementDto> jobAdvertisementDtoList) {
		return jobAdvertisementDtoList.stream()
				.map(jobAdvertisementDto -> new JobAdvertisementId(jobAdvertisementDto.getId()))
				.collect(Collectors.toSet());
	}

	private Pageable appendUniqueSortingKey(Pageable pageable) {
		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()
				.and(Sort.by(Sort.Direction.DESC, PATH_CREATED_TIME))
		);
	}

	private AggregatedPageImpl<JobAdvertisementDto> extractHighlightedResults(Pageable pageable, SearchResponse response) {
		AggregatedPage<JobAdvertisementDocument> searchResults = resultsMapper.mapResults(response, JobAdvertisementDocument.class, pageable);
		SearchHits searchHits = response.getHits();
		Iterator<SearchHit> searchHitIterator = searchHits.iterator();

		List<JobAdvertisementDto> highlightedResults = searchResults.getContent().stream()
				.map(JobAdvertisementDocument::getJobAdvertisement)
				.map(JobAdvertisementDto::toDtoWithOwner)
				.map(jobAdvertisementDto -> highlightManagedFields(jobAdvertisementDto, searchHitIterator.next().getHighlightFields()))
				.collect(Collectors.toList());

		return new AggregatedPageImpl<>(highlightedResults, pageable, searchHits.totalHits, searchResults.getAggregations(), searchResults.getScrollId());
	}

	private QueryBuilder createManagedJobAdsKeywordsQuery(String keywordsText) {
		if (isBlank(keywordsText)) {
			return matchAllQuery();
		}

		String[] keywords = keywordsText.split(MANAGED_JOB_AD_KEYWORD_DELIMITER);

		BoolQueryBuilder keywordQuery = boolQuery();
		Stream.of(keywords)
				.flatMap(keyword -> Stream.of(
						multiMatchQuery(keyword, PATH_OWNER_USER_DISPLAY_NAME, PATH_LOCATION_CITY)
								.field(PATH_TITLE, 1.5f).type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX),
						termQuery(PATH_AVAM_JOB_ID, keyword).boost(10f),
						termQuery(PATH_EGOV_JOB_ID, keyword).boost(10f)
				))
				.forEach(keywordQuery::should);

		String allKeywords = String.join(" ", keywords);
		if (isNotBlank(allKeywords)) {
			keywordQuery.should(multiMatchQuery(allKeywords, PATH_OWNER_USER_DISPLAY_NAME, PATH_LOCATION_CITY, PATH_TITLE)
					.boost(2f)
					.operator(Operator.AND));
		}

		return mustAll(keywordQuery);
	}

	private Sort createSort(SearchSort sort) {
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

	private NativeSearchQueryBuilder createSearchQueryBuilder(JobAdvertisementSearchRequest jobSearchRequest) {
		BoolQueryBuilder boolQueryBuilder = createQuery(jobSearchRequest);
		BoolQueryBuilder filterQueryBuilder = createFilter(jobSearchRequest);

		if (isRadiusNeeded(jobSearchRequest)) {
			boolQueryBuilder.must(prepareBoostedFunctionQuery(jobSearchRequest));
		} else {

			if (isCantonSearch(jobSearchRequest.getCantonCodes())) {
				filterQueryBuilder.must(termsQuery(PATH_LOCATION_CANTON_CODE, jobSearchRequest.getCantonCodes()));
			}

			if (isMultipleLocationSearch(jobSearchRequest)) {
				filterQueryBuilder.must(termsQuery(PATH_LOCATION_COMMUNAL_CODE, jobSearchRequest.getCommunalCodes()));
			}

			if (isSingleLocationAbroadSearch(jobSearchRequest)) {
				filterQueryBuilder
						.must(existsQuery(PATH_LOCATION_COUNTRY_ISO_CODE))
						.mustNot(termsQuery(PATH_LOCATION_COUNTRY_ISO_CODE, SWITZERLAND_COUNTRY_ISO_CODE));
			}
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
			case SBN3:
				path = PATH_OCCUPATIONS_SBN3_CODE;
				break;
			case SBN5:
				path = PATH_OCCUPATIONS_SBN5_CODE;
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
						.operator(Operator.AND));
			}
		}

		return keywordQuery;
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

	private BoolQueryBuilder publicationStartDateFilter(Integer onlineSinceDays) {
		if (onlineSinceDays == null) {
			return boolQuery();
		}
		return boolQuery().must(
				rangeQuery(PATH_PUBLICATION_START_DATE).gte(String.format("now-%sd/d", onlineSinceDays)));
	}

	private BoolQueryBuilder companyFilter(String companyName) {
		BoolQueryBuilder companyFilter = boolQuery();

		if (isNotBlank(companyName)) {
			companyFilter.must(matchPhrasePrefixQuery(PATH_COMPANY_NAME, companyName));
		}

		return companyFilter;
	}

	private BoolQueryBuilder ownerCompanyIdFilter(String companyId) {
		if (isBlank(companyId)) {
			return boolQuery();
		}
		return boolQuery().must(termQuery(PATH_OWNER_COMPANY_ID, companyId));
	}

	private BoolQueryBuilder ownerUserIdFilter(String userId) {
		if (isBlank(userId)) {
			return boolQuery();
		}
		return boolQuery().must(termQuery(PATH_OWNER_USER_ID, userId));
	}

	private BoolQueryBuilder stateFilter(JobAdvertisementStatus state) {
		if (state == null) {
			return boolQuery();
		}
		return boolQuery().must(termQuery(PATH_STATUS, state.name()));
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

	private static BoolQueryBuilder mustAll(BoolQueryBuilder... queryBuilders) {
		return Stream.of(queryBuilders)
				.filter(BoolQueryBuilder::hasClauses)
				.reduce(boolQuery(), BoolQueryBuilder::must);
	}

	private static JobAdvertisementDto highlightFields(JobAdvertisementDto jobAdvertisementDto, Map<String, HighlightField> highlightFieldMap) {
		for (JobDescriptionDto jobDescriptionDto : jobAdvertisementDto.getJobContent().getJobDescriptions()) {

			if (highlightFieldMap.containsKey(PATH_TITLE)) {
				jobDescriptionDto.setTitle(highlightFieldMap.get(PATH_TITLE).getFragments()[0].toString());
			}

			if (highlightFieldMap.containsKey(PATH_DESCRIPTION)) {
				jobDescriptionDto.setDescription(highlightFieldMap.get(PATH_DESCRIPTION).getFragments()[0].toString());
			}

		}

		return jobAdvertisementDto;
	}

	private static JobAdvertisementDto highlightManagedFields(JobAdvertisementDto jobAdvertisementDto, Map<String, HighlightField> highlightFieldMap) {
		highlightFieldMap.keySet().forEach(key -> {
			String keyFragment = highlightFieldMap.get(key).getFragments()[0].toString();
			switch (key) {
				case PATH_OWNER_USER_DISPLAY_NAME:
					jobAdvertisementDto.getOwner().setUserDisplayName(keyFragment);
					break;
				case PATH_LOCATION_CITY:
					jobAdvertisementDto.getJobContent().getLocation().setCity(keyFragment);
					break;
				case PATH_TITLE:
					jobAdvertisementDto.getJobContent().getJobDescriptions().forEach(dto -> dto.setTitle(keyFragment));
					break;
				case PATH_AVAM_JOB_ID:
					jobAdvertisementDto.setStellennummerAvam(keyFragment);
					break;
				case PATH_EGOV_JOB_ID:
					jobAdvertisementDto.setStellennummerEgov(keyFragment);
					break;
			}
		});
		return jobAdvertisementDto;
	}

	private List<JobAdvertisementSearchResult> mapToJobAdvertisementSearchResults(List<JobAdvertisementDto> jobAdvertisements, Map<String, FavouriteItemDto> favouriteItems) {
		return jobAdvertisements.stream()
				.map(jobAdvertisementDto -> {
					FavouriteItemDto favouriteItemDto = favouriteItems.get(jobAdvertisementDto.getId());
					return new JobAdvertisementSearchResult(jobAdvertisementDto, favouriteItemDto);
				})
				.collect(Collectors.toList());
	}

	private boolean isRadiusNeeded(JobAdvertisementSearchRequest jobSearchRequest) {
		return jobSearchRequest.getRadiusSearchRequest() != null;
	}

	private boolean isMultipleLocationSearch(JobAdvertisementSearchRequest jobSearchRequest) {
		return isNotEmpty(jobSearchRequest.getCommunalCodes()) && jobSearchRequest.getCommunalCodes().length > 1;
	}

	private boolean canViewRestrictedJobAds() {
		return this.currentUserContext.hasAnyRoles(Role.JOBSEEKER_CLIENT, Role.SYSADMIN);
	}

	private FunctionScoreQueryBuilder prepareBoostedFunctionQuery(JobAdvertisementSearchRequest jobSearchRequest) {
		RadiusSearchRequest radiusSearchRequest = jobSearchRequest.getRadiusSearchRequest();
		GeoDistanceQueryBuilder geoDistanceQueryBuilder = getDistanceQuery(radiusSearchRequest);
		GaussDecayFunctionBuilder gaussDecayFunctionBuilder = getGaussDecayFunctionBuilder(radiusSearchRequest);
		return functionScoreQuery(geoDistanceQueryBuilder, gaussDecayFunctionBuilder).boost(2f);
	}

	private GaussDecayFunctionBuilder getGaussDecayFunctionBuilder(RadiusSearchRequest radiusSearchRequest) {
		return gaussDecayFunction(PATH_LOCATION_COORDINATES, radiusSearchRequest.getGeoPoint().toString(), SCALE_IN_KM, OFFSET_IN_KM, DECAY_RATE);
	}

	private GeoDistanceQueryBuilder getDistanceQuery(RadiusSearchRequest radiusSearchRequest) {
		return geoDistanceQuery(PATH_LOCATION_COORDINATES)
				.point(radiusSearchRequest.getGeoPoint().getLat(), radiusSearchRequest.getGeoPoint().getLon())
				.distance(radiusSearchRequest.getDistance(), KILOMETERS);
	}

	private boolean isSingleLocationAbroadSearch(JobAdvertisementSearchRequest jobSearchRequest) {
		return isNotEmpty(jobSearchRequest.getCommunalCodes())
				&& Arrays.asList(jobSearchRequest.getCommunalCodes()).contains(COMMUNAL_CODE_ABROAD)
				&& jobSearchRequest.getCommunalCodes().length == 1;
	}

	private boolean isCantonSearch(String[] cantonCodes) {
		return isNotEmpty(cantonCodes);
	}

}
