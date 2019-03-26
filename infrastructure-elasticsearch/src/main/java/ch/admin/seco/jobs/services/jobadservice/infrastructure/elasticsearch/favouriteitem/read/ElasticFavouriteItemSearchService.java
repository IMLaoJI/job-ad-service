package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.read;

import ch.admin.seco.jobs.services.jobadservice.application.favouriteitem.FavouriteItemSearchService;
import ch.admin.seco.jobs.services.jobadservice.application.security.CurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.ElasticsearchConfiguration;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.favouriteitem.write.FavouriteItemElasticsearchRepository;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write.JobAdvertisementElasticsearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.stereotype.Service;

@Service
public class ElasticFavouriteItemSearchService implements FavouriteItemSearchService {

    private static Logger LOG = LoggerFactory.getLogger(ElasticFavouriteItemSearchService.class);

    private final CurrentUserContext currentUserContext;

    private final ElasticsearchTemplate elasticsearchTemplate;

    private final ResultsMapper resultsMapper;

    private final FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository;

    private final JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository;

    public ElasticFavouriteItemSearchService(CurrentUserContext currentUserContext, ElasticsearchTemplate elasticsearchTemplate,
                                             ElasticsearchConfiguration.CustomEntityMapper customEntityMapper,
                                             FavouriteItemElasticsearchRepository favouriteItemElasticsearchRepository,
                                             JobAdvertisementElasticsearchRepository jobAdvertisementElasticsearchRepository) {
        this.currentUserContext = currentUserContext;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.resultsMapper = new DefaultResultMapper(elasticsearchTemplate.getElasticsearchConverter().getMappingContext(), customEntityMapper);
        this.favouriteItemElasticsearchRepository = favouriteItemElasticsearchRepository;
        this.jobAdvertisementElasticsearchRepository = jobAdvertisementElasticsearchRepository;
    }

}
