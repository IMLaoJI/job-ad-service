package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller;

import ch.admin.seco.jobs.services.jobadservice.application.complaint.ComplaintApplicationService;
import ch.admin.seco.jobs.services.jobadservice.application.complaint.ComplaintDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/complaint")
public class ComplaintRestController {
    private final ComplaintApplicationService complaintApplicationService;

    public ComplaintRestController(ComplaintApplicationService complaintApplicationService) {
        this.complaintApplicationService = complaintApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void sendComplaint(@RequestBody @Valid ComplaintDto complaintDto) {
        complaintApplicationService.sendComplaint(complaintDto);
    }

}
