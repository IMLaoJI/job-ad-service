package ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.jobadvertisement.write;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ParentRelation;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.INDEX_NAME_JOB_ADVERTISEMENT;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.common.ElasticsearchIndexService.TYPE_DOC;

@Document(indexName = INDEX_NAME_JOB_ADVERTISEMENT, type = TYPE_DOC, shards = 1, replicas = 0)
@Mapping(mappingPath = "config/elasticsearch/mappings/job-advertisement.json")
@Setting(settingPath = "config/elasticsearch/settings/folding-analyzer.json")
public class JobAdvertisementDocument {

    public static final String JOB_ADVERTISEMENT_PARENT_RELATION_NAME = "rel_jobAdvertisement";

    @Id
    private String id;

    private JobAdvertisement jobAdvertisement;

    private ParentRelation jobAdvertisementRelations;

    protected JobAdvertisementDocument() {
    }

    public JobAdvertisementDocument(JobAdvertisement jobAdvertisement) {
        this.jobAdvertisement = jobAdvertisement;
        this.id = jobAdvertisement.getId().getValue();
        this.jobAdvertisementRelations = new ParentRelation.Builder()
                .setName(JOB_ADVERTISEMENT_PARENT_RELATION_NAME)
                .build();
    }

    public String getId() {
        return id;
    }

    public JobAdvertisement getJobAdvertisement() {
        return jobAdvertisement;
    }

    public ParentRelation getJobAdvertisementRelations() {
        return jobAdvertisementRelations;
    }
}
