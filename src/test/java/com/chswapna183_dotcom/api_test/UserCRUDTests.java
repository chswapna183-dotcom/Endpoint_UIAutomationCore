package com.chswapna183_dotcom.api_test;

import com.chswapna183_dotcom.api.base.BaseAPITest;
import com.chswapna183_dotcom.api.endpoints.UserEndpoints;
import com.chswapna183_dotcom.api.payload.UserPayload;
import com.chswapna183_dotcom.api.utils.APIUtil;
import com.chswapna183_dotcom.support.MockServer;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UserCRUDTests extends BaseAPITest {

    @BeforeClass(alwaysRun = true)
    public void startMockServer() {
        MockServer.start();
        System.setProperty("apiBaseUrl", MockServer.getBaseUrl());
        System.setProperty("apiBasePath", "/api");
    }

    @AfterClass(alwaysRun = true)
    public void stopMockServer() {
        System.clearProperty("apiBaseUrl");
        System.clearProperty("apiBasePath");
        MockServer.stop();
    }

    @Test(groups = "api")
    public void createUser_returns201AndGeneratedFields() {
        UserPayload created = createUser("morpheus", "leader");
        APIUtil.assertNotBlank(created.getId(), "Created user id should be present.");
        APIUtil.assertNotBlank(created.getCreatedAt(), "createdAt timestamp should be present.");
    }

    @Test(groups = "api")
    public void getUser_returns200AndUserData() {
        UserPayload created = createUser("trinity", "operator");

        Response response = given()
                .when()
                .get(UserEndpoints.userById(created.getId()));

        APIUtil.assertStatusCode(response, 200);

        UserPayload fetched = response.as(UserPayload.class);
        Assert.assertEquals(fetched.getId(), created.getId(), "User id should match.");
        Assert.assertEquals(fetched.getName(), created.getName(), "User name should match.");
    }

    @Test(groups = "api")
    public void updateUser_returns200AndUpdatedAt() {
        UserPayload payload = UserPayload.builder()
                .name("morpheus")
                .job("zion resident")
                .build();

        UserPayload created = createUser("neo", "chosen one");

        Response response = given()
                .body(payload)
                .when()
                .put(UserEndpoints.userById(created.getId()));

        APIUtil.assertStatusCode(response, 200);

        UserPayload updated = response.as(UserPayload.class);
        Assert.assertEquals(updated.getName(), payload.getName(), "Updated name should match payload.");
        Assert.assertEquals(updated.getJob(), payload.getJob(), "Updated job should match payload.");
        APIUtil.assertNotBlank(updated.getUpdatedAt(), "updatedAt timestamp should be present.");
    }

    @Test(groups = "api")
    public void deleteUser_returns204() {
        UserPayload created = createUser("smith", "agent");

        Response response = given()
                .when()
                .delete(UserEndpoints.userById(created.getId()));

        APIUtil.assertStatusCode(response, 204);
        Assert.assertTrue(response.getBody().asString().isBlank(), "Delete response body should be empty.");

        Response getAfterDelete = given()
                .when()
                .get(UserEndpoints.userById(created.getId()));
        APIUtil.assertStatusCode(getAfterDelete, 404);
    }

    private UserPayload createUser(String name, String job) {
        UserPayload payload = UserPayload.builder()
                .name(name)
                .job(job)
                .build();

        Response response = given()
                .body(payload)
                .when()
                .post(UserEndpoints.USERS);

        APIUtil.assertStatusCode(response, 201);
        return response.as(UserPayload.class);
    }
}
