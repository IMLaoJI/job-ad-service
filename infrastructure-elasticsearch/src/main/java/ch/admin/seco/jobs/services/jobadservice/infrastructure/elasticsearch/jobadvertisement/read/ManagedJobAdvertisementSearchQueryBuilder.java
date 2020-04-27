package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.read;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementStatus;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.elasticsearch.index.query.QueryBuilders.*;

@Component
public class ManagedJobAdvertisementSearchQueryBuilder extends SearchQueryBuilder {

	private static final String MANAGED_JOB_AD_KEYWORD_DELIMITER = "\\s+";
	private static final String PATH_OWNER_COMPANY_ID = PATH_CTX + "owner.companyId";
	private static final String PATH_OWNER_USER_ID = PATH_CTX + "owner.userId";

	QueryBuilder createManagedJobAdsKeywordsQuery(String keywordsText, String companyId) {
		if (isBlank(keywordsText)) {
			return boolQuery().must(termQuery(PATH_OWNER_COMPANY_ID, companyId));
		}

		String[] keywords = keywordsText.split(MANAGED_JOB_AD_KEYWORD_DELIMITER);

		BoolQueryBuilder boolQuery = boolQuery();
		for (String keyword : keywords) {
			boolQuery.should(prefixQuery(PATH_TITLE + ".keyword", keyword));
			boolQuery.should(prefixQuery(PATH_LOCATION_CITY + ".keyword", keyword));
			boolQuery.should(prefixQuery(PATH_OWNER_USER_DISPLAY_NAME, keyword));

			boolQuery.should(fuzzyQuery(PATH_TITLE + ".keyword", keyword).fuzziness(Fuzziness.ONE));
			boolQuery.should(fuzzyQuery(PATH_OWNER_USER_DISPLAY_NAME + ".keyword", keyword).fuzziness(Fuzziness.ONE));
			boolQuery.should(fuzzyQuery(PATH_LOCATION_CITY + ".keyword", keyword).fuzziness(Fuzziness.ONE));

			boolQuery.should(termQuery(PATH_AVAM_JOB_ID, keyword).boost(10f));
			boolQuery.should(termQuery(PATH_EGOV_JOB_ID, keyword).boost(10f));
		}


		BoolQueryBuilder keywordQuery = boolQuery();
		keywordQuery.must(termQuery(PATH_OWNER_COMPANY_ID, companyId));
		keywordQuery.must(boolQuery);

		String allKeywords = String.join(" ", keywords);
		if (isNotBlank(allKeywords)) {
			keywordQuery.should(multiMatchQuery(allKeywords, PATH_OWNER_USER_DISPLAY_NAME, PATH_LOCATION_CITY, PATH_TITLE)
					.boost(2f)
					.operator(Operator.AND));
		}
		return keywordQuery;
	}

	BoolQueryBuilder ownerUserIdFilter(String userId) {
		if (isBlank(userId)) {
			return boolQuery();
		}
		return boolQuery().must(termQuery(PATH_OWNER_USER_ID, userId));
	}

	BoolQueryBuilder stateFilter(JobAdvertisementStatus state) {
		if (state == null) {
			return boolQuery();
		}
		return boolQuery().must(termQuery(PATH_STATUS, state.name()));
	}
}
