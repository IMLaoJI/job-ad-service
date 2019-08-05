package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

public enum Sort {
    DATE_ASC ("asc"),
    DATE_DESC("desc");

    private final String value;

    private Sort(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

