package com.prasandeep.api.actions;

import com.prasandeep.api.client.RequestSpecFactory;
import com.prasandeep.api.endpoints.ReqResEndpoints;
import com.prasandeep.api.models.LoginRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/** Auth HTTP actions against ReqRes (login → token). */
public class AuthActions {

    public Response login(LoginRequest body) {
        return given()
                .spec(RequestSpecFactory.reqResSpec())
                .body(body)
                .when()
                .post(ReqResEndpoints.LOGIN)
                .then()
                .extract()
                .response();
    }

    public Response login(String email, String password) {
        return login(new LoginRequest(email, password));
    }
}
