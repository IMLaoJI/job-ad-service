package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementId;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobContent.Builder;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.ApplyChannelFixture.testApplyChannel;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.ApplyChannelFixture.testDisplayApplyChannel;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.CompanyFixture.testCompany;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.CompanyFixture.testDisplayCompany;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.EmployerFixture.testEmployer;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.EmploymentFixture.testEmployment;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.JobDescriptionFixture.testJobDescription;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.LanguageSkillFixture.testLanguageSkill;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.LocationFixture.testLocation;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.OccupationFixture.testOccupationEmpty;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.fixture.PublicContactFixture.testPublicContact;
import static ch.admin.seco.jobs.services.jobadservice.domain.jobcenter.fixture.JobCenterFixture.testJobCenter;
import static java.util.Collections.singletonList;

public class JobContentFixture {

    public static Builder of(JobAdvertisementId id) {
        return testJobContent()
                .setDisplayCompany(CompanyFixture.of(id).build())
                .setCompany(CompanyFixture.of(id).build())
                .setEmployment(testEmployment().build())
                .setLanguageSkills(singletonList(testLanguageSkill().build()))
                .setPublicContact(PublicContactFixture.of(id).build())
                .setApplyChannel(ApplyChannelFixture.of(id).build())
                .setDisplayApplyChannel(ApplyChannelFixture.of(id).build());
    }

    public static Builder testJobContentEmpty() {
        return new Builder();
    }

    public static Builder testJobContent() {
        return testJobContentEmpty()
                .setExternalUrl("externalUr")
                .setX28OccupationCodes("x28OccupationCodes")
                .setNumberOfJobs("1")
                .setJobDescriptions(singletonList(testJobDescription().build()))
                .setDisplayCompany(testDisplayCompany(testJobCenter()).build())
                .setCompany(testCompany().build())
                .setEmployer(testEmployer().build())
                .setEmployment(testEmployment().build())
                .setLocation(testLocation().build())
                .setOccupations(singletonList(
                        testOccupationEmpty()
                                .setAvamOccupationCode("9999")
                                .build()))
                .setLanguageSkills(singletonList(testLanguageSkill().build()))
                .setApplyChannel(testApplyChannel().build())
                .setDisplayApplyChannel(testDisplayApplyChannel(testJobCenter()).build())
                .setPublicContact(testPublicContact().build());
    }
}
