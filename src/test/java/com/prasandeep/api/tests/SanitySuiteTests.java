package com.prasandeep.api.tests;

import com.prasandeep.api.base.BaseTest;
import com.prasandeep.api.models.Post;
import com.prasandeep.api.validators.PostValidator;
import com.prasandeep.api.validators.ResponseValidator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

/** All core API scenarios in one sanity class. */
public class SanitySuiteTests extends BaseTest {

    @Test(priority = 1, groups = {"sanity", "smoke"}, description = "GET all posts")
    public void getAllPosts() {
        Response response = posts.getAllPosts();
        ResponseValidator.assertStatus(response, 200);
        ResponseValidator.assertListNotEmpty(response);
        ResponseValidator.assertJsonSchema(response, "schemas/posts-array-schema.json");
    }

    @Test(priority = 2, groups = {"sanity", "smoke"}, description = "GET post by id")
    public void getPostById() {
        Response response = posts.getPostById(1);
        ResponseValidator.assertStatus(response, 200);
        ResponseValidator.assertJsonSchema(response, "schemas/post-schema.json");
        PostValidator.assertPostId(response, 1);
    }

    @Test(priority = 3, groups = {"sanity", "negative"}, description = "GET unknown post 404")
    public void getUnknownPost() {
        ResponseValidator.assertStatus(posts.getPostById(999999), 404);
    }

    @Test(priority = 4, groups = {"sanity", "smoke"}, description = "POST create")
    public void createPost() {
        Post payload = new Post(1, "Sanity create", "Body from sanity suite");
        Response response = posts.createPost(payload);
        ResponseValidator.assertStatus(response, 201);
        PostValidator.assertPostMatches(response, payload);
    }

    @Test(priority = 5, groups = {"sanity"}, description = "PUT update")
    public void putPost() {
        Post payload = new Post(1, "Sanity put", "Updated");
        Response response = posts.updatePost(1, payload);
        ResponseValidator.assertStatus(response, 200);
        PostValidator.assertPostMatches(response, payload);
    }

    @Test(priority = 6, groups = {"sanity"}, description = "PATCH update")
    public void patchPost() {
        Post payload = new Post();
        payload.setTitle("Sanity patch");
        Response response = posts.patchPost(1, payload);
        ResponseValidator.assertStatus(response, 200);
        org.testng.Assert.assertEquals(response.jsonPath().getString("title"), "Sanity patch");
    }

    @Test(priority = 7, groups = {"sanity"}, description = "DELETE post")
    public void deletePost() {
        ResponseValidator.assertStatus(posts.deletePost(1), 200);
    }
}
