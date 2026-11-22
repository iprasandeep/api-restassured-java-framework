package com.prasandeep.api.actions;

import com.prasandeep.api.client.RequestSpecFactory;
import com.prasandeep.api.endpoints.PostEndpoints;
import com.prasandeep.api.models.Post;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/** HTTP actions for Posts API — no assertions here. */
public class PostActions {

    public Response getAllPosts() {
        return given()
                .spec(RequestSpecFactory.baseSpec())
                .when()
                .get(PostEndpoints.POSTS)
                .then()
                .extract()
                .response();
    }

    public Response getPostById(int id) {
        return given()
                .spec(RequestSpecFactory.baseSpec())
                .pathParam("id", id)
                .when()
                .get(PostEndpoints.POST_BY_ID)
                .then()
                .extract()
                .response();
    }

    public Response createPost(Post body) {
        return given()
                .spec(RequestSpecFactory.baseSpec())
                .body(body)
                .when()
                .post(PostEndpoints.POSTS)
                .then()
                .extract()
                .response();
    }

    public Response updatePost(int id, Post body) {
        return given()
                .spec(RequestSpecFactory.baseSpec())
                .pathParam("id", id)
                .body(body)
                .when()
                .put(PostEndpoints.POST_BY_ID)
                .then()
                .extract()
                .response();
    }

    public Response patchPost(int id, Post body) {
        return given()
                .spec(RequestSpecFactory.baseSpec())
                .pathParam("id", id)
                .body(body)
                .when()
                .patch(PostEndpoints.POST_BY_ID)
                .then()
                .extract()
                .response();
    }

    public Response deletePost(int id) {
        return given()
                .spec(RequestSpecFactory.baseSpec())
                .pathParam("id", id)
                .when()
                .delete(PostEndpoints.POST_BY_ID)
                .then()
                .extract()
                .response();
    }
}
