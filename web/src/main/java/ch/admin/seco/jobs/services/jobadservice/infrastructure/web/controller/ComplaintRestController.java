package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.application.complaint.ComplaintApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.complaint.ComplaintDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/complaint")
public class ComplaintRestController {
    private final ComplaintApplicationService complaintApplicationService;

    @Value("${alv.complaint.toggle.reportAdvertisementLink.visible}")
    private boolean reportAdvertisementLinkVisible;

    public ComplaintRestController(ComplaintApplicationService complaintApplicationService) {
        this.complaintApplicationService = complaintApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void sendComplaint(@RequestBody @Valid ComplaintDto complaintDto) {
        if (reportAdvertisementLinkVisible) {
            complaintApplicationService.sendComplaint(complaintDto);
        }
    }

}
