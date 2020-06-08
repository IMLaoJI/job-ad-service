package ch.admin.seco.jobs.services.jobadservice.integration.external.exporter.config;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JPA Configuration to read {@link JobAdvertisement}
 */
@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "jobAdEntityManagerFactory",
        transactionManagerRef = "jobAdTransactionManager",
        basePackageClasses = {JobAdvertisement.class}
)
class CustomJpaConfiguration {

    private final DataSource jobAdServiceDataSource;

    private final JpaProperties jpaProperties;

    private final HibernateProperties hibernateProperties;

    private final EntityManagerFactoryBuilder builder;

    CustomJpaConfiguration(@Qualifier("jobAdServiceDataSource") DataSource jobAdServiceDataSource,
                           JpaProperties jpaProperties,
                           HibernateProperties hibernateProperties,
                           EntityManagerFactoryBuilder builder) {
        this.jobAdServiceDataSource = jobAdServiceDataSource;
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = hibernateProperties;
        this.builder = builder;
    }

    @Bean
    LocalContainerEntityManagerFactoryBean jobAdEntityManagerFactory() {
        return builder
                .dataSource(this.jobAdServiceDataSource)
                .packages(JobAdvertisement.class)
                .persistenceUnit("job-advertisement")
                .properties(getVendorProperties())
                .build();
    }

    @Bean
    PlatformTransactionManager jobAdTransactionManager() {
        return new JpaTransactionManager(this.jobAdEntityManagerFactory().getObject());
    }


    private Map<String, Object> getVendorProperties() {
        Map<String, Object> properties = this.hibernateProperties.determineHibernateProperties(this.jpaProperties.getProperties(), new HibernateSettings());
        return new LinkedHashMap<>(properties);
    }

}
