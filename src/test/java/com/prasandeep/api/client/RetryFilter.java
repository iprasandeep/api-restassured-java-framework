package com.prasandeep.api.client;

import com.prasandeep.api.config.ConfigReader;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

/**
 * Retries transient failures (5xx / connection errors) using config-driven attempts.
 * Re-entry safe so parallel / nested filter runs do not recurse forever.
 */
public class RetryFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(RetryFilter.class);
    private static final ThreadLocal<Boolean> IN_RETRY = ThreadLocal.withInitial(() -> false);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        if (Boolean.TRUE.equals(IN_RETRY.get())) {
            return ctx.next(requestSpec, responseSpec);
        }

        int maxAttempts = ConfigReader.getInt("retry.max.attempts");
        long delayMs = ConfigReader.getInt("retry.delay.ms");
        Response response = null;
        RuntimeException lastError = null;

        IN_RETRY.set(true);
        try {
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    response = attempt == 1
                            ? ctx.next(requestSpec, responseSpec)
                            : resend(requestSpec);
                    if (response.statusCode() < 500 || attempt == maxAttempts) {
                        return response;
                    }
                    LOG.warn("Attempt {}/{} got HTTP {} — retrying after {}ms",
                            attempt, maxAttempts, response.statusCode(), delayMs);
                } catch (RuntimeException ex) {
                    lastError = ex;
                    if (attempt == maxAttempts) {
                        throw ex;
                    }
                    LOG.warn("Attempt {}/{} failed: {} — retrying after {}ms",
                            attempt, maxAttempts, ex.getMessage(), delayMs);
                }
                sleep(delayMs);
            }
        } finally {
            IN_RETRY.set(false);
        }

        if (response != null) {
            return response;
        }
        if (lastError != null) {
            throw lastError;
        }
        throw new IllegalStateException("Retry loop exited unexpectedly");
    }

    private static Response resend(FilterableRequestSpecification requestSpec) {
        Method method = Method.valueOf(requestSpec.getMethod());
        String path = requestSpec.getDerivedPath();
        if (path == null || path.isBlank()) {
            path = requestSpec.getUserDefinedPath();
        }
        return given().spec(requestSpec).request(method, path);
    }

    private static void sleep(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry interrupted", e);
        }
    }
}
