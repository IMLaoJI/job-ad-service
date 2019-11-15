package ch.admin.seco.jobs.services.jobadservice.integration.external.importer;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.task.configuration.EnableTask;

import ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config.ExternalJobAdvertisementProperties;

@SpringBootApplication
@EnableTask
@EnableBatchProcessing
@EnableBinding(Source.class)
@EnableConfigurationProperties(ExternalJobAdvertisementProperties.class)
public class ExternalJobAdvertisementImportTask {

    public static void main(String[] args) {
        SpringApplication.run(ExternalJobAdvertisementImportTask.class, args);
    }
}
