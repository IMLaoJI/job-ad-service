package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.webform;

import ch.admin.seco.jobs.services.jobadservice.application.HtmlToMarkdownConverter;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.AddressDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.CompanyDto;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.ConditionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.WebformCreateAddressDtoFixture.*;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.WebformCreateApplyChannelDtoFixture.webformCreateApplyChannelDtoEmpty;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.WebformCreateApplyChannelDtoFixture.webformCreateApplyChannelDtoWithPostAddressEmpty;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures.WebformCreateCompanyDtoFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JobAdvertisementFromWebAssembler.class)
public class JobAdvertisementFromWebAssemblerTest {

    @Autowired
    private JobAdvertisementFromWebAssembler jobAdvertisementFromWebAssembler;

    @MockBean
    private HtmlToMarkdownConverter mockHtmlToMarkdownConverter;

    // Convert ApplyChannel

    /**
     * if all channel are missing, then a condition exception should be thrown.
     */
    @Test
    public void shouldApplyChannelNotBeEmpty() {
        WebformCreateApplyChannelDto createApplyChannelDto = webformCreateApplyChannelDtoEmpty();

        assertThatExceptionOfType(ConditionException.class)
                .isThrownBy(() -> jobAdvertisementFromWebAssembler.convertApplyChannel(createApplyChannelDto));
    }

    /**
     * if only post address is missing, then the applyChannel is valid.
     */
    @Test
    public void shouldApplyChannelWithOnlyPostAddressMissingIsValid() {
        WebformCreateApplyChannelDto createApplyChannelDto = webformCreateApplyChannelDtoWithPostAddressEmpty();

        assertThat(createApplyChannelDto).isNotNull();
    }

    // Convert Address

    /**
     * if street and post office box number is missing, then a condition exception should be thrown.
     */
    @Test
    public void shouldAddressNotBeInvalid() {
        WebformCreateAddressDto createAddressDto = webformCreateAddressDtoWithoutNormalAddressAndPOBoxAddress();

        assertThatExceptionOfType(ConditionException.class)
                .isThrownBy(() -> jobAdvertisementFromWebAssembler.convertPostAddress(createAddressDto));
    }

