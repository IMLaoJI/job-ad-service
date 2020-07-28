package ch.admin.seco.jobs.services.jobadservice.integration.external.exporter.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

import java.util.Properties;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.quote;

@Configuration
@EnableConfigurationProperties(SftpProperties.class)
public class SftpConfig {

    private final SftpProperties sftpProperties;

    public SftpConfig(SftpProperties sftpProperties) {
        this.sftpProperties = sftpProperties;
    }

    @Bean
    public DefaultSftpSessionFactory externalSftpSessionFactory() {
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
    public SftpMessageHandler sftpMessageHandler() {
        SftpMessageHandler sftpMessageHandler = new SftpMessageHandler(this.externalSftpSessionFactory());
        sftpMessageHandler.setRemoteDirectoryExpressionString(quote(this.sftpProperties.getRemoteDirectory()));
        return sftpMessageHandler;
    }

    private Properties defaultSessionConfig() {
        Properties properties = new Properties();
        properties.setProperty("StrictHostKeyChecking", "no");
        properties.setProperty("PreferredAuthentications", "password");
        return properties;
    }
}
