package ch.admin.seco.jobs.services.jobadservice.integration.external.exporter.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceConfig.class);

    /*
     * The batch data source is passed from SCDF as default data source.
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "job-ad-export.datasource.batch")
    DataSourceProperties batchDataSourceProperties() {
        return new DataSourceProperties();
    }

    /*
     * Initialize Batch Metadata DataSource.
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "job-ad-export.datasource.batch.hikari")
    DataSource batchDataSource() {
        return batchDataSourceProperties().initializeDataSourceBuilder().build();
    }

    /*
     * Define data source for JobAdvertisement export with custom prefix.
     */
    @Bean
    @ConfigurationProperties("job-ad-export.datasource.jobadservice")
    DataSourceProperties jobAdServiceDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("job-ad-export.datasource.jobadservice.hikari")
    DataSource jobAdServiceDataSource() {
        DataSourceProperties jobAdServiceDataSourceProperties = jobAdServiceDataSourceProperties();
        LOG.info("Building jobAdServiceDataSource ({})", jobAdServiceDataSourceProperties.getUrl());
        return jobAdServiceDataSourceProperties.initializeDataSourceBuilder().build();
    }

}
