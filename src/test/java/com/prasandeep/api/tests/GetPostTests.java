package com.prasandeep.api.tests;

import com.prasandeep.api.base.BaseTest;
import com.prasandeep.api.validators.PostValidator;
import com.prasandeep.api.validators.ResponseValidator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class GetPostTests extends BaseTest {

    @Test(groups = {"smoke", "get"}, description = "GET /posts returns a non-empty array")
    public void getAllPostsReturnsList() {
        Response response = posts.getAllPosts();
        ResponseValidator.assertStatus(response, 200);
        ResponseValidator.assertContentTypeJson(response);
        ResponseValidator.assertListNotEmpty(response);
        ResponseValidator.assertJsonSchema(response, "schemas/posts-array-schema.json");
    }

    @Test(groups = {"smoke", "get"}, description = "GET /posts/1 returns a single post")
    public void getPostByIdReturnsEntity() {
        Response response = posts.getPostById(1);
        ResponseValidator.assertStatus(response, 200);
        ResponseValidator.assertHasKeys(response, "userId", "id", "title", "body");
        ResponseValidator.assertJsonSchema(response, "schemas/post-schema.json");
        PostValidator.assertPostId(response, 1);
    }

    @Test(groups = {"negative", "get"}, description = "GET unknown id returns 404")
    public void getUnknownPostReturns404() {
        Response response = posts.getPostById(999999);
        ResponseValidator.assertStatus(response, 404);
    }
}
