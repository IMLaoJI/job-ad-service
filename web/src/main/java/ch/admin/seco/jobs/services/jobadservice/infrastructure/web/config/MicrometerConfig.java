package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MicrometerConfig {

    private final Logger log = LoggerFactory.getLogger(MicrometerConfig.class);

    @Bean
    public TimedAspect timedAspect(MeterRegistry meterRegistry) {
        return new TimedAspect(meterRegistry) {
            @Override
            public Object timedMethod(ProceedingJoinPoint pjp) throws Throwable {
                String clazz = pjp.getStaticPart().getSignature().getDeclaringTypeName();
                String method = pjp.getStaticPart().getSignature().getName();

                log.trace(".start to execute {}.{}", clazz, method);

                try {
                    return super.timedMethod(pjp);
                } finally {
                    log.trace("..finished to execute {}.{}", clazz, method);
                }
            }
        };
    }
}
