package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config;

import static java.util.Objects.nonNull;

import java.io.File;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

@Configuration
public class SftpConfig {

    @Bean
    public DefaultSftpSessionFactory externalSftpSessionFactory(SftpProperties sftpProperties) {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
        factory.setHost(sftpProperties.getHost());
        if (nonNull(sftpProperties.getPort())) {
            factory.setPort(sftpProperties.getPort());
        }
        factory.setUser(sftpProperties.getUsername());
        factory.setPassword(sftpProperties.getPassword());
        factory.setAllowUnknownKeys(sftpProperties.getAllowUnknownKeys());
        factory.setSessionConfig(defaultSessionConfig());
        factory.setClientVersion("SSH-2.0-SFTP");

        return factory;
    }

    @Bean
    public SftpInboundFileSynchronizingMessageSource externalJobAdDataFileMessageSource(SftpProperties sftpProperties, DefaultSftpSessionFactory sftpSessionFactory) {
        SftpInboundFileSynchronizer sftpInboundFileSynchronizer = sftpInboundFileSynchronizer(sftpProperties, sftpSessionFactory);
        SftpInboundFileSynchronizingMessageSource messageSource = new SftpInboundFileSynchronizingMessageSource(sftpInboundFileSynchronizer);
        messageSource.setAutoCreateLocalDirectory(true);
        messageSource.setLocalDirectory(new File(sftpProperties.getLocalDirectory()));
        return messageSource;
    }

    @Bean
    public SftpInboundFileSynchronizer sftpInboundFileSynchronizer(SftpProperties sftpProperties, DefaultSftpSessionFactory sftpSessionFactory) {
        SftpInboundFileSynchronizer synchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory);
        synchronizer.setRemoteDirectory(sftpProperties.getRemoteDirectory());
        synchronizer.setFilter(new SftpSimplePatternFileListFilter(sftpProperties.getFileNamePattern()));
        synchronizer.setPreserveTimestamp(true);
        return synchronizer;
    }

    private Properties defaultSessionConfig() {
        Properties properties = new Properties();
        properties.setProperty("StrictHostKeyChecking", "no");
        properties.setProperty("PreferredAuthentications", "password");
        return properties;
    }
}
