package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.config;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.EnumSet;

@Configuration
public class WebMvcConfig implements ServletContextInitializer, WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry formatterRegistry) {
        DateTimeFormatterRegistrar dateTimeFormatterRegistrar = new DateTimeFormatterRegistrar();
        dateTimeFormatterRegistrar.setUseIsoFormat(true);
        dateTimeFormatterRegistrar.registerFormatters(formatterRegistry);
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        initMDCInserting(servletContext);
    }

    private void initMDCInserting(ServletContext servletContext) {
        FilterRegistration.Dynamic mdcInsertingFilter = servletContext.addFilter("webappMDCInsertingFilter", new MDCInsertingServletFilter());
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);

        mdcInsertingFilter.addMappingForUrlPatterns(dispatcherTypes, true, "/*");
        mdcInsertingFilter.setAsyncSupported(true);
    }
}
