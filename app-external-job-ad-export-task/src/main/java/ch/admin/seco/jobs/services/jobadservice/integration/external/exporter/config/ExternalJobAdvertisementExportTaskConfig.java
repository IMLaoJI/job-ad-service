package ch.admin.seco.jobs.services.jobadservice.integration.external.exporter.config;

import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.integration.external.jobadexport.Oste;
import ch.admin.seco.jobs.services.jobadservice.integration.external.jobadexport.OsteList;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.zeroturnaround.zip.ZipUtil;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Configuration
public class ExternalJobAdvertisementExportTaskConfig {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalJobAdvertisementExportTaskConfig.class);
    private static final String PARAMETER_XML_FILE_PATH = "XML_FILE_PATH";
    private static final String NAMESPACE_URI = "http://jobroom.seco.admin.ch/oste/1.4";
    private static final String ROOT_TAG_NAME = String.format("{%s}osteList", NAMESPACE_URI);
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SftpMessageHandler sftpMessageHandler;
    private final ExternalJobAdvertisementProperties externalJobAdvertisementProperties;

    @Autowired
    public ExternalJobAdvertisementExportTaskConfig(
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            SftpMessageHandler sftpMessageHandler,
            ExternalJobAdvertisementProperties externalJobAdvertisementProperties) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.sftpMessageHandler = sftpMessageHandler;
        this.externalJobAdvertisementProperties = externalJobAdvertisementProperties;
    }

    @Bean
    public Job externalExportJob(JpaPagingItemReader<JobAdvertisement> jpaPagingItemReader,
                                 ExternalJobAdvertisementTransformer externalJobAdvertisementTransformer,
                                 StaxEventItemWriter<Oste> xmlWriter,
                                 PlatformTransactionManager jobAdTransactionManager) {
        return jobBuilderFactory.get("external-jobad-xml-export")
                .incrementer(new RunIdIncrementer())
                .listener(new PrepareAndCleanupXmlFileJobExecutionListener())
                .start(stepBuilderFactory
                        .get("generate-xml-file")
                        .listener(itemLoggerListener())
                        .<JobAdvertisement, Oste>chunk(10)
                        .reader(jpaPagingItemReader).readerIsTransactionalQueue()
                        .processor(externalJobAdvertisementTransformer)
                        .writer(xmlWriter)
                        .transactionManager(jobAdTransactionManager)
                        .build())
                .next(stepBuilderFactory
                        .get("zip-and-upload-to-external")
                        .tasklet(uploadToSftpServer())
                        .build())
                .build();
    }

    @Bean
    public ItemLoggerListener itemLoggerListener() {
        return new ItemLoggerListener();
    }

    @Bean
    public Tasklet uploadToSftpServer() {
        return (contribution, chunkContext) -> {
            LOG.info("Uploading jobs to SFTP Server: '{}:{}/{}'",
                    externalJobAdvertisementProperties.getHost(), externalJobAdvertisementProperties.getPort(), externalJobAdvertisementProperties.getRemoteDirectory());
            File xmlFile = (File) chunkContext.getStepContext().getJobExecutionContext().get(PARAMETER_XML_FILE_PATH);

            File zipFile = zip(xmlFile, externalJobAdvertisementProperties.getXmlFileName());
            String remoteFileName = FilenameUtils.getBaseName(externalJobAdvertisementProperties.getXmlFileName()) + ".zip";

            LOG.info("Sending zip '{}' to SFTP", remoteFileName);
            sftpMessageHandler.handleMessage(
                    MessageBuilder.withPayload(zipFile)
                            .setHeader(FileHeaders.FILENAME, remoteFileName)
                            .build());

            LOG.debug("Delete xml file '{}'", xmlFile.getAbsolutePath());
            delete(xmlFile);
            LOG.debug("Delete zip file '{}'", zipFile.getAbsolutePath());
            delete(zipFile);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    JpaPagingItemReader<JobAdvertisement> jpaPagingItemReader(EntityManagerFactory jobAdServiceEntityManagerFactory) {
        JpaPagingItemReader<JobAdvertisement> jpaPagingItemReader = new JpaPagingItemReader<>();
        // FIXME Workaround because Eures interface only gets job ads from External
        jpaPagingItemReader.setQueryString("select j from JobAdvertisement j " +
                "where j.status = 'PUBLISHED_PUBLIC' and " +
                "j.sourceSystem in ('API', 'JOBROOM', 'RAV', 'EXTERN') and " +
                "(j.publication.publicDisplay is TRUE OR j.publication.euresDisplay is TRUE)" +
                "order by j.id");
        jpaPagingItemReader.setEntityManagerFactory(jobAdServiceEntityManagerFactory);
        jpaPagingItemReader.setPageSize(10);
        jpaPagingItemReader.setTransacted(true);
        jpaPagingItemReader.setSaveState(true);

        return jpaPagingItemReader;
    }

    @Bean
    ExternalJobAdvertisementTransformer externalJobAdvertisementTransformer() {
        return new ExternalJobAdvertisementTransformer();
    }

    @Bean
    @JobScope
    StaxEventItemWriter<Oste> xmlFileReader(@Value("#{jobExecutionContext['" + PARAMETER_XML_FILE_PATH + "']}") File xmlFile) {
        return new StaxEventItemWriterBuilder<Oste>()
                .name("external-xml-export-writer")
                .resource(new PathResource(xmlFile.toPath()))
                .marshaller(ExternalMarshaller())
                .rootTagName(ROOT_TAG_NAME)
                .rootElementAttributes(Collections.singletonMap("timestamp", TimeMachine.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .saveState(false)
                .overwriteOutput(true)
                .build();
    }

    @Bean
    Jaxb2Marshaller ExternalMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(OsteList.class, Oste.class);
        return jaxb2Marshaller;
    }

    private void delete(File file) {
        if (Files.isWritable(file.toPath())) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                file.deleteOnExit();
                LOG.error("Failed to delete file {}", file.getAbsolutePath());
            }
        }
    }

    private File zip(File xmlFile, String xmlFileName) throws IOException {
        LOG.debug("Zip xml file '{}'", xmlFileName);
        Path targetFile = Files.createTempFile(xmlFile.toPath().getParent(), null, ".zip");
        Files.deleteIfExists(targetFile);
        ZipUtil.packEntry(xmlFile, targetFile.toFile(), xmlFileName);
        return targetFile.toFile();
    }

    private class PrepareAndCleanupXmlFileJobExecutionListener extends JobExecutionListenerSupport {
        @Override
        public void afterJob(JobExecution jobExecution) {
            if (jobExecution.getExecutionContext().containsKey(PARAMETER_XML_FILE_PATH)) {
                File xmlFile = (File) (jobExecution.getExecutionContext().get(PARAMETER_XML_FILE_PATH));
                LOG.info("Cleanup xml file '{}'", xmlFile.getAbsolutePath());
                delete(xmlFile);
            }
        }

        @Override
        public void beforeJob(JobExecution jobExecution) {
            try {
                Path tempFolder = Paths.get(externalJobAdvertisementProperties.getLocalDirectory());
                Files.createDirectories(tempFolder);
                Path tempXmlFilePath = Files.createTempFile(tempFolder, null, ".xml")
                        .toAbsolutePath();
                LOG.info("Create temp xml file: {}", tempXmlFilePath);
                jobExecution.getExecutionContext().put(PARAMETER_XML_FILE_PATH, tempXmlFilePath.toFile());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static class ItemLoggerListener extends ItemListenerSupport<JobAdvertisement, Oste> {
        private static final Logger LOGGER = LoggerFactory.getLogger(ItemLoggerListener.class);

        @Override
        public void afterRead(JobAdvertisement item) {
            LOGGER.debug("Successfully read JobAdvertisement: {}", item);
        }

        @Override
        public void onReadError(Exception ex) {
            LOGGER.error("JobAdvertisement read failure", ex);
        }

        @Override
        public void onProcessError(JobAdvertisement item, Exception e) {
            LOGGER.error("JobAdvertisement ({}) process failed", item);
            LOGGER.error("JobAdvertisement process failure", e);
        }

        @Override
        public void afterWrite(List<? extends Oste> item) {
            LOGGER.debug("external xml successfully written: {}", item);
        }

        @Override
        public void onWriteError(Exception ex, List<? extends Oste> item) {
            LOGGER.error("external xml write failed: {}", item);
            LOGGER.error("external xml write failure", ex);
        }
    }
}
