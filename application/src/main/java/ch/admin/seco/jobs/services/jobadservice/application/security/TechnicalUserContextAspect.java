package ch.admin.seco.jobs.services.jobadservice.application.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
class TechnicalUserContextAspect {

	@Pointcut("@annotation(ch.admin.seco.jobs.services.jobadservice.application.LoginAsTechnicalUser)")
	void loginAsTechnicalUserPointcut() {
	}

	@Around("loginAsTechnicalUserPointcut() ")
	Object initSecurityContextAround(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			TechnicalUserContextUtil.initContext();
			return joinPoint.proceed();
		} finally {
			TechnicalUserContextUtil.clearContext();
		}
	}
}
