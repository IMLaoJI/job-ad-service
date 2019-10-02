package ch.admin.seco.jobs.services.jobadservice.infrastructure.scheduler.actuator.health;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
class TaskHealthAspect {

	private final TaskHealthRepository taskHealthRepository;

	TaskHealthAspect(TaskHealthRepository taskHealthRepository) {
		this.taskHealthRepository = taskHealthRepository;
	}

	@Pointcut("@annotation(ch.admin.seco.jobs.services.jobadservice.infrastructure.scheduler.actuator.health.TaskHealth)")
	protected void taskHealthPointcut() {
	}

	@Around("taskHealthPointcut()")
	protected Object healthTaskAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		final TaskHealthInformation taskHealthInformation = new TaskHealthInformation();
		final String taskHealthName = taskHealthName(joinPoint);
		this.taskHealthRepository.save(taskHealthName, taskHealthInformation);
		try {
			Object proceed = joinPoint.proceed();
			taskHealthInformation.completed();
			return proceed;
		} catch (Throwable throwable) {
			taskHealthInformation.failed(throwable);
			throw throwable;
		} finally {
			this.taskHealthRepository.save(taskHealthName, taskHealthInformation);
		}
	}

	private String taskHealthName(ProceedingJoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		return method.getAnnotation(TaskHealth.class).value();
	}
}
