package ch.admin.seco.jobs.services.jobadservice.infrastructure.database;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.H2SequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.PostgresSequenceMaxValueIncrementer;

@Configuration
public class SequenceGeneratorConfiguration {

	private final DataSource dataSource;

	private final DataSourceProperties dataSourceProperties;

	private final String stellennummerEgovGeneratorSequenceName;

	public SequenceGeneratorConfiguration(DataSource dataSource,
			DataSourceProperties dataSourceProperties,
			@Value("${jobAdvertisement.stellennummerEgovGeneratorSequenceName:STELLEN_NUM_EGOV_SEQ}")
					String egovNumberGeneratorSequenceName) {
		this.dataSource = dataSource;
		this.dataSourceProperties = dataSourceProperties;
		this.stellennummerEgovGeneratorSequenceName = egovNumberGeneratorSequenceName;
	}

	@Bean
	public DataFieldMaxValueIncrementer stellennummerEgovGenerator() {
		final String driverClassName = this.dataSourceProperties.determineDriverClassName();

		if (driverClassName.toUpperCase().contains("H2")) {
			return new H2SequenceMaxValueIncrementer(dataSource, stellennummerEgovGeneratorSequenceName);
		} else if (driverClassName.toUpperCase().contains("POSTGRESQL")) {
			return new PostgresSequenceMaxValueIncrementer(dataSource, stellennummerEgovGeneratorSequenceName);
		} else {
			throw new UnsupportedOperationException(String.format("Implementation is not found for: %s", driverClassName));
		}
	}

}
