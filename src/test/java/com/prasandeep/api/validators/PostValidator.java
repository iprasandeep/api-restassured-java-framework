package com.prasandeep.api.validators;

import com.prasandeep.api.models.Post;
import io.restassured.response.Response;
import org.testng.Assert;

/** Post-specific assertions. */
public final class PostValidator {

    private PostValidator() {
    }

    public static void assertPostMatches(Response response, Post expected) {
        Post actual = response.as(Post.class);
        Assert.assertEquals(actual.getTitle(), expected.getTitle(), "title mismatch");
        Assert.assertEquals(actual.getBody(), expected.getBody(), "body mismatch");
        if (expected.getUserId() != null) {
            Assert.assertEquals(actual.getUserId(), expected.getUserId(), "userId mismatch");
        }
        Assert.assertNotNull(actual.getId(), "id should be present");
    }

    public static void assertPostId(Response response, int expectedId) {
        Assert.assertEquals(response.jsonPath().getInt("id"), expectedId, "id mismatch");
    }
}
