package ch.admin.seco.jobs.services.jobadservice.application;

public enum BusinessLogObjectType {
    JOB_ADVERTISEMENT_LOG("JobAdvertisement");

    private String typeName;

    BusinessLogObjectType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
