package ch.admin.seco.jobs.services.jobadservice.application;

import javax.validation.Valid;

public interface MailSenderService {

    void send(@Valid MailSenderData mailSenderData);

}
