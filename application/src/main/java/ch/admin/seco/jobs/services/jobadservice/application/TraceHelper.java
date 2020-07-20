package ch.admin.seco.jobs.services.jobadservice.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

public class TraceHelper {
    private static final Logger LOG = LoggerFactory.getLogger(TraceHelper.class);

    public static StopWatch stopWatch() {
        return new StopWatch();
    }

    public static void startTask(String prefix, String task, StopWatch stopWatch) {
        LOG.trace(prefix + " start: {}", task);
        stopWatch.start(task);
    }

    public static void stopTask(StopWatch stopWatch) {
        stopWatch.stop();
        LOG.trace("finished: {} in {} ms", stopWatch.getLastTaskName(), stopWatch.getLastTaskTimeMillis());
    }
}
