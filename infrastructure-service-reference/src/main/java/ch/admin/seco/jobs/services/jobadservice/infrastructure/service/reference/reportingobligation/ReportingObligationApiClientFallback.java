package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.reportingobligation;

import ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReportingObligationApiClientFallback implements ReportingObligationApiClient {

    private static Logger LOG = LoggerFactory.getLogger(ReportingObligationApiClientFallback.class);

    @Override
    public ReportingObligationResource hasReportingObligation(ProfessionCodeType professionCodeType, String professionCode, String cantonCode) {
        LOG.warn("Fallback active for ReportingObligationApiClientFallback.hasReportingObligation(" + professionCodeType + "," + professionCode + "," + cantonCode + ")");
        return new ReportingObligationResource(
                true,
                null,
                null
        );
    }

}
