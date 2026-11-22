package com.prasandeep.api.tests;

import com.prasandeep.api.base.BaseTest;
import com.prasandeep.api.models.Post;
import com.prasandeep.api.validators.PostValidator;
import com.prasandeep.api.validators.ResponseValidator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.List;

public class MutatingPostTests extends BaseTest {

    @DataProvider(name = "createPosts")
    public Object[][] createPosts() throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("testdata/posts.json")) {
            List<Post> postsData = new ObjectMapper().readValue(in, new TypeReference<List<Post>>() {
            });
            Object[][] rows = new Object[postsData.size()][1];
            for (int i = 0; i < postsData.size(); i++) {
                rows[i][0] = postsData.get(i);
            }
            return rows;
        }
    }

    @Test(groups = {"smoke", "post"}, dataProvider = "createPosts",
            description = "POST /posts creates a resource (JSONPlaceholder fake create)")
    public void createPostReturns201(Post payload) {
        Response response = posts.createPost(payload);
        ResponseValidator.assertStatus(response, 201);
        PostValidator.assertPostMatches(response, payload);
    }

    @Test(groups = {"put"}, description = "PUT /posts/1 replaces fields")
    public void updatePostWithPut() {
        Post payload = new Post(1, "Updated title", "Updated body");
        Response response = posts.updatePost(1, payload);
        ResponseValidator.assertStatus(response, 200);
        PostValidator.assertPostMatches(response, payload);
        PostValidator.assertPostId(response, 1);
    }

    @Test(groups = {"patch"}, description = "PATCH /posts/1 updates title only")
    public void patchPostTitle() {
        Post payload = new Post();
        payload.setTitle("Patched title only");
        Response response = posts.patchPost(1, payload);
        ResponseValidator.assertStatus(response, 200);
        ResponseValidator.assertHasKeys(response, "id", "title");
        org.testng.Assert.assertEquals(response.jsonPath().getString("title"), "Patched title only");
    }

    @Test(groups = {"delete"}, description = "DELETE /posts/1 returns success")
    public void deletePost() {
        Response response = posts.deletePost(1);
        // JSONPlaceholder returns 200 for delete
        ResponseValidator.assertStatus(response, 200);
    }
}
