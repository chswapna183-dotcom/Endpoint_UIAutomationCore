package com.chswapna183_dotcom.api.client;

import com.chswapna183_dotcom.config.ConfigLoader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class APIClient {
    private APIClient() {
    }

    private static RequestSpecification buildRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigLoader.getApiBaseUrl())
                .setBasePath(ConfigLoader.getApiBasePath())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    public static RequestSpecification request() {
        return RestAssured.given().spec(buildRequestSpec());
    }
}
