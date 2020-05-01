package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import ch.admin.seco.jobs.services.jobadservice.application.HtmlToMarkdownConverter;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.ConditionException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.ApiCreateJobAdvertisementFixture.*;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.util.PhoneNumberUtil.sanitizePhoneNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = JobAdvertisementFromApiAssembler.class)
public class JobAdvertisementFromApiAssemblerTest {

    @Autowired
    private JobAdvertisementFromApiAssembler jobAdvertisementFromApiAssembler;

    //needed for test injection, even if it is not used in this class
    @MockBean
    private HtmlToMarkdownConverter mockHtmlToMarkdownConverter;

    @Test
    public void testChangePhoneNumberFormat() {
        // given
        ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDto = createJobAdvertisementDto();

        // when
        CreateJobAdvertisementDto createJobAdvertisementDto = jobAdvertisementFromApiAssembler.convert(apiCreateJobAdvertisementDto);

        // then
        assertThat(createJobAdvertisementDto.getCompany().getPhone()).isEqualTo(phoneUnformatted);
        assertThat(createJobAdvertisementDto.getApplyChannel().getPhoneNumber()).isEqualTo(phoneUnformatted);
        assertThat(createJobAdvertisementDto.getContact().getPhone()).isEqualTo(phoneUnformatted);
    }

    @Test
    public void testPhoneNotValidIfHasText() {
        // given
        ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDto = createJobAdDtoWithPhoneWithText();
        String phone = apiCreateJobAdvertisementDto.getCompany().getPhone();

        // then
        assertThatExceptionOfType(ConditionException.class)
                .isThrownBy(() -> sanitizePhoneNumber(phone, PhoneNumberUtil.PhoneNumberFormat.E164));
    }

    @Test
    public void testPhoneNotValidIfFalseFormatted() {
        // given
        ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDto = createJobAdDtoWithPhoneFalseFormatted();
        String phone = apiCreateJobAdvertisementDto.getCompany().getPhone();

        // then
        assertThatExceptionOfType(ConditionException.class)
                .isThrownBy(() -> sanitizePhoneNumber(phone, PhoneNumberUtil.PhoneNumberFormat.E164));
    }

    @Test
    public void testPhoneNotValidIfTooLong() {
        // given
        ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDto = createJobAdDtoWithPhoneTooLong();
        String phone = apiCreateJobAdvertisementDto.getCompany().getPhone();

        // then
        assertThatExceptionOfType(ConditionException.class)
                .isThrownBy(() -> sanitizePhoneNumber(phone, PhoneNumberUtil.PhoneNumberFormat.E164));
    }

    @Test
    public void testPhoneNumberParsedIsNull() {
        // given
        String phone = "0385 9 a9999 a 999999999999999999999999";

        // then
        assertThatExceptionOfType(ConditionException.class)
                .isThrownBy(() -> sanitizePhoneNumber(phone, PhoneNumberUtil.PhoneNumberFormat.E164));
    }

}
