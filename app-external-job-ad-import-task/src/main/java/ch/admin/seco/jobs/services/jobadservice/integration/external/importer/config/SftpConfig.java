package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.io.File;
import java.util.Properties;

import com.jcraft.jsch.ProxyHTTP;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

@Configuration
public class SftpConfig {

    @Bean
    public DefaultSftpSessionFactory externalSftpSessionFactory(ExternalJobAdvertisementProperties externalJobAdvertisementProperties) {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
        factory.setHost(externalJobAdvertisementProperties.getHost());
        if (nonNull(externalJobAdvertisementProperties.getPort())) {
            factory.setPort(externalJobAdvertisementProperties.getPort());
        }
        if (nonNull(externalJobAdvertisementProperties.getProxyHost())) {
            int proxyPort = defaultIfNull(externalJobAdvertisementProperties.getPort(), 80);
            ProxyHTTP proxyHTTP = new ProxyHTTP(externalJobAdvertisementProperties.getProxyHost(), proxyPort);
            factory.setProxy(proxyHTTP);
        }
        factory.setUser(externalJobAdvertisementProperties.getUsername());
        factory.setPassword(externalJobAdvertisementProperties.getPassword());
        factory.setAllowUnknownKeys(externalJobAdvertisementProperties.getAllowUnknownKeys());
        factory.setSessionConfig(defaultSessionConfig());
        factory.setClientVersion("SSH-2.0-SFTP");

        return factory;
    }

    @Bean
    public SftpInboundFileSynchronizingMessageSource externalJobAdDataFileMessageSource(ExternalJobAdvertisementProperties externalJobAdvertisementProperties, DefaultSftpSessionFactory sftpSessionFactory) {
        SftpInboundFileSynchronizer sftpInboundFileSynchronizer = sftpInboundFileSynchronizer(externalJobAdvertisementProperties, sftpSessionFactory);
        SftpInboundFileSynchronizingMessageSource messageSource = new SftpInboundFileSynchronizingMessageSource(sftpInboundFileSynchronizer);
        messageSource.setAutoCreateLocalDirectory(true);
        messageSource.setLocalDirectory(new File(externalJobAdvertisementProperties.getLocalDirectory()));
        return messageSource;
    }

    @Bean
    public SftpInboundFileSynchronizer sftpInboundFileSynchronizer(ExternalJobAdvertisementProperties externalJobAdvertisementProperties, DefaultSftpSessionFactory sftpSessionFactory) {
        SftpInboundFileSynchronizer synchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory);
        synchronizer.setRemoteDirectory(externalJobAdvertisementProperties.getRemoteDirectory());
        synchronizer.setFilter(new SftpSimplePatternFileListFilter(externalJobAdvertisementProperties.getFileNamePattern()));
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
