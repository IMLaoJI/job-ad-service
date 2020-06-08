package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read;

import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.dto.FavouriteItemDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementSearchResult;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.JobAdvertisementSearchService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.ManagedJobAdSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.JobDescriptionDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.search.JobAdvertisementSearchRequest;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUser;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.domain.favouriteitem.FavouriteItemRepository;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.ElasticsearchConfiguration;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementDocument;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
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
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemDocument.FAVOURITE_ITEM_RELATION_NAME;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read.JobAdvertisementSearchQueryBuilder.mustAll;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchPhrasePrefixQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.join.query.JoinQueryBuilders.hasChildQuery;
import static org.springframework.data.domain.Sort.Order.desc;

@Service
public class ElasticJobAdvertisementSearchService implements JobAdvertisementSearchService {


	private static Logger LOG = LoggerFactory.getLogger(ElasticJobAdvertisementSearchService.class);

	private static final String PATH_CREATED_TIME = "jobAdvertisement.createdTime";
	private static final String PATH_CTX = "jobAdvertisement.";
	private static final String PATH_PUBLICATION_START_DATE = PATH_CTX + "publication.startDate";
	private static final String PATH_AVAM_JOB_ID = PATH_CTX + "stellennummerAvam";
	private static final String PATH_EGOV_JOB_ID = PATH_CTX + "stellennummerEgov";
	private static final String PATH_OWNER_USER_DISPLAY_NAME = PATH_CTX + "owner.userDisplayName";
	private static final String PATH_DESCRIPTION = PATH_CTX + "jobContent.jobDescriptions.description";
	private static final String PATH_LOCATION_CITY = PATH_CTX + "jobContent.location.city";
	private static final String PATH_TITLE = PATH_CTX + "jobContent.jobDescriptions.title";

	private final CurrentUserContext currentUserContext;

	private final ElasticsearchTemplate elasticsearchTemplate;

	private final ResultsMapper resultsMapper;

	private final FavouriteItemRepository favouriteItemRepository;

	private final JobAdvertisementSearchQueryBuilder jobAdvertisementSearchQueryBuilder;

	private final ManagedJobAdvertisementSearchQueryBuilder managedJobAdvertisementSearchQueryBuilder;

	public ElasticJobAdvertisementSearchService(CurrentUserContext currentUserContext,
	                                            ElasticsearchTemplate elasticsearchTemplate,
	                                            ElasticsearchConfiguration.CustomEntityMapper customEntityMapper,
	                                            FavouriteItemRepository favouriteItemRepository,
	                                            JobAdvertisementSearchQueryBuilder jobAdvertisementSearchQueryBuilder,
	                                            ManagedJobAdvertisementSearchQueryBuilder managedJobAdvertisementSearchQueryBuilder) {
		this.currentUserContext = currentUserContext;
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.favouriteItemRepository = favouriteItemRepository;
		this.jobAdvertisementSearchQueryBuilder = jobAdvertisementSearchQueryBuilder;
		this.managedJobAdvertisementSearchQueryBuilder = managedJobAdvertisementSearchQueryBuilder;
		this.resultsMapper = new DefaultResultMapper(elasticsearchTemplate.getElasticsearchConverter().getMappingContext(), customEntityMapper);
	}

	@Override
	public Page<JobAdvertisementSearchResult> search(JobAdvertisementSearchRequest jobSearchRequestDto, int page, int size, SearchSort sort) {

		final Pageable pageRequest = jobAdvertisementSearchQueryBuilder.createPageRequest(page, size, sort);
		final NativeSearchQuery searchQuery = jobAdvertisementSearchQueryBuilder.createSearchQuery(jobSearchRequestDto, pageRequest);
		if (LOG.isTraceEnabled()) {
			LOG.trace("query: {}", searchQuery.getQuery());
			LOG.trace("filter: {}", searchQuery.getFilter());
		}
		return elasticsearchTemplate.query(searchQuery, response -> extractSearchResult(pageRequest, response));
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
                .withQuery(managedJobAdvertisementSearchQueryBuilder.
		                createManagedJobAdsKeywordsQuery(searchRequest.getKeywordsText(),
				                searchRequest.getCompanyId()))
                .withFilter(mustAll(
		                managedJobAdvertisementSearchQueryBuilder.publicationStartDateFilter(searchRequest.getOnlineSinceDays()),
		                managedJobAdvertisementSearchQueryBuilder.ownerUserIdFilter(searchRequest.getOwnerUserId()),
		                managedJobAdvertisementSearchQueryBuilder.stateFilter(searchRequest.getState())))
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
}
