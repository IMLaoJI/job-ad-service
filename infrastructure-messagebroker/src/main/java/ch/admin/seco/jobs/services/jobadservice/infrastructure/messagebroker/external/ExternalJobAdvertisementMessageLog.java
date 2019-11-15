package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.external;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class ExternalJobAdvertisementMessageLog {

    @Id
    @NotNull
    private String fingerprint;

    @NotNull
    private LocalDate lastMessageDate;

    public ExternalJobAdvertisementMessageLog() {
    }

    public ExternalJobAdvertisementMessageLog(@NotNull String fingerprint, @NotNull LocalDate lastMessageDate) {
        this.fingerprint = fingerprint;
        this.lastMessageDate = lastMessageDate;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public LocalDate getLastMessageDate() {
        return lastMessageDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalJobAdvertisementMessageLog that = (ExternalJobAdvertisementMessageLog) o;
        return Objects.equals(fingerprint, that.fingerprint);
    }

    @Override
    public int hashCode() {

        return Objects.hash(fingerprint);
    }

    @Override
    public String toString() {
        return "ExternalJobAdvertisementMessageLog{" +
                "fingerprint='" + fingerprint + '\'' +
                ", lastMessageDate=" + lastMessageDate +
                '}';
    }
}
