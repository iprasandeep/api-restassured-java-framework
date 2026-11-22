package com.prasandeep.api.client;

import com.prasandeep.api.config.ConfigReader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/** Shared RestAssured request specification. */
public final class RequestSpecFactory {

    private RequestSpecFactory() {
    }

    public static RequestSpecification baseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("base.uri"))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .log(LogDetail.URI)
                .build();
    }
}
