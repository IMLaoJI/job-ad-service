package ch.admin.seco.jobs.services.jobadservice.integration.external.exporter.config;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.*;
import ch.admin.seco.jobs.services.jobadservice.integration.external.jobadexport.Oste;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

public class ExternalJobAdvertisementTransformer implements ItemProcessor<JobAdvertisement, Oste> {
    private static final String ORACLE_DATE_FORMAT = "yyyy-MM-dd-HH.mm.ss.SSSSSS";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(ORACLE_DATE_FORMAT);


    /**
     *
     * @param jobAdvertisement Comes from DB and is read from DB in ExternalJobAdvertisementExportTaskConfig.jpaPagingItemReader
     * @return Oste
     */
    @Override
    public Oste process(JobAdvertisement jobAdvertisement) {
        JobContent jobContent = jobAdvertisement.getJobContent();
        Oste externalJobAdvertisement = new Oste();
        externalJobAdvertisement.setOsteId(jobAdvertisement.getId().getValue());
        externalJobAdvertisement.setStellennummerAvam(jobAdvertisement.getStellennummerAvam());
        externalJobAdvertisement.setStellennummerEGov(jobAdvertisement.getStellennummerEgov());
        externalJobAdvertisement.setFingerprint(jobAdvertisement.getFingerprint());
        externalJobAdvertisement.setArbeitsamtbereich(jobAdvertisement.getJobCenterCode());
        externalJobAdvertisement.setGleicheOste(jobContent.getNumberOfJobs());
        externalJobAdvertisement.setSourceSystem(jobAdvertisement.getSourceSystem().name());

        mapTitleAndDescription(jobContent.getJobDescriptions(), externalJobAdvertisement);
        mapOccupation(jobContent.getOccupations(), externalJobAdvertisement);
        mapLocation(jobContent.getLocation(), externalJobAdvertisement);
        mapPublication(jobAdvertisement.getPublication(), externalJobAdvertisement);
        mapEmployment(jobContent.getEmployment(), externalJobAdvertisement);
        mapPublicContact(jobContent.getPublicContact(), externalJobAdvertisement);
        mapLanguageSkills(jobContent.getLanguageSkills(), externalJobAdvertisement);
        mapCompany(jobContent.getDisplayCompany(), externalJobAdvertisement);
        mapApplyChannel(jobContent.getApplyChannel(), externalJobAdvertisement);

        return externalJobAdvertisement;
    }

    private void mapApplyChannel(ApplyChannel applyChannel, Oste externalJobAdvertisement) {
        if (applyChannel != null) {
            externalJobAdvertisement.setBewerbungSchriftlich(isNotBlank(applyChannel.getRawPostAddress()));

            externalJobAdvertisement.setBewerbungElektronisch(isNotBlank(applyChannel.getFormUrl()) || isNotBlank(applyChannel.getEmailAddress()));
            externalJobAdvertisement.setUntUrl(applyChannel.getFormUrl());
            externalJobAdvertisement.setUntEMail(applyChannel.getEmailAddress());

            externalJobAdvertisement.setBewerbungTelefonisch(isNotBlank(applyChannel.getPhoneNumber()));
            externalJobAdvertisement.setUntTelefon(applyChannel.getPhoneNumber());
        }
    }

    private void mapCompany(Company company, Oste externalJobAdvertisement) {
        externalJobAdvertisement.setUntName(company.getName());

        externalJobAdvertisement.setUntLand(company.getCountryIsoCode());
        externalJobAdvertisement.setUntPlz(company.getPostalCode());
        externalJobAdvertisement.setUntOrt(company.getCity());
        externalJobAdvertisement.setUntStrasse(company.getStreet());
        externalJobAdvertisement.setUntHausNr(company.getHouseNumber());

        externalJobAdvertisement.setUntPostfach(company.getPostOfficeBoxNumber());
        externalJobAdvertisement.setUntPostfachPlz(company.getPostOfficeBoxPostalCode());
        externalJobAdvertisement.setUntPostfachOrt(company.getPostOfficeBoxCity());
    }

    private void mapLanguageSkills(List<LanguageSkill> languageSkills, Oste externalJobAdvertisement) {
        if (!isEmpty(languageSkills)) {
            if (languageSkills.size() >= 1) {
                LanguageSkill languageSkill = languageSkills.get(0);
                externalJobAdvertisement.setSk1SpracheCode(languageSkill.getLanguageIsoCode());
                externalJobAdvertisement.setSk1SchriftlichCode(safeString(languageSkill.getWrittenLevel()));
                externalJobAdvertisement.setSk1MuendlichCode(safeString(languageSkill.getSpokenLevel()));
            }
            if (languageSkills.size() >= 2) {
                LanguageSkill languageSkill = languageSkills.get(1);
                externalJobAdvertisement.setSk2SpracheCode(languageSkill.getLanguageIsoCode());
                externalJobAdvertisement.setSk2SchriftlichCode(safeString(languageSkill.getWrittenLevel()));
                externalJobAdvertisement.setSk2MuendlichCode(safeString(languageSkill.getSpokenLevel()));
            }
            if (languageSkills.size() >= 3) {
                LanguageSkill languageSkill = languageSkills.get(2);
                externalJobAdvertisement.setSk3SpracheCode(languageSkill.getLanguageIsoCode());
                externalJobAdvertisement.setSk3SchriftlichCode(safeString(languageSkill.getWrittenLevel()));
                externalJobAdvertisement.setSk3MuendlichCode(safeString(languageSkill.getSpokenLevel()));
            }
            if (languageSkills.size() >= 4) {
                LanguageSkill languageSkill = languageSkills.get(3);
                externalJobAdvertisement.setSk4SpracheCode(languageSkill.getLanguageIsoCode());
                externalJobAdvertisement.setSk4SchriftlichCode(safeString(languageSkill.getWrittenLevel()));
                externalJobAdvertisement.setSk4MuendlichCode(safeString(languageSkill.getSpokenLevel()));
            }
            if (languageSkills.size() >= 5) {
                LanguageSkill languageSkill = languageSkills.get(4);
                externalJobAdvertisement.setSk5SpracheCode(languageSkill.getLanguageIsoCode());
                externalJobAdvertisement.setSk5SchriftlichCode(safeString(languageSkill.getWrittenLevel()));
                externalJobAdvertisement.setSk5MuendlichCode(safeString(languageSkill.getSpokenLevel()));
            }
        }
    }

