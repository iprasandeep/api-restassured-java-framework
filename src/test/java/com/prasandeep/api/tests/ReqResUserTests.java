package com.prasandeep.api.tests;

import com.prasandeep.api.base.BaseTest;
import com.prasandeep.api.validators.ResponseValidator;
import com.prasandeep.api.validators.SoftAssertHelper;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Live ReqRes.in user resource tests. */
public class ReqResUserTests extends BaseTest {

    @Test(groups = {"reqres", "smoke"}, description = "GET /api/users?page=2 returns list")
    public void getUsersPage() {
        Response response = users.getUsers(2);
        ResponseValidator.assertStatus(response, 200);
        new SoftAssertHelper()
                .assertJsonPathEquals(response, "page", 2)
                .assertNotNull(response.jsonPath().get("data"), "data")
                .assertTrue(response.jsonPath().getList("data").size() > 0, "non-empty data")
                .assertAll();
    }

    @Test(groups = {"reqres"}, description = "GET /api/users/2 returns a single user")
    public void getUserById() {
        Response response = users.getUserById(2);
        ResponseValidator.assertStatus(response, 200);
        Assert.assertEquals(response.jsonPath().getInt("data.id"), 2, "id mismatch");
        Assert.assertNotNull(response.jsonPath().getString("data.email"), "email");
        Assert.assertNotNull(response.jsonPath().getString("data.first_name"), "first_name");
    }
}
