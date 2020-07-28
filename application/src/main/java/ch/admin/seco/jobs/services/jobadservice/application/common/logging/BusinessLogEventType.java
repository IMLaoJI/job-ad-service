package ch.admin.seco.jobs.services.jobadservice.application.common.logging;

import ch.admin.seco.alv.shared.logger.business.BusinessLogEnum;

public enum BusinessLogEventType implements BusinessLogEnum {
    JOB_ADVERTISEMENT_FAVORITE_EVENT("JOB_ADVERTISEMENT_FAVORITE"),
    JOB_ADVERTISEMENT_ACCESS_EVENT("JOB_ADVERTISEMENT_ACCESS");

    private String enumName;

    BusinessLogEventType(String enumName) {
        this.enumName = enumName;
    }

    @Override
    public String enumName() {
        return enumName;
    }
}
