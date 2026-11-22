package com.prasandeep.api.validators;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

/** Shared response assertions for API tests. */
public final class ResponseValidator {

    private ResponseValidator() {
    }

    public static void assertStatus(Response response, int expected) {
        Assert.assertEquals(response.statusCode(), expected,
                "Unexpected HTTP status. Body: " + response.asString());
    }

    public static void assertContentTypeJson(Response response) {
        String contentType = response.getContentType();
        Assert.assertTrue(
                contentType != null && contentType.contains("application/json"),
                "Expected JSON content type, got: " + contentType);
    }

    public static void assertJsonSchema(Response response, String schemaClasspath) {
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaClasspath));
    }

    public static void assertHasKeys(Response response, String... keys) {
        Map<String, Object> json = response.jsonPath().getMap(".");
        for (String key : keys) {
            Assert.assertTrue(json.containsKey(key), "Missing key: " + key + " in " + json.keySet());
        }
    }

    public static void assertListNotEmpty(Response response) {
        List<?> list = response.jsonPath().getList("$");
        Assert.assertNotNull(list, "Expected JSON array");
        Assert.assertFalse(list.isEmpty(), "Expected non-empty array");
    }
}
