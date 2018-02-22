package ch.admin.seco.jobs.services.jobadservice.application;

import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;

public interface RavRegistrationService {

    void registrate(JobAdvertisement jobAdvertisement);

    void deregister(JobAdvertisement jobAdvertisement);

}