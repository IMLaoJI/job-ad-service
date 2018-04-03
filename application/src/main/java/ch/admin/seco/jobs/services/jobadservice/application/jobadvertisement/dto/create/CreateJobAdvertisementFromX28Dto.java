package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.create;

import java.util.Collection;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.CompanyDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.EmploymentDto;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.OccupationDto;

public class CreateJobAdvertisementFromX28Dto {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String fingerprint;

    private String externalUrl;

    @NotNull
    private EmploymentDto employment;

    @NotNull
    private CompanyDto company;

    @NotNull
    private CreateLocationDto location;

    @NotEmpty
    private Collection<OccupationDto> occupations;

    protected CreateJobAdvertisementFromX28Dto() {
        // For reflection libs
    }

    public CreateJobAdvertisementFromX28Dto(String title, String description, String fingerprint, String externalUrl, EmploymentDto employment, CompanyDto company, CreateLocationDto location, Collection<OccupationDto> occupations) {
        this.title = title;
        this.description = description;
        this.fingerprint = fingerprint;
        this.externalUrl = externalUrl;
        this.employment = employment;
        this.company = company;
        this.location = location;
        this.occupations = occupations;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public EmploymentDto getEmployment() {
        return employment;
    }

    public void setEmployment(EmploymentDto employment) {
        this.employment = employment;
    }

    public CompanyDto getCompany() {
        return company;
    }

    public void setCompany(CompanyDto company) {
        this.company = company;
    }

    public CreateLocationDto getLocation() {
        return location;
    }

    public void setLocation(CreateLocationDto location) {
        this.location = location;
    }

    public Collection<OccupationDto> getOccupations() {
        return occupations;
    }

    public void setOccupations(Collection<OccupationDto> occupations) {
        this.occupations = occupations;
    }
}
