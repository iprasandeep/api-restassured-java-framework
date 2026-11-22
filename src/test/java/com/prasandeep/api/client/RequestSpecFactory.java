package com.prasandeep.api.client;

import com.prasandeep.api.config.ConfigReader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

/** Shared RestAssured request specifications (base URI, auth, filters). */
public final class RequestSpecFactory {

    private RequestSpecFactory() {
    }

    /** Spec for the primary API ({@code base.uri}). */
    public static RequestSpecification baseSpec() {
        return baseSpec(ConfigReader.get("base.uri"));
    }

    /**
     * Spec for ReqRes ({@code reqres.base.uri}).
     * Sends {@code x-api-key} (ReqRes /api/* rejects Bearer).
     */
    public static RequestSpecification reqResSpec() {
        RequestSpecBuilder builder = commonBuilder(ConfigReader.get("reqres.base.uri"));
        builder.addHeader("x-api-key", ConfigReader.getOrDefault("reqres.api.key", "reqres-free-v1"));
        applyHeadersAndCookies(builder);
        return builder.build();
    }

    /** Spec with an explicit base URI (e.g. WireMock {@code http://localhost:port}). */
    public static RequestSpecification baseSpec(String baseUri) {
        RequestSpecBuilder builder = commonBuilder(baseUri);
        applyAuth(builder);
        return builder.build();
    }

    /**
     * Spec that always requires a Bearer token (from AuthContext or {@code auth.bearer.token}).
     */
    public static RequestSpecification authenticatedSpec() {
        return authenticatedSpec(ConfigReader.get("base.uri"));
    }

    public static RequestSpecification authenticatedSpec(String baseUri) {
        String token = AuthContext.getBearerToken();
        if (token == null || token.isBlank()) {
            token = ConfigReader.getOrDefault("auth.bearer.token", "");
        }
        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "No Bearer token: call AuthContext.setBearerToken(...) or set auth.bearer.token");
        }
        AuthContext.setBearerToken(token);
        return baseSpec(baseUri);
    }

    private static RequestSpecBuilder commonBuilder(String baseUri) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .addFilter(new RequestResponseLogger())
                .addFilter(new RetryFilter())
                .log(LogDetail.URI);
    }

    private static void applyAuth(RequestSpecBuilder builder) {
        String token = AuthContext.getBearerToken();
        if (token == null || token.isBlank()) {
            token = ConfigReader.getOrDefault("auth.bearer.token", "");
        }
        if (token != null && !token.isBlank()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }
        applyHeadersAndCookies(builder);
    }

    private static void applyHeadersAndCookies(RequestSpecBuilder builder) {
        for (Map.Entry<String, String> h : AuthContext.getHeaders().entrySet()) {
            builder.addHeader(h.getKey(), h.getValue());
        }
        for (Map.Entry<String, String> c : AuthContext.getCookies().entrySet()) {
            builder.addCookie(c.getKey(), c.getValue());
        }
    }
}
