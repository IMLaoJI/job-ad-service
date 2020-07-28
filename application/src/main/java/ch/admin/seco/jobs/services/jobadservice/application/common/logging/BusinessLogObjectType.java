package ch.admin.seco.jobs.services.jobadservice.application.common.logging;

import ch.admin.seco.alv.shared.logger.business.BusinessLogEnum;

public enum BusinessLogObjectType implements BusinessLogEnum {
    JOB_ADVERTISEMENT_LOG("JobAdvertisement");

    private String enumName;

    BusinessLogObjectType(String enumName) {
        this.enumName = enumName;
    }

    @Override
    public String enumName() {
        return enumName;
    }
}
