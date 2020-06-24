package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config.dto;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create.CreateLocationDto;

import javax.validation.constraints.Size;

public class ExternalLocationDto {

    @Size(max = 255)
    private String remarks;

    @Size(max = 50)
    private String city;

    @Size(max = 10)
    private String postalCode;

    @Size(max = 2)
    private String countryIsoCode;

    public ExternalLocationDto() {
        // For reflection libs
    }


    CreateLocationDto toCreateLocationDto() {
        return new CreateLocationDto()
                .setRemarks(remarks)
                .setCity(city)
                .setPostalCode(postalCode)
                .setCountryIsoCode(countryIsoCode);
    }

    public String getRemarks() {
        return remarks;
    }

    public ExternalLocationDto setRemarks(String remarks) {
        this.remarks = remarks;
        return this;
    }

    public String getCity() {
        return city;
    }

    public ExternalLocationDto setCity(String city) {
        this.city = city;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public ExternalLocationDto setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public ExternalLocationDto setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
        return this;
    }

}
