package ch.admin.seco.jobs.services.jobadservice.integration.external.exporter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

/**
 * Custom Transaction Management for Spring-Batch
 */
@Configuration
@EnableTransactionManagement
public class BatchTXManagementConfiguration implements TransactionManagementConfigurer {

    private final DataSource batchDataSource;

    public BatchTXManagementConfiguration(DataSource batchDataSource) {
        this.batchDataSource = batchDataSource;
    }

    @Bean
    PlatformTransactionManager batchTransactionManager() {
        return new DataSourceTransactionManager(this.batchDataSource);
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return this.batchTransactionManager();
    }
}
