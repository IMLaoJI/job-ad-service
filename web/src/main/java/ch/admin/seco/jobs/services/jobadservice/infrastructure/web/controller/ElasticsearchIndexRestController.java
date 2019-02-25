package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.application.IsSystemAdmin;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.elasticsearch.write.ElasticsearchIndexService;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.util.HeaderUtil;
import com.codahale.metrics.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/elasticsearch")
public class ElasticsearchIndexRestController {

    private ElasticsearchIndexService elasticsearchIndexService;

    @Autowired
    public ElasticsearchIndexRestController(ElasticsearchIndexService elasticsearchIndexService) {
        this.elasticsearchIndexService = elasticsearchIndexService;
    }

    @PostMapping(value = "/index", produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    @IsSystemAdmin
    public ResponseEntity<Void> reindexAll() {
        elasticsearchIndexService.reindexAll();
        return ResponseEntity.accepted()
                .headers(HeaderUtil.createAlert("elasticsearch.reindex.jobservice.accepted", ""))
                .build();
    }
}
