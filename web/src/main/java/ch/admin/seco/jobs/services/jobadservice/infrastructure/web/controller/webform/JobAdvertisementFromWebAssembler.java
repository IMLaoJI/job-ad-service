package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.webform;

import ch.admin.seco.jobs.services.jobadservice.application.HtmlToMarkdownConverter;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.AddressDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.ApplyChannelDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.update.CancellationDto;
import ch.admin.seco.jobs.services.jobadservice.core.time.TimeMachine;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.SourceSystem;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.CancellationResource;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static java.util.Collections.singletonList;
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
                .setCompany(createJobAdvertisementFromWebDto.getCompany())
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
                .setOccupations(singletonList(createJobAdvertisementFromWebDto.getOccupation()));
    }

    CancellationDto convert(CancellationResource cancellation) {
        return new CancellationDto()
                .setCancellationCode(cancellation.getCode())
                .setCancellationDate(TimeMachine.now().toLocalDate())
                .setCancelledBy(SourceSystem.JOBROOM);
    }

    private ApplyChannelDto convertApplyChannel(ApplyChannelDto createApplyChannelDto) {
        if (createApplyChannelDto == null) {
            return null;
        }
        return new ApplyChannelDto()
                .setRawPostAddress(trimOrNull(createApplyChannelDto.getRawPostAddress()))
                .setPostAddress(convertPostAddress(createApplyChannelDto.getPostAddress()))
                .setEmailAddress(trimOrNull(createApplyChannelDto.getEmailAddress()))
                .setPhoneNumber(trimOrNull(createApplyChannelDto.getPhoneNumber()))
                .setFormUrl(trimOrNull(createApplyChannelDto.getFormUrl()))
                .setAdditionalInfo(trimOrNull(createApplyChannelDto.getAdditionalInfo()));
    }

    private AddressDto convertPostAddress(AddressDto createPostAddressDto) {
        if (createPostAddressDto == null) {
            return null;
        }
        if (!hasText(createPostAddressDto.getName())) {
            return null;
        }
        return new AddressDto()
                .setName(trimOrNull(createPostAddressDto.getName()))
                .setStreet(trimOrNull(createPostAddressDto.getStreet()))
                .setHouseNumber(trimOrNull(createPostAddressDto.getHouseNumber()))
                .setPostalCode(trimOrNull(createPostAddressDto.getPostalCode()))
                .setCity(trimOrNull(createPostAddressDto.getCity()))
                .setPostOfficeBoxNumber(trimOrNull(createPostAddressDto.getPostOfficeBoxNumber()))
                .setPostOfficeBoxPostalCode(trimOrNull(createPostAddressDto.getPostOfficeBoxPostalCode()))
                .setPostOfficeBoxCity(trimOrNull(createPostAddressDto.getPostOfficeBoxCity()))
                .setCountryIsoCode(trimOrNull(createPostAddressDto.getCountryIsoCode()));
    }

    private String trimOrNull(String value) {
        return hasText(value) ? trimWhitespace(value) : null;
    }
}
