package ru.javawebinar.topjava;

import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

public class MeasureTest extends Stopwatch {

    private static final Logger logger = getLogger(Stopwatch.class);
    private static final Map<String, Long> overview = new HashMap<>();


    @Override
    protected void finished(long nanos, Description description) {
        String testName = description.getMethodName();
        long millis = TimeUnit.NANOSECONDS.toMillis(nanos);
        overview.put(testName, millis);
        logger.info("Test {} finished, spent {} milliseconds", testName, millis);
    }


    public static String getOverview() {
        return overview.entrySet().stream()
                .map(entry -> format("\t%-23s - %d ms", entry.getKey(), entry.getValue()))
                .collect(joining(lineSeparator(), lineSeparator(), ""));
    }
}
