package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.utils;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

public class SupportedEducationCodeValidator implements ConstraintValidator<SupportedEducationCode, String> {

    static final String SEK_II_WEITERFUEHRENDE_SCHULE = "130";
    static final String SEK_II_GRUNDBILDUNG_EBA = "131";
    static final String SEK_II_GRUNDBILDUNG_EFZ = "132";
    static final String SEK_II_FACHMITTELSCHULE = "133";
    static final String SEK_II_BERUFSMATURITAET = "134";
    static final String SEK_II_FACHMATURITAET = "135";
    static final String SEK_II_GYMNASIALE_MATURITAET = "136";
    static final String TER_BERUFSBILDUNG_FA = "150";
    static final String TER_BERUFSBILDUNG_DIPL = "160";
    static final String TER_BACHELOR_FACHHOCHSCHULE = "170";
    static final String TER_BACHELOR_UNIVERSITAET = "171";
    static final String TER_MASTER_FACHHOCHSCHULE = "172";
    static final String TER_MASTER_UNIVERSITAET = "173";
    static final String TER_DOKTORAT_UNIVERSITAET = "180";

    private static final Set<String> SUPPORTED_EDUCATION_CODES = new HashSet<>();

    static {
        SUPPORTED_EDUCATION_CODES.add(SEK_II_WEITERFUEHRENDE_SCHULE);
        SUPPORTED_EDUCATION_CODES.add(SEK_II_GRUNDBILDUNG_EBA);
        SUPPORTED_EDUCATION_CODES.add(SEK_II_GRUNDBILDUNG_EFZ);
        SUPPORTED_EDUCATION_CODES.add(SEK_II_FACHMITTELSCHULE);
        SUPPORTED_EDUCATION_CODES.add(SEK_II_BERUFSMATURITAET);
        SUPPORTED_EDUCATION_CODES.add(SEK_II_FACHMATURITAET);
        SUPPORTED_EDUCATION_CODES.add(SEK_II_GYMNASIALE_MATURITAET);
        SUPPORTED_EDUCATION_CODES.add(TER_BERUFSBILDUNG_FA);
        SUPPORTED_EDUCATION_CODES.add(TER_BERUFSBILDUNG_DIPL);
        SUPPORTED_EDUCATION_CODES.add(TER_BACHELOR_FACHHOCHSCHULE);
        SUPPORTED_EDUCATION_CODES.add(TER_BACHELOR_UNIVERSITAET);
        SUPPORTED_EDUCATION_CODES.add(TER_MASTER_FACHHOCHSCHULE);
        SUPPORTED_EDUCATION_CODES.add(TER_MASTER_UNIVERSITAET);
        SUPPORTED_EDUCATION_CODES.add(TER_DOKTORAT_UNIVERSITAET);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return true;
        }
        return SUPPORTED_EDUCATION_CODES.contains(value);
    }
}
