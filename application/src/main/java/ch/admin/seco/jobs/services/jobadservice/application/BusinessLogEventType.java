package ch.admin.seco.jobs.services.jobadservice.application;

public enum BusinessLogEventType {
    JOB_ADVERTISEMENT_FAVORITE_EVENT("JOB_ADVERTISEMENT_FAVORITE"),
    JOB_ADVERTISEMENT_ACCESS_EVENT("JOB_ADVERTISEMENT_ACCESS");

    private String typeName;

    BusinessLogEventType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
