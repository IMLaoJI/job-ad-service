package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.apiuser.write;

import ch.admin.seco.jobs.services.jobadservice.domain.apiuser.ApiUser;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import javax.persistence.Id;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.INDEX_NAME_API_USER;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.TYPE_DOC;

@Document(indexName = INDEX_NAME_API_USER, type = TYPE_DOC, shards = 1, replicas = 0)
@Mapping(mappingPath = "config/elasticsearch/mappings/api-user.json")
@Setting(settingPath = "config/elasticsearch/settings/folding-analyzer.json")
public class ApiUserDocument {

	@Id
	private String id;

	private ApiUser apiUser;

	protected ApiUserDocument() {
	}

	public ApiUserDocument(ApiUser apiUser) {
		this.id = apiUser.getId().getValue();
		this.apiUser = apiUser;
	}

	public String getId() {
		return id;
	}

	public ApiUser getApiUser() {
		return apiUser;
	}
}
