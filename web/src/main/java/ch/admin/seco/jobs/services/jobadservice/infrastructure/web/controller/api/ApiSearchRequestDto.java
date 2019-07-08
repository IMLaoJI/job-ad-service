package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

public class ApiSearchRequestDto {

    private String status[];

    public String[] getStatus() {
        return status;
    }

    public ApiSearchRequestDto setStatus(String[] status) {
        this.status = status;
        return this;
    }
}
