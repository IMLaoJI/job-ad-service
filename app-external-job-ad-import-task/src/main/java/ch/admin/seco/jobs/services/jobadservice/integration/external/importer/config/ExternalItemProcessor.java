package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.external.ExternalJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.integration.external.jobadimport.Oste;

public class ExternalItemProcessor implements ItemProcessor<Oste, ExternalJobAdvertisementDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalItemProcessor.class);

    private final ExternalJobAdvertisementAssembler externalJobAdvertisementAssembler;

    private final Validator validator;


    ExternalItemProcessor(Validator validator) {
        this.validator = validator;
        this.externalJobAdvertisementAssembler = new ExternalJobAdvertisementAssembler();
    }

    @Override
    public ExternalJobAdvertisementDto process(Oste item) {
        ExternalJobAdvertisementDto createItem = externalJobAdvertisementAssembler.createJobAdvertisementFromExternalDto(item);
        Set<ConstraintViolation<ExternalJobAdvertisementDto>> violations = validator.validate(createItem);
        if (violations.isEmpty()) {
            return createItem;
        }
        LOGGER.warn("Item with Fingerprint: {} has constraint violations: {}", item.getFingerprint(), violations);
        return null;
    }

}
