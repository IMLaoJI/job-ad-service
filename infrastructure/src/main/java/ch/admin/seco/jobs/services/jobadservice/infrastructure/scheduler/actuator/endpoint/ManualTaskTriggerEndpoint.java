package ch.admin.seco.jobs.services.jobadservice.infrastructure.scheduler.actuator.endpoint;


import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Endpoint(id = "manual-task-trigger")
class ManualTaskTriggerEndpoint {

	private final Map<String, ManualTaskTrigger> taskTriggers;

	private ManualTaskTriggerEndpoint(Map<String, ManualTaskTrigger> taskTriggers) {
		this.taskTriggers = taskTriggers;
	}

	@ReadOperation
	public void invoke(@Selector String name) {
		this.taskTriggers.entrySet().stream()
				.filter(entry -> entry.getKey().equalsIgnoreCase(name))
				.map(Map.Entry::getValue)
				.findFirst()
				.ifPresent(ManualTaskTrigger::invoke);
	}

}
