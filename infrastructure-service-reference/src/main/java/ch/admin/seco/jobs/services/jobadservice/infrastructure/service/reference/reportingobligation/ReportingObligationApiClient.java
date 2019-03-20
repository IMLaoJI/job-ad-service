package ch.admin.seco.jobs.services.jobadservice.infrastructure.service.reference.reportingobligation;

import ch.admin.seco.jobs.services.jobadservice.domain.profession.ProfessionCodeType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "referenceservice", contextId = "reporting-obligations-api", url = "${feign.referenceservice.url:}", fallback = ReportingObligationApiClientFallback.class, decode404 = true)
public interface ReportingObligationApiClient {

    @GetMapping(value = "/api/reporting-obligations/check-by-canton/{codeType}/{code}", consumes = "application/json")
    ReportingObligationResource hasReportingObligation(
            @PathVariable("codeType") ProfessionCodeType professionCodeType,
            @PathVariable("code") String professionCode,
            @RequestParam(value = "cantonCode", required = false) String cantonCode
    );

}