    private void mapEmployment(Employment employment, Oste externalJobAdvertisement) {
        if (employment != null) {
            externalJobAdvertisement.setAbSofort(employment.isImmediately());
            externalJobAdvertisement.setUnbefristet(employment.isPermanent());

            if (employment.getStartDate() != null) {
                externalJobAdvertisement.setStellenantritt(DATE_FORMATTER.format(employment.getStartDate().atStartOfDay()));
            }

            if (employment.getEndDate() != null) {
                externalJobAdvertisement.setVertragsdauer(DATE_FORMATTER.format(employment.getEndDate().atStartOfDay().plusDays(1).minus(1, ChronoUnit.SECONDS)));
            }
            externalJobAdvertisement.setPensumVon((long) employment.getWorkloadPercentageMin());
            externalJobAdvertisement.setPensumBis((long) employment.getWorkloadPercentageMax());
        }
    }

    private void mapPublication(Publication publication, Oste externalJobAdvertisement) {
        if (publication != null) {

            if (publication.getStartDate() != null) {
                externalJobAdvertisement.setAnmeldeDatum(DATE_FORMATTER.format(publication.getStartDate().atStartOfDay()));
            }

            if (publication.getEndDate() != null) {
                externalJobAdvertisement.setGueltigkeit(DATE_FORMATTER.format(publication.getEndDate().atStartOfDay().plusDays(1).minus(1, ChronoUnit.SECONDS)));
            }

            externalJobAdvertisement.setEuresAnonym(publication.isEuresAnonymous());
            externalJobAdvertisement.setEuresPublikation(publication.isEuresDisplay());
            externalJobAdvertisement.setWwwAnonym(publication.isCompanyAnonymous());
        }
    }

    private void mapLocation(Location location, Oste externalJobAdvertisement) {
        if (location != null) {
            externalJobAdvertisement.setArbeitsortAusland(location.getCountryIsoCode());
            externalJobAdvertisement.setArbeitsortGemeindeNr(location.getCommunalCode());
            externalJobAdvertisement.setArbeitsortKanton(location.getCantonCode());
            externalJobAdvertisement.setArbeitsortPlz(location.getPostalCode());
            externalJobAdvertisement.setArbeitsortRegion(location.getRegionCode());
            externalJobAdvertisement.setArbeitsortText(location.getCity());
        }
    }

    private void mapTitleAndDescription(List<JobDescription> jobDescriptions, Oste externalJobAdvertisement) {
        if (!isEmpty(jobDescriptions)) {
            JobDescription jobDescription = jobDescriptions.get(0);
            externalJobAdvertisement.setBezeichnung(jobDescription.getTitle());
            externalJobAdvertisement.setBeschreibung(jobDescription.getDescription());
        }
    }

    private void mapOccupation(List<Occupation> occupations, Oste externalJobAdvertisement) {
        if (!isEmpty(occupations)) {

            if (occupations.size() >= 1) {
                Occupation occupation = occupations.get(0);
                externalJobAdvertisement.setBq1AvamBerufNrNew(occupation.getAvamOccupationCode());
                externalJobAdvertisement.setBq1AusbildungCode(occupation.getEducationCode());
                externalJobAdvertisement.setBq1ErfahrungCode(safeString(occupation.getWorkExperience()));
            }

            if (occupations.size() >= 2) {
                Occupation occupation = occupations.get(1);
                externalJobAdvertisement.setBq2AvamBerufNrNew(occupation.getAvamOccupationCode());
                externalJobAdvertisement.setBq2AusbildungCode(occupation.getEducationCode());
                externalJobAdvertisement.setBq2ErfahrungCode(safeString(occupation.getWorkExperience()));
            }

            if (occupations.size() >= 3) {
                Occupation occupation = occupations.get(2);
                externalJobAdvertisement.setBq3AvamBerufNrNew(occupation.getAvamOccupationCode());
                externalJobAdvertisement.setBq3AusbildungCode(occupation.getEducationCode());
                externalJobAdvertisement.setBq3ErfahrungCode(safeString(occupation.getWorkExperience()));
            }
        }
    }

    private void mapPublicContact(PublicContact publicContact, Oste externalJobAdvertisement) {
        if (publicContact != null) {
            externalJobAdvertisement.setKpAnredeCode(safeString(publicContact.getSalutation()));
            externalJobAdvertisement.setKpName(publicContact.getLastName());
            externalJobAdvertisement.setKpVorname(publicContact.getFirstName());
            externalJobAdvertisement.setKpEMail(publicContact.getEmail());
            externalJobAdvertisement.setKpTelefonNr(publicContact.getPhone());
        }
    }

    private static String safeString(LanguageLevel languageLevel) {
        if (languageLevel != null) {
            return languageLevel.name();
        }

        return null;
    }

    private static String safeString(Object object) {
        if (object != null) {
            return object.toString();
        }

        return null;
    }
}
