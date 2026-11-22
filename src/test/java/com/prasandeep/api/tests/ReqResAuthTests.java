package com.prasandeep.api.tests;

import com.prasandeep.api.base.BaseTest;
import com.prasandeep.api.client.AuthContext;
import com.prasandeep.api.validators.ResponseValidator;
import com.prasandeep.api.validators.SoftAssertHelper;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Live ReqRes.in auth flows.
 * Login returns a token (Bearer pattern); /api/* calls use {@code x-api-key}.
 * Bearer-protected flows are covered by WireMockAuthTests.
 */
public class ReqResAuthTests extends BaseTest {

    private static final String DEMO_EMAIL = "eve.holt@reqres.in";
    private static final String DEMO_PASSWORD = "cityslicka";

    @Test(groups = {"reqres", "auth"}, description = "POST /api/login returns a token")
    public void loginReturnsToken() {
        Response response = auth.login(DEMO_EMAIL, DEMO_PASSWORD);
        ResponseValidator.assertStatus(response, 200);
        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "token should be present");
        Assert.assertFalse(token.isBlank(), "token should not be blank");
    }

    @Test(groups = {"reqres", "auth"}, description = "Login token stored in AuthContext; users fetched with x-api-key")
    public void loginThenFetchUsersWithApiKey() {
        Response login = auth.login(DEMO_EMAIL, DEMO_PASSWORD);
        ResponseValidator.assertStatus(login, 200);
        String token = login.jsonPath().getString("token");
        AuthContext.setBearerToken(token);
        Assert.assertEquals(AuthContext.getBearerToken(), token, "token stored in AuthContext");

        // ReqRes /api/* expects x-api-key (via reqResSpec), not Bearer
        AuthContext.addHeader("X-Request-Source", "api-framework");
        Response listResponse = users.getUsers(1);
        ResponseValidator.assertStatus(listResponse, 200);

        new SoftAssertHelper()
                .assertNotNull(listResponse.jsonPath().get("data"), "data array")
                .assertTrue(listResponse.jsonPath().getList("data").size() > 0, "users not empty")
                .assertJsonPathEquals(listResponse, "page", 1)
                .assertAll();
    }

    @Test(groups = {"reqres", "auth", "negative"}, description = "Login without password returns 400")
    public void loginMissingPasswordReturns400() {
        Response response = auth.login(DEMO_EMAIL, "");
        ResponseValidator.assertStatus(response, 400);
        Assert.assertNotNull(response.jsonPath().getString("error"), "error message expected");
    }
}
