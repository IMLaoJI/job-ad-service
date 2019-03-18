package ch.admin.seco.jobs.services.jobadservice.integration.x28.exporter.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.task.configuration.DefaultTaskConfigurer;
import org.springframework.cloud.task.configuration.TaskConfigurer;
import org.springframework.cloud.task.repository.support.TaskRepositoryInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class CustomTaskConfigurer {

    private final DataSource batchDataSource;

    public CustomTaskConfigurer(@Qualifier("batchDataSource") DataSource batchDataSource) {
        this.batchDataSource = batchDataSource;
    }

    @Bean
    @DependsOn("customTaskRepositoryInitializer")
    public TaskConfigurer taskConfigurer() {
        return new DefaultTaskConfigurer(this.batchDataSource);
    }

    @Bean
    public TaskRepositoryInitializer customTaskRepositoryInitializer() {
        TaskRepositoryInitializer taskRepositoryInitializer = new TaskRepositoryInitializer();
        taskRepositoryInitializer.setDataSource(this.batchDataSource);
        return taskRepositoryInitializer;
    }

}