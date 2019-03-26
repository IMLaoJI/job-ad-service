package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.apiuser.write;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ApiUserElasticsearchRepository extends ElasticsearchRepository<ApiUserDocument, String> {
}
