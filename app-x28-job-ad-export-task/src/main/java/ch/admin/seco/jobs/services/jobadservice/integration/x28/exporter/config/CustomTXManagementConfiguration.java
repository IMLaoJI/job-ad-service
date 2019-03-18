package ch.admin.seco.jobs.services.jobadservice.integration.x28.exporter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.persistence.EntityManagerFactory;

/**
 * Custom Transaction
 */
@Configuration
@EnableTransactionManagement
public class CustomTXManagementConfiguration implements TransactionManagementConfigurer {

    private final EntityManagerFactory entityManagerFactory;

    public CustomTXManagementConfiguration(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return this.transactionManager();
    }
}
