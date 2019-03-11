package ch.admin.seco.jobs.services.jobadservice.application;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.validation.annotation.Validated;

import ch.admin.seco.jobs.services.jobadservice.core.conditions.Condition;

@Validated
public class MailSenderData {

    @NotBlank
    private final String subject;

    @NotEmpty
    private final Set<@Email String> to = new HashSet<>();

    private final Set<@Email String> cc = new HashSet<>();

    private final Set<@Email String> bcc = new HashSet<>();

    private final String templateName;

    private final Map<String, Object> templateVariables;

    private final Locale locale;

    private MailSenderData(Builder builder) {
        this.subject = Condition.notBlank(builder.subject, "E-Mail must contain a subject.");
        this.to.addAll(builder.to);
        this.cc.addAll(builder.cc);
        this.bcc.addAll(builder.bcc);
        this.templateName = Condition.notBlank(builder.templateName, "E-Mail must contain a template.");
        if (builder.templateVariables != null) {
            this.templateVariables = builder.templateVariables;
        } else {
            this.templateVariables = Collections.emptyMap();
        }
        this.locale = Condition.notNull(builder.locale, "E-Mail must contain a locale.");
        Condition.notEmpty(builder.to, "E-Mail must contain at least one receiver.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MailSenderData that = (MailSenderData) o;
        return Objects.equals(subject, that.subject) &&
                Objects.equals(to, that.to) &&
                Objects.equals(cc, that.cc) &&
                Objects.equals(bcc, that.bcc) &&
                Objects.equals(templateName, that.templateName) &&
                Objects.equals(templateVariables, that.templateVariables) &&
                Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, to, cc, bcc, templateName, templateVariables, locale);
    }

    public String getSubject() {
        return subject;
    }

    public Set<String> getTo() {
        return to;
    }

    public Set<String> getCc() {
        return cc;
    }

    public Set<String> getBcc() {
        return this.bcc;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Map<String, Object> getTemplateVariables() {
        return templateVariables;
    }

    public Locale getLocale() {
        return locale;
    }

    public static class Builder {

        private String subject;

        private Set<String> to = new HashSet<>();

        private Set<String> cc = new HashSet<>();

        private Set<String> bcc = new HashSet<>();

        private String templateName;

        private Map<String, Object> templateVariables;

        private Locale locale;

        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder setTo(String... to) {
            this.to.addAll(Arrays.asList(to));
            return this;
        }

        public Builder setCc(String... cc) {
            this.cc.addAll(Arrays.asList(cc));
            return this;
        }

        public Builder setBcc(String... bcc) {
            this.bcc.addAll(Arrays.asList(bcc));
            return this;
        }

        public Builder setTemplateName(String templateName) {
            this.templateName = templateName;
            return this;
        }

        public Builder setTemplateVariables(Map<String, Object> templateVariables) {
            this.templateVariables = templateVariables;
            return this;
        }

        public Builder setLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public MailSenderData build() {
            return new MailSenderData(this);
        }

    }

    @Override
    public String toString() {
        return "MailSenderData{" +
                "subject='" + subject + '\'' +
                ", to=" + to +
                ", cc=" + cc +
                ", bcc=" + bcc +
                ", templateName='" + templateName + '\'' +
                ", templateVariables=" + templateVariables +
                ", locale=" + locale +
                '}';
    }
}
