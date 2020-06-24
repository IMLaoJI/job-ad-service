package ch.admin.seco.jobs.services.jobadservice.integration.external.importer.config.dto;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.PublicationDto;

import java.time.LocalDate;

public class ExternalPublicationDto {

	private boolean companyAnonymous;

	private LocalDate publicationStartDate;

	private LocalDate publicationEndDate;

	public boolean isCompanyAnonymous() {
		return companyAnonymous;
	}

	public ExternalPublicationDto setCompanyAnonymous(boolean companyAnonymous) {
		this.companyAnonymous = companyAnonymous;
		return this;
	}

	public LocalDate getPublicationStartDate() {
		return publicationStartDate;
	}

	public ExternalPublicationDto setPublicationStartDate(LocalDate publicationStartDate) {
		this.publicationStartDate = publicationStartDate;
		return this;
	}

	public LocalDate getPublicationEndDate() {
		return publicationEndDate;
	}

	public ExternalPublicationDto setPublicationEndDate(LocalDate publicationEndDate) {
		this.publicationEndDate = publicationEndDate;
		return this;
	}

	PublicationDto toCreatePublicationDto() {
		return new PublicationDto()
				.setStartDate(publicationStartDate)
				.setEndDate(publicationEndDate)
				.setCompanyAnonymous(companyAnonymous);
	}

}
