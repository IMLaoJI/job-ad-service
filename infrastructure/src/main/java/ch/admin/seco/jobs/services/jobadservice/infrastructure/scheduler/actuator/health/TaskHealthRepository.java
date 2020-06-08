package ch.admin.seco.jobs.services.jobadservice.infrastructure.scheduler.actuator.health;

import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
class TaskHealthRepository {

	private final Map<String, TaskHealthInformation> results = new HashMap<>();

	void save(String id, TaskHealthInformation data) {
		results.put(id, data);
	}

	Map<String, TaskHealthInformation> findAll() {
		return Collections.unmodifiableMap(this.results);
	}

}
