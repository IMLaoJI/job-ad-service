package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config.dto.ExternalCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.integration.external.jobadimport.Oste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

public class ExternalItemProcessor implements ItemProcessor<Oste, CreateJobAdvertisementDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalItemProcessor.class);

    private final ExternalJobAdvertisementAssembler externalJobAdvertisementAssembler;

    private final Validator validator;


    ExternalItemProcessor(Validator validator) {
        this.validator = validator;
        this.externalJobAdvertisementAssembler = new ExternalJobAdvertisementAssembler();
    }

    @Override
    public CreateJobAdvertisementDto process(Oste item) {
        ExternalCreateJobAdvertisementDto createItem = externalJobAdvertisementAssembler.createJobAdvertisementFromExternalDto(item);
        final CreateJobAdvertisementDto createJobAdvertisementDto = createItem.toDto(createItem);
        Set<ConstraintViolation<CreateJobAdvertisementDto>> violations = validator.validate(createJobAdvertisementDto);
        if (violations.isEmpty()) {
            return createJobAdvertisementDto;
        }
        LOGGER.warn("Item with Fingerprint: {} has constraint violations: {}", item.getFingerprint(), violations);
        return null;
    }

}
