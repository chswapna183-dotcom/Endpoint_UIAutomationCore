package com.chswapna183_dotcom.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.response.Response;
import org.testng.Assert;

public final class APIUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private APIUtil() {
    }

    public static void assertStatusCode(Response response, int expectedStatusCode) {
        Assert.assertNotNull(response, "Response must not be null.");
        Assert.assertEquals(
                response.getStatusCode(),
                expectedStatusCode,
                "Unexpected status code. Body: " + response.getBody().asString()
        );
    }

    public static void assertNotBlank(String value, String message) {
        Assert.assertTrue(value != null && !value.trim().isEmpty(), message);
    }

    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return GSON.toJson(obj);
        }
    }
}

