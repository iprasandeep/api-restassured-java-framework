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

/** Local WireMock demos: Bearer auth, cookies, headers. */
public class WireMockAuthTests {

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

    @Test(groups = {"wiremock", "auth"}, description = "Bearer token unlocks protected stub")
    public void bearerAllowsProtectedResource() {
        AuthContext.setBearerToken("wiremock-token-123");
        Response response = given()
                .spec(RequestSpecFactory.baseSpec(baseUri))
                .when()
                .get("/secure/profile")
                .then()
                .extract()
                .response();

        ResponseValidator.assertStatus(response, 200);
        Assert.assertEquals(response.jsonPath().getString("name"), "Interview User");
    }

    @Test(groups = {"wiremock", "auth", "negative"}, description = "Missing Bearer returns 401")
    public void missingBearerReturns401() {
        Response response = given()
                .spec(RequestSpecFactory.baseSpec(baseUri))
                .when()
                .get("/secure/profile")
                .then()
                .extract()
                .response();

        ResponseValidator.assertStatus(response, 401);
    }

    @Test(groups = {"wiremock", "auth"}, description = "Set-Cookie then cookie-authenticated call")
    public void cookieSessionFlow() {
        Response session = given()
                .spec(RequestSpecFactory.baseSpec(baseUri))
                .when()
                .get("/session")
                .then()
                .extract()
                .response();

        ResponseValidator.assertStatus(session, 200);
        String cookie = session.getCookie("sessionId");
        Assert.assertEquals(cookie, "abc-xyz");

        AuthContext.addCookie("sessionId", cookie);
        Response me = given()
                .spec(RequestSpecFactory.baseSpec(baseUri))
                .when()
                .get("/session/me")
                .then()
                .extract()
                .response();

        ResponseValidator.assertStatus(me, 200);
        Assert.assertEquals(me.jsonPath().getString("user"), "cookie-user");
    }

    @Test(groups = {"wiremock"}, description = "Custom header is accepted by stubbed API")
    public void customHeaderIsSent() {
        AuthContext.addHeader("X-Client", "restassured-framework");
        AuthContext.setBearerToken("wiremock-token-123");

        Response response = given()
                .spec(RequestSpecFactory.baseSpec(baseUri))
                .when()
                .get("/secure/profile")
                .then()
                .extract()
                .response();

        ResponseValidator.assertStatus(response, 200);
    }
}
