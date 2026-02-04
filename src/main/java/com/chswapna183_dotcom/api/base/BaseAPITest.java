package com.chswapna183_dotcom.api.base;

import com.chswapna183_dotcom.api.client.APIClient;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

public abstract class BaseAPITest {

    @BeforeClass(alwaysRun = true)
    public void apiSetup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected RequestSpecification given() {
        return APIClient.request();
    }
}

