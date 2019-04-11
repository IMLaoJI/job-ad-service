package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.webform;

import ch.admin.seco.jobs.services.jobadservice.application.HtmlToMarkdownConverter;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.AddressDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ApplyChannelDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.CompanyDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.CancellationDto;
import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.CancellationResource;
import org.springframework.stereotype.Component;

import static org.springframework.util.StringUtils.hasText;
import static org.springframework.util.StringUtils.trimWhitespace;

@Component
public class JobAdvertisementFromWebAssembler {

    private final HtmlToMarkdownConverter htmlToMarkdownConverter;

    public JobAdvertisementFromWebAssembler(HtmlToMarkdownConverter htmlToMarkdownConverter) {
        this.htmlToMarkdownConverter = htmlToMarkdownConverter;
    }

    CreateJobAdvertisementDto convert(WebformCreateJobAdvertisementDto createJobAdvertisementFromWebDto) {
        createJobAdvertisementFromWebDto.getJobDescriptions()
                .forEach(jobDescription -> jobDescription.setDescription(this.htmlToMarkdownConverter.convert(jobDescription.getDescription())));
        return new CreateJobAdvertisementDto()
                .setReportToAvam(true)
                .setApplyChannel(convertApplyChannel(createJobAdvertisementFromWebDto.getApplyChannel()))
                .setCompany(convertCompany(createJobAdvertisementFromWebDto.getCompany()))
                .setContact(createJobAdvertisementFromWebDto.getContact())
                .setEmployer(createJobAdvertisementFromWebDto.getEmployer())
                .setExternalReference(createJobAdvertisementFromWebDto.getExternalReference())
                .setLanguageSkills(createJobAdvertisementFromWebDto.getLanguageSkills())
                .setEmployment(createJobAdvertisementFromWebDto.getEmployment())
                .setNumberOfJobs(createJobAdvertisementFromWebDto.getNumberOfJobs())
                .setJobDescriptions(createJobAdvertisementFromWebDto.getJobDescriptions())
                .setPublication(createJobAdvertisementFromWebDto.getPublication())
                .setReportToAvam(createJobAdvertisementFromWebDto.isReportToAvam())
                .setExternalUrl(createJobAdvertisementFromWebDto.getExternalUrl())
                .setPublicContact(createJobAdvertisementFromWebDto.getPublicContact())
                .setLocation(createJobAdvertisementFromWebDto.getLocation())
                .setOccupation(createJobAdvertisementFromWebDto.getOccupation());
    }

    CancellationDto convert(CancellationResource cancellation) {
        return new CancellationDto()
                .setCancellationCode(cancellation.getCode())
                .setCancellationDate(TimeMachine.now().toLocalDate())
                .setCancelledBy(SourceSystem.JOBROOM);
    }

    ApplyChannelDto convertApplyChannel(WebformCreateApplyChannelDto createApplyChannelDto) {
        if (createApplyChannelDto == null) {
            return null;
        }

        validateApplyChannelIsNotEmpty(createApplyChannelDto);

        return new ApplyChannelDto()
                .setPostAddress(convertPostAddress(createApplyChannelDto.getPostAddress()))
                .setEmailAddress(trimOrNull(createApplyChannelDto.getEmailAddress()))
                .setPhoneNumber(trimOrNull(createApplyChannelDto.getPhoneNumber()))
                .setFormUrl(trimOrNull(createApplyChannelDto.getFormUrl()))
                .setAdditionalInfo(trimOrNull(createApplyChannelDto.getAdditionalInfo()));
    }

    CompanyDto convertCompany(WebformCreateCompanyDto createCompanyDto) {
        if (createCompanyDto == null) {
            return null;
        }

        validateAddressNormalOrPOBox(createCompanyDto.getStreet(), createCompanyDto.getPostOfficeBoxNumber());

        CompanyDto companyDto = new CompanyDto();
        if (hasText(createCompanyDto.getPostOfficeBoxNumber())) {
            companyDto
                    .setPostOfficeBoxNumber(trimOrNull(createCompanyDto.getPostOfficeBoxNumber()))
                    .setPostOfficeBoxPostalCode(trimOrNull(createCompanyDto.getPostalCode()))
                    .setPostOfficeBoxCity(trimOrNull(createCompanyDto.getCity()));
        }
        if (hasText(createCompanyDto.getStreet())) {
            companyDto
                    .setStreet(trimOrNull(createCompanyDto.getStreet()))
                    .setHouseNumber(trimOrNull(createCompanyDto.getHouseNumber()))
                    .setPostalCode(trimOrNull(createCompanyDto.getPostalCode()))
                    .setCity(trimOrNull(createCompanyDto.getCity()));
        }

        return companyDto
                .setName(trimOrNull(createCompanyDto.getName()))
                .setCountryIsoCode(trimOrNull(createCompanyDto.getCountryIsoCode()))
                .setSurrogate(createCompanyDto.isSurrogate());
    }

    AddressDto convertPostAddress(WebformCreateAddressDto createAddressDto) {
        if (createAddressDto == null) {
            return null;
        }

        validateAddressNormalOrPOBox(createAddressDto.getStreet(), createAddressDto.getPostOfficeBoxNumber());

        AddressDto addressDto = new AddressDto();

        if (hasText(createAddressDto.getPostOfficeBoxNumber())) {
            addressDto
                    .setPostOfficeBoxNumber(trimOrNull(createAddressDto.getPostOfficeBoxNumber()))
                    .setPostOfficeBoxPostalCode(trimOrNull(createAddressDto.getPostalCode()))
                    .setPostOfficeBoxCity(trimOrNull(createAddressDto.getCity()));
        } else {
            addressDto
                    .setStreet(trimOrNull(createAddressDto.getStreet()))
                    .setHouseNumber(trimOrNull(createAddressDto.getHouseNumber()))
                    .setPostalCode(trimOrNull(createAddressDto.getPostalCode()))
                    .setCity(trimOrNull(createAddressDto.getCity()));
        }

        return addressDto
                .setName(trimOrNull(createAddressDto.getName()))
                .setCountryIsoCode(trimOrNull(createAddressDto.getCountryIsoCode()));
    }

    private void validateApplyChannelIsNotEmpty(WebformCreateApplyChannelDto createApplyChannelDto) {
        boolean isNotEmpty = (createApplyChannelDto.getPostAddress() != null)
                || hasText(createApplyChannelDto.getEmailAddress())
                || hasText(createApplyChannelDto.getPhoneNumber())
                || hasText(createApplyChannelDto.getFormUrl());

        Condition.isTrue(isNotEmpty, "One of the apply channel must be set");
    }

    private void validateAddressNormalOrPOBox(String street, String postOfficeBoxNumber) {
        Condition.isTrue(hasText(street) || hasText(postOfficeBoxNumber), "Street or post office box number must be set");
    }

    private String trimOrNull(String value) {
        return hasText(value) ? trimWhitespace(value) : null;
    }

}
