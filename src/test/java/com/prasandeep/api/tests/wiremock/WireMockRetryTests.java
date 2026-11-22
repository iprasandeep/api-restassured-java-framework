package com.prasandeep.api.tests.wiremock;

import com.prasandeep.api.client.AuthContext;
import com.prasandeep.api.client.RequestSpecFactory;
import com.prasandeep.api.validators.ResponseValidator;
import com.prasandeep.api.wiremock.WireMockServerManager;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/** WireMock flaky endpoint — RetryFilter recovers after 503s. */
public class WireMockRetryTests {

    private final WireMockServerManager wireMock = new WireMockServerManager();
    private String baseUri;

    @BeforeClass(alwaysRun = true)
    public void startServer() {
        wireMock.start();
        baseUri = wireMock.baseUri();
    }

    @AfterClass(alwaysRun = true)
    public void stopServer() {
        wireMock.stop();
    }

    @BeforeMethod(alwaysRun = true)
    public void clearAuth() {
        AuthContext.clear();
    }

    @Test(groups = {"wiremock", "retry"}, description = "RetryFilter recovers from two 503 responses")
    public void retriesUntilSuccess() {
        Response response = given()
                .spec(RequestSpecFactory.baseSpec(baseUri))
                .when()
                .get("/flaky")
                .then()
                .extract()
                .response();

        ResponseValidator.assertStatus(response, 200);
        Assert.assertEquals(response.jsonPath().getString("status"), "recovered");
    }
}
