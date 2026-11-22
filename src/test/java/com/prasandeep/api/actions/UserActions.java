package com.prasandeep.api.actions;

import com.prasandeep.api.client.RequestSpecFactory;
import com.prasandeep.api.endpoints.ReqResEndpoints;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/** User HTTP actions against ReqRes. */
public class UserActions {

    public Response getUsers(int page) {
        return given()
                .spec(RequestSpecFactory.reqResSpec())
                .queryParam("page", page)
                .when()
                .get(ReqResEndpoints.USERS)
                .then()
                .extract()
                .response();
    }

    public Response getUserById(int id) {
        return given()
                .spec(RequestSpecFactory.reqResSpec())
                .pathParam("id", id)
                .when()
                .get(ReqResEndpoints.USER_BY_ID)
                .then()
                .extract()
                .response();
    }
}
