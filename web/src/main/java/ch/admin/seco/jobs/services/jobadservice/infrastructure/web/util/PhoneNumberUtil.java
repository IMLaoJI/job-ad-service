package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.util;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.ConditionException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.Phonenumber;

import static org.springframework.util.StringUtils.hasText;

public final class PhoneNumberUtil {

    /*
     * Check for a valid phone number and format as international number, but with no formatting applied (without spaces)
     * example: +41795551234
     */
    public static String sanitizePhoneNumber(final String phone, final com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat format) {
        if (hasText(phone)) {
            try {
                Phonenumber.PhoneNumber phoneNumber = com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance().parse(phone, "CH");
                validatePhoneNumber(phone, phoneNumber);
                return com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance().format(phoneNumber, format);
            } catch (NumberParseException e) {
                throw new ConditionException("Failed to parse phone number %s .", phone);
            }
        }
        return null;
    }

    private static void validatePhoneNumber(final String phone, final Phonenumber.PhoneNumber phoneNumber) {
        String validationMessage = String.format("Failed to parse phone number %s .", phone);
        Condition.isTrue(com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance().isValidNumber(phoneNumber), validationMessage);
    }
}
