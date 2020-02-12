package ch.admin.seco.jobs.services.jobadservice.integration.external.exporter.config;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.springframework.util.StringUtils.quote;

import java.util.Properties;

import com.jcraft.jsch.ProxyHTTP;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

@Configuration
@EnableConfigurationProperties(ExternalJobAdvertisementProperties.class)
public class SftpConfig {

    private final ExternalJobAdvertisementProperties externalJobAdvertisementProperties;

    public SftpConfig(ExternalJobAdvertisementProperties externalJobAdvertisementProperties) {
        this.externalJobAdvertisementProperties = externalJobAdvertisementProperties;
    }

    @Bean
    public DefaultSftpSessionFactory externalSftpSessionFactory() {
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
    public SftpMessageHandler sftpMessageHandler() {
        SftpMessageHandler sftpMessageHandler = new SftpMessageHandler(this.externalSftpSessionFactory());
        sftpMessageHandler.setRemoteDirectoryExpressionString(quote(this.externalJobAdvertisementProperties.getRemoteDirectory()));
        return sftpMessageHandler;
    }

    private Properties defaultSessionConfig() {
        Properties properties = new Properties();
        properties.setProperty("StrictHostKeyChecking", "no");
        properties.setProperty("PreferredAuthentications", "password");
        return properties;
    }
}
