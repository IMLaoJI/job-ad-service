package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.SupportedEducationCode;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils.SupportedEducationCodeValidator.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SupportedEducationCodeValidatorTest {

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNoViolation() {
        assertThat(validator.validate(new DummyClass(PRIMARSTUFE))).isEmpty();
        assertThat(validator.validate(new DummyClass(SEKUNDARSTUFE))).isEmpty();
        assertThat(validator.validate(new DummyClass(SEK_II_WEITERFUEHRENDE_SCHULE))).isEmpty();
        assertThat(validator.validate(new DummyClass(SEK_II_GRUNDBILDUNG_EBA))).isEmpty();
        assertThat(validator.validate(new DummyClass(SEK_II_GRUNDBILDUNG_EFZ))).isEmpty();
        assertThat(validator.validate(new DummyClass(SEK_II_FACHMITTELSCHULE))).isEmpty();
        assertThat(validator.validate(new DummyClass(SEK_II_BERUFSMATURITAET))).isEmpty();
        assertThat(validator.validate(new DummyClass(SEK_II_FACHMATURITAET))).isEmpty();
        assertThat(validator.validate(new DummyClass(SEK_II_GYMNASIALE_MATURITAET))).isEmpty();
        assertThat(validator.validate(new DummyClass(TER_BERUFSBILDUNG_FA))).isEmpty();
        assertThat(validator.validate(new DummyClass(TER_BERUFSBILDUNG_DIPL))).isEmpty();
        assertThat(validator.validate(new DummyClass(TER_BACHELOR_FACHHOCHSCHULE))).isEmpty();
        assertThat(validator.validate(new DummyClass(TER_BACHELOR_UNIVERSITAET))).isEmpty();
        assertThat(validator.validate(new DummyClass(TER_MASTER_FACHHOCHSCHULE))).isEmpty();
        assertThat(validator.validate(new DummyClass(TER_MASTER_UNIVERSITAET))).isEmpty();
        assertThat(validator.validate(new DummyClass(TER_DOKTORAT_UNIVERSITAET))).isEmpty();
        assertThat(validator.validate(new DummyClass(null))).isEmpty();
    }

    @Test
    public void testInvalid() {
        DummyClass invalidObject = new DummyClass("129");

        Set<ConstraintViolation<DummyClass>> constraintViolations = validator.validate(invalidObject);

        assertThat(constraintViolations).hasSize(1);
    }

    private static class DummyClass {

        @SupportedEducationCode
        private String educationCode;

        DummyClass(String educationCode) {
            this.educationCode = educationCode;
        }
    }
}
