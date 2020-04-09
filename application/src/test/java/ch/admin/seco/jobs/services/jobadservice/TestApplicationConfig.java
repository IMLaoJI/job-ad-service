package ch.admin.seco.jobs.services.jobadservice;

import ch.admin.seco.jobs.services.jobadservice.application.BusinessLogger;
import ch.admin.seco.jobs.services.jobadservice.application.JobCenterService;
import ch.admin.seco.jobs.services.jobadservice.application.LocationService;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.application.ProfessionService;
import ch.admin.seco.jobs.services.jobadservice.application.ReportingObligationService;
import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.ExternalJobAdvertisementArchiverService;
import ch.admin.seco.jobs.services.jobadservice.application.security.TestingCurrentUserContext;
import ch.admin.seco.jobs.services.jobadservice.domain.LanguageProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

@SpringBootApplication
public class TestApplicationConfig {

    @MockBean
    private MailSenderService mailSenderService;

    @MockBean
    private BusinessLogger businessLogger;

    @MockBean
    private DataFieldMaxValueIncrementer egovNumberGenerator;

    @MockBean
    private ReportingObligationService reportingObligationService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private ProfessionService professionService;

    @MockBean
    private JobCenterService jobCenterService;

    @MockBean
    private ExternalJobAdvertisementArchiverService externalJobAdvertisementArchiverService;

    @Bean
    public TestingCurrentUserContext testingCurrentUserContext() {
        return new TestingCurrentUserContext("junitest-1");
    }

    @Bean
    LanguageProvider languageProvider() {
        return new LanguageProvider(LocaleContextHolder::getLocale);
    }



}
