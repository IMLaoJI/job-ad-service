package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.fixtures;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.Salutation;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api.ApiApplyChannelDto;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api.ApiCompanyDto;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api.ApiContactDto;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api.ApiCreateJobAdvertisementDto;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api.ApiCreateLocationDto;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api.ApiEmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api.ApiJobDescriptionDto;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api.ApiOccupationDto;
import ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api.ApiPublicationDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ApiCreateJobAdvertisementFixture {

    public static final String phoneFormatted = "+41 58 844 44 44";

    public static ApiCreateJobAdvertisementDto createJobAdvertismentDto01(){
        ApiCreateJobAdvertisementDto apiCreateJobAdvertisementDto = new ApiCreateJobAdvertisementDto();

        ApiContactDto apiContactDto = new ApiContactDto();
        apiContactDto.setSalutation(Salutation.MR);
        apiContactDto.setFirstName("Test First Name");
        apiContactDto.setLastName("Test Last Name");
        apiContactDto.setPhone(phoneFormatted);
        apiContactDto.setEmail("Test@mail.com");
        apiContactDto.setLanguageIsoCode("en");
        apiCreateJobAdvertisementDto.setContact(apiContactDto);

        ApiPublicationDto apiPublicationDto = new ApiPublicationDto();
        apiPublicationDto.setStartDate(LocalDate.now());
        apiCreateJobAdvertisementDto.setPublication(apiPublicationDto);

        ApiJobDescriptionDto apiJobDescriptionDto = new ApiJobDescriptionDto();
        apiJobDescriptionDto.setLanguageIsoCode("en");
        apiJobDescriptionDto.setDescription("Test Description");
        apiJobDescriptionDto.setTitle("Test Description");
        List<ApiJobDescriptionDto> apiJobDescriptionDtos = new ArrayList<>();
        apiJobDescriptionDtos.add(apiJobDescriptionDto);
        apiCreateJobAdvertisementDto.setJobDescriptions(apiJobDescriptionDtos);


        ApiCompanyDto apiCompanyDto = new ApiCompanyDto();
        apiCompanyDto.setName("Test company name");
        apiCompanyDto.setPostalCode("3001");
        apiCompanyDto.setCity("Bern");
        apiCompanyDto.setCountryIsoCode("CH");
        apiCompanyDto.setPhone("+41588444444");
        apiCreateJobAdvertisementDto.setCompany(apiCompanyDto);

        ApiEmploymentDto apiEmploymentDto = new ApiEmploymentDto();
        apiEmploymentDto.setWorkloadPercentageMin(11);
        apiEmploymentDto.setWorkloadPercentageMax(11);
        apiCreateJobAdvertisementDto.setEmployment(apiEmploymentDto);

        ApiCreateLocationDto apiCreateLocationDto = new ApiCreateLocationDto();
        apiCreateLocationDto.setCity("Bern");
        apiCreateLocationDto.setPostalCode("3012");
        apiCreateLocationDto.setCountryIsoCode("CH");
        apiCreateJobAdvertisementDto.setLocation(apiCreateLocationDto);

        ApiOccupationDto apiOccupationDto = new ApiOccupationDto();
        apiOccupationDto.setAvamOccupationCode("123456789");
        apiCreateJobAdvertisementDto.setOccupation(apiOccupationDto);

        ApiApplyChannelDto apiApplyChannelDto = new ApiApplyChannelDto();
        apiApplyChannelDto.setPhoneNumber("+41588444444");
        apiCreateJobAdvertisementDto.setApplyChannel(apiApplyChannelDto);

        return apiCreateJobAdvertisementDto;
    }
}
