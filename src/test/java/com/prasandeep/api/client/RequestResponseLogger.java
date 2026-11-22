package com.prasandeep.api.client;

import com.prasandeep.api.config.ConfigReader;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Conditional request/response logging controlled by {@code log.request.response}.
 * Avoids calling {@code getBody()} on the request spec (RestAssured can ClassCast on POJO bodies).
 */
public class RequestResponseLogger implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestResponseLogger.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        boolean enabled = ConfigReader.getBoolean("log.request.response");
        if (enabled) {
            LOG.info(">>> {} {}", requestSpec.getMethod(), requestSpec.getURI());
            if (requestSpec.getHeaders() != null) {
                LOG.info(">>> Headers: {}", requestSpec.getHeaders().toString());
            }
        }

        Response response = ctx.next(requestSpec, responseSpec);

        if (enabled) {
            LOG.info("<<< Status: {}", response.statusCode());
            LOG.info("<<< Body: {}", response.asString());
        }
        return response;
    }
}
