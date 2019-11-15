package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external.ExternalCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.integration.external.jobadimport.Oste;
import ch.admin.seco.jobs.services.jobadservice.integration.external.jobadimport.OsteList;

@Configuration
public class ExternalJobAdvertisementImportTaskConfig {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalJobAdvertisementImportTaskConfig.class);
    private static final String PARAMETER_XML_FILE_PATH = "XML_FILE_PATH";
    private static final String PARAMETER_LAST_MODIFIED_TIME = "LAST_MODIFIED_TIME";

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final MessageChannel messageBrokerOutputChannel;

    private final MessageSource<File> externalJobAdvertisementDataFileMessageSource;

    private final Validator validator;

    @Autowired
    public ExternalJobAdvertisementImportTaskConfig(
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            MessageSource<File> externalJobAdvertisementDataFileMessageSource,
            MessageChannel output,
            Validator validator) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.externalJobAdvertisementDataFileMessageSource = externalJobAdvertisementDataFileMessageSource;
        this.messageBrokerOutputChannel = output;
        this.validator = validator;
    }

    @Bean
    public Job externalImportJob(StaxEventItemReader<Oste> xmlFileReader, ExternalJobAdvertisementWriter externalJobAdvertisementWriter, ExternalJobAdvertisementProperties externalJobAdvertisementProperties) {
        return jobBuilderFactory.get("external-jobad-xml-import")
                .incrementer(new RunIdIncrementer())
                .listener(new CleanupXmlFileJobExecutionListener())
                .start(stepBuilderFactory
                        .get("download-from-sftp")
                        .allowStartIfComplete(true)
                        .tasklet(downloadFromSftpServer(externalJobAdvertisementProperties))
                        .build())
                .on("NO_FILE").end()
                .on("*")
                .to(stepBuilderFactory
                        .get("send-to-job-ad-service")
                        .listener(itemLoggerListener())
                        .<Oste, ExternalCreateJobAdvertisementDto>chunk(10)
                        .reader(xmlFileReader)
                        .processor(externalItemProcessor())
                        .writer(externalJobAdvertisementWriter)
                        .build())
                .build()
                .build();
    }

    @Bean
    public ItemLoggerListener itemLoggerListener() {
        return new ItemLoggerListener();
    }

    @Bean
    public ExternalItemProcessor externalItemProcessor() {
        return new ExternalItemProcessor(validator);
    }

    @Bean
    public Tasklet downloadFromSftpServer(ExternalJobAdvertisementProperties externalJobAdvertisementProperties) {
        return (contribution, chunkContext) -> {
            LOG.info("Downloading from SFTP Server ('{}:{}/{}')",
                    externalJobAdvertisementProperties.getHost(),
                    externalJobAdvertisementProperties.getPort(),
                    externalJobAdvertisementProperties.getRemoteDirectory());

            Message<File> externalJobAdDataFileMessage = externalJobAdvertisementDataFileMessageSource.receive();
            if ((externalJobAdDataFileMessage == null) || (externalJobAdDataFileMessage.getPayload() == null)) {

                LOG.error("external JobAd data file not available");
                contribution.setExitStatus(new ExitStatus("NO_FILE"));
                return RepeatStatus.FINISHED;
            }

            File originFile = externalJobAdDataFileMessage.getPayload();

            LOG.info("Downloaded from SFTP Server file '{}'", originFile.getName());

            File xmlFile = unzip(originFile);
            if (!xmlFile.equals(originFile)) {
                delete(originFile);
            }

            ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
            executionContext.put(PARAMETER_XML_FILE_PATH, xmlFile);
            executionContext.put(PARAMETER_LAST_MODIFIED_TIME, getLastModifiedTime(xmlFile));
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Jaxb2Marshaller ExternalMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(OsteList.class);
        return jaxb2Marshaller;
    }

    @Bean
    @JobScope
    public StaxEventItemReader<Oste> xmlFileReader(@Value("#{jobExecutionContext['" + PARAMETER_XML_FILE_PATH + "']}") File xmlFile) {
        return new StaxEventItemReaderBuilder<Oste>()
                .resource(new PathResource(xmlFile.toPath()))
                .unmarshaller(ExternalMarshaller())
                .strict(false)
                .saveState(false)
                .addFragmentRootElements("oste")
                .build();
    }

    @Bean
    public ExternalJobAdvertisementWriter externalJobAdvertisementWriter() {
        return new ExternalJobAdvertisementWriter(messageBrokerOutputChannel);
    }

    private void delete(File file) {
        LOG.debug("Deleting file '{}'", file.getAbsolutePath());

        if (Files.isWritable(file.toPath())) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                file.deleteOnExit();
                LOG.error("Failed to delete file {}", file.getAbsolutePath());
            }
        }
    }

    private File unzip(File originFile) throws IOException {
        if (originFile.getName().endsWith(".zip")) {
            ZipFile zipFile = new ZipFile(originFile);
            ZipEntry zipEntry = zipFile.entries().nextElement();

            File targetFile = Files.createTempFile(originFile.toPath().getParent(), null, null).toFile();
            ZipUtil.unpackEntry(zipFile, zipEntry.getName(), targetFile);
            targetFile.setLastModified(zipEntry.getLastModifiedTime().toMillis());
            return targetFile;
        } else {
            return originFile;
        }
    }

    private Date getLastModifiedTime(File xmlFile) {
        try {
            return new Date(Files.getLastModifiedTime(xmlFile.toPath()).toMillis());
        } catch (IOException e) {
            return new Date();
        }
    }

    private class CleanupXmlFileJobExecutionListener extends JobExecutionListenerSupport {
        @Override
        public void afterJob(JobExecution jobExecution) {
            if (jobExecution.getExecutionContext().containsKey(PARAMETER_XML_FILE_PATH)) {

                File xmlFile = (File) (jobExecution.getExecutionContext().get(PARAMETER_XML_FILE_PATH));
                delete(xmlFile);
            }
        }
    }

    private static class ItemLoggerListener extends ItemListenerSupport<Oste, Oste> {
        private static final Logger LOGGER = LoggerFactory.getLogger(ItemLoggerListener.class);

        @Override
        public void afterRead(Oste item) {
            LOGGER.debug("Successfully read external xml: {}", item);
        }

        @Override
        public void onReadError(Exception ex) {
            LOGGER.error("external read failure", ex);
        }

        @Override
        public void afterWrite(List<? extends Oste> item) {
            LOGGER.debug("external xml JobAdvertisements successfully sent: {}", item);
        }

        @Override
        public void onWriteError(Exception ex, List<? extends Oste> item) {
            LOGGER.error("external xml JobAdvertisement write failed: {}", item);
            LOGGER.error("external xml JobAdvertisement write failure", ex);
        }
    }
}
