package com.prasandeep.api.validators;

import io.restassured.response.Response;
import org.testng.asserts.SoftAssert;

/**
 * Soft assertions so a test can collect multiple failures before failing once.
 * Always call {@link #assertAll()} at the end of the test.
 */
public final class SoftAssertHelper {

    private final SoftAssert soft = new SoftAssert();

    public SoftAssertHelper assertStatus(Response response, int expected) {
        soft.assertEquals(response.statusCode(), expected,
                "Unexpected HTTP status. Body: " + response.asString());
        return this;
    }

    public SoftAssertHelper assertEquals(Object actual, Object expected, String message) {
        soft.assertEquals(actual, expected, message);
        return this;
    }

    public SoftAssertHelper assertTrue(boolean condition, String message) {
        soft.assertTrue(condition, message);
        return this;
    }

    public SoftAssertHelper assertNotNull(Object actual, String message) {
        soft.assertNotNull(actual, message);
        return this;
    }

    public SoftAssertHelper assertJsonPathEquals(Response response, String path, Object expected) {
        soft.assertEquals(response.jsonPath().get(path), expected, "jsonPath " + path);
        return this;
    }

    public void assertAll() {
        soft.assertAll();
    }
}