    /**
     * If only the street is filled out, then the result should be a normal address.
     */
    @Test
    public void shouldAddressBeNormalAddress() {
        WebformCreateAddressDto createAddressDto = webformCreateAddressDtoForNormalAddress();

        AddressDto result = jobAdvertisementFromWebAssembler.convertPostAddress(createAddressDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(createAddressDto.getName());
        assertThat(result.getStreet()).isEqualTo(createAddressDto.getStreet());
        assertThat(result.getHouseNumber()).isEqualTo(createAddressDto.getHouseNumber());
        assertThat(result.getPostalCode()).isEqualTo(createAddressDto.getPostalCode());
        assertThat(result.getCity()).isEqualTo(createAddressDto.getCity());
        assertThat(result.getPostOfficeBoxNumber()).isNull();
        assertThat(result.getPostOfficeBoxPostalCode()).isNull();
        assertThat(result.getPostOfficeBoxCity()).isNull();
        assertThat(result.getCountryIsoCode()).isEqualTo(createAddressDto.getCountryIsoCode());
    }

    /**
     * If only the post office box number is filled out, then the result should be a post office address.
     */
    @Test
    public void shouldAddressBePOBoxAddress() {
        WebformCreateAddressDto createAddressDto = webformCreateAddressDtoForPOBoxAddress();

        AddressDto result = jobAdvertisementFromWebAssembler.convertPostAddress(createAddressDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(createAddressDto.getName());
        assertThat(result.getStreet()).isNull();
        assertThat(result.getHouseNumber()).isNull();
        assertThat(result.getPostalCode()).isNull();
        assertThat(result.getCity()).isNull();
        assertThat(result.getPostOfficeBoxNumber()).isEqualTo(createAddressDto.getPostOfficeBoxNumber());
        assertThat(result.getPostOfficeBoxPostalCode()).isEqualTo(createAddressDto.getPostalCode());
        assertThat(result.getPostOfficeBoxCity()).isEqualTo(createAddressDto.getCity());
        assertThat(result.getCountryIsoCode()).isEqualTo(createAddressDto.getCountryIsoCode());
    }

    /**
     * If street and the post office box number is filled out, then the result should be a post office address.
     */
    @Test
    public void shouldAddressWithBothAddressBePOBoxAddress() {
        WebformCreateAddressDto createAddressDto = webformCreateAddressDtoForNormalAddressAndPOBoxAddress();

        AddressDto result = jobAdvertisementFromWebAssembler.convertPostAddress(createAddressDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(createAddressDto.getName());
        assertThat(result.getStreet()).isNull();
        assertThat(result.getHouseNumber()).isNull();
        assertThat(result.getPostalCode()).isNull();
        assertThat(result.getCity()).isNull();
        assertThat(result.getPostOfficeBoxNumber()).isEqualTo(createAddressDto.getPostOfficeBoxNumber());
        assertThat(result.getPostOfficeBoxPostalCode()).isEqualTo(createAddressDto.getPostalCode());
        assertThat(result.getPostOfficeBoxCity()).isEqualTo(createAddressDto.getCity());
        assertThat(result.getCountryIsoCode()).isEqualTo(createAddressDto.getCountryIsoCode());
    }

    // Convert Company

    /**
     * if street and post office box number is missing, then a condition exception should be thrown.
     */
    @Test
    public void shouldCompanyNotBeInvalid() {
        WebformCreateCompanyDto createCompanyDto = webformCreateCompanyDtoWithAddressAndPOBoxEmpty();

        assertThatExceptionOfType(ConditionException.class)
                .isThrownBy(() -> jobAdvertisementFromWebAssembler.convertCompany(createCompanyDto));
    }

    /**
     * If only the street is filled out, then the result should be a company with normal address.
     */
    @Test
    public void shouldCompanyBeNormalAddress() {
        WebformCreateCompanyDto createCompanyDto = testWebformCreateCompanyDtoWithOnlyFilledAddressFields();

        CompanyDto result = jobAdvertisementFromWebAssembler.convertCompany(createCompanyDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(createCompanyDto.getName());
        assertThat(result.getStreet()).isEqualTo(createCompanyDto.getStreet());
        assertThat(result.getHouseNumber()).isEqualTo(createCompanyDto.getHouseNumber());
        assertThat(result.getPostalCode()).isEqualTo(createCompanyDto.getPostalCode());
        assertThat(result.getCity()).isEqualTo(createCompanyDto.getCity());
        assertThat(result.getPostOfficeBoxNumber()).isNull();
        assertThat(result.getPostOfficeBoxPostalCode()).isNull();
        assertThat(result.getPostOfficeBoxCity()).isNull();
        assertThat(result.getCountryIsoCode()).isEqualTo(createCompanyDto.getCountryIsoCode());
    }

    /**
     * If only the post office box number is filled out, then the result should be a company with post office address.
     */
    @Test
    public void shouldCompanyBePOBoxAddress() {
        WebformCreateCompanyDto createCompanyDto = testWebformCreateCompanyDtoWithOnlyFilledPOBoxFields();

        CompanyDto result = jobAdvertisementFromWebAssembler.convertCompany(createCompanyDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(createCompanyDto.getName());
        assertThat(result.getStreet()).isNull();
        assertThat(result.getHouseNumber()).isNull();
        assertThat(result.getPostalCode()).isNull();
        assertThat(result.getCity()).isNull();
        assertThat(result.getPostOfficeBoxNumber()).isEqualTo(createCompanyDto.getPostOfficeBoxNumber());
        assertThat(result.getPostOfficeBoxPostalCode()).isEqualTo(createCompanyDto.getPostalCode());
        assertThat(result.getPostOfficeBoxCity()).isEqualTo(createCompanyDto.getCity());
        assertThat(result.getCountryIsoCode()).isEqualTo(createCompanyDto.getCountryIsoCode());
    }

    /**
     * If street and the post office box number is filled out, then the result should be a company with normal and post office address.
     */
    @Test
    public void shouldCompanyWithBothAddressBePOBoxAddress() {
        WebformCreateCompanyDto createCompanyDto = testWebformCreateCompanyDtoWithBothAddressAndPOBoxFields();

        CompanyDto result = jobAdvertisementFromWebAssembler.convertCompany(createCompanyDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(createCompanyDto.getName());
        assertThat(result.getStreet()).isEqualTo(createCompanyDto.getStreet());
        assertThat(result.getHouseNumber()).isEqualTo(createCompanyDto.getHouseNumber());
        assertThat(result.getPostalCode()).isEqualTo(createCompanyDto.getPostalCode());
        assertThat(result.getCity()).isEqualTo(createCompanyDto.getCity());
        assertThat(result.getPostOfficeBoxNumber()).isEqualTo(createCompanyDto.getPostOfficeBoxNumber());
        assertThat(result.getPostOfficeBoxPostalCode()).isEqualTo(createCompanyDto.getPostalCode());
        assertThat(result.getPostOfficeBoxCity()).isEqualTo(createCompanyDto.getCity());
        assertThat(result.getCountryIsoCode()).isEqualTo(createCompanyDto.getCountryIsoCode());
    }
}
